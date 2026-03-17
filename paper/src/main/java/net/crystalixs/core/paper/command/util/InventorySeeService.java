package net.crystalixs.core.paper.command.util;

import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class InventorySeeService {

    private final JavaPlugin plugin;
    private final Map<UUID, TargetSessions> sessions = new ConcurrentHashMap<>();

    public InventorySeeService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target, boolean canModify) {
        TargetSessions targetSessions = sessions.computeIfAbsent(target.getUniqueId(), id -> createSessions(target));
        Session session = canModify ? targetSessions.modify : targetSessions.readOnly;
        session.viewerCount.incrementAndGet();

        ItemProvider divider = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(new AdventureComponentWrapper(empty()));
        Gui gui = Gui.normal()
                .setStructure(
                        "a a a a # o # # #",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "# # # # # # # # #",
                        "h h h h h h h h h"
                )
                .addIngredient('a', session.armor)
                .addIngredient('o', session.offhand)
                .addIngredient('x', session.storage)
                .addIngredient('h', session.hotbar)
                .addIngredient('#', divider)
                .build();

        var titleComponent = translatable("command.invsee.title").arguments(component("player", target.name()));
        var renderedTitle = GlobalTranslator.render(titleComponent, viewer.locale());

        Window.single()
                .setViewer(viewer)
                .setGui(gui)
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .addCloseHandler(() -> {
                    if (session.viewerCount.decrementAndGet() <= 0) {
                        targetSessions.removeIfUnused(target.getUniqueId());
                    }
                })
                .open(viewer);
    }

    public void refresh(UUID targetId) {
        TargetSessions targetSessions = sessions.get(targetId);
        if (targetSessions == null) return;
        targetSessions.notifyAllWindows();
    }

    private TargetSessions createSessions(Player target) {
        PlayerInventory inventory = target.getInventory();

        Session modify = createSession(inventory);
        Session readOnly = createSession(inventory);
        readOnly.setReadOnly();

        TargetSessions targetSessions = new TargetSessions(modify, readOnly);
        modify.setPostUpdateNotify(targetSessions::notifyAllWindows);
        return targetSessions;
    }

    private Session createSession(PlayerInventory inventory) {
        var armor = ofSection(inventory, 39, 38, 37, 36);
        var offhand = ofSection(inventory, 40);
        var hotbar = ofSection(inventory, 0, 1, 2, 3, 4, 5, 6, 7, 8);
        var storage = ofSection(inventory, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
        return new Session(armor, offhand, hotbar, storage);
    }

    private ReferencingInventory ofSection(PlayerInventory inventory, int... slots) {
        return new ReferencingInventory(
                inventory,
                inv -> {
                    ItemStack[] result = new ItemStack[slots.length];
                    for (int i = 0; i < slots.length; i++) {
                        ItemStack item = inv.getItem(slots[i]);
                        result[i] = item == null ? null : item.clone();
                    }
                    return result;
                },
                (inv, index) -> {
                    ItemStack item = inv.getItem(slots[index]);
                    return item == null ? null : item.clone();
                },
                (inv, index, item) -> inv.setItem(slots[index], item == null ? null : item.clone())
        );
    }

    private static final class Session {
        private final ReferencingInventory armor;
        private final ReferencingInventory offhand;
        private final ReferencingInventory hotbar;
        private final ReferencingInventory storage;
        private final AtomicInteger viewerCount = new AtomicInteger(0);

        private Session(ReferencingInventory armor, ReferencingInventory offhand, ReferencingInventory hotbar, ReferencingInventory storage) {
            this.armor = armor;
            this.offhand = offhand;
            this.hotbar = hotbar;
            this.storage = storage;
        }

        private void setReadOnly() {
            armor.setPreUpdateHandler(event -> event.setCancelled(true));
            offhand.setPreUpdateHandler(event -> event.setCancelled(true));
            hotbar.setPreUpdateHandler(event -> event.setCancelled(true));
            storage.setPreUpdateHandler(event -> event.setCancelled(true));
        }

        private void setPostUpdateNotify(Runnable notify) {
            armor.setPostUpdateHandler(event -> notify.run());
            offhand.setPostUpdateHandler(event -> notify.run());
            hotbar.setPostUpdateHandler(event -> notify.run());
            storage.setPostUpdateHandler(event -> notify.run());
        }

        private void notifyAllWindows() {
            armor.notifyWindows();
            offhand.notifyWindows();
            hotbar.notifyWindows();
            storage.notifyWindows();
        }
    }

    private final class TargetSessions {
        private final Session modify;
        private final Session readOnly;

        private TargetSessions(Session modify, Session readOnly) {
            this.modify = modify;
            this.readOnly = readOnly;
        }

        private void notifyAllWindows() {
            modify.notifyAllWindows();
            readOnly.notifyAllWindows();
        }

        private void removeIfUnused(UUID targetId) {
            if (modify.viewerCount.get() <= 0 && readOnly.viewerCount.get() <= 0) {
                sessions.remove(targetId, this);
            }
        }
    }
}
