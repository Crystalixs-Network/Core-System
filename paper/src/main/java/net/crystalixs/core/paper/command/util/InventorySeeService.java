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

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class InventorySeeService {

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;

    public InventorySeeService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target, boolean canModify) {
        Session session = sessions.computeIfAbsent(target.getUniqueId(), uuid -> Session.create(target));

        if (!canModify) {
            session.armor().setPreUpdateHandler(event -> event.setCancelled(true));
            session.offhand().setPreUpdateHandler(event -> event.setCancelled(true));
            session.hotbar().setPreUpdateHandler(event -> event.setCancelled(true));
            session.storage().setPreUpdateHandler(event -> event.setCancelled(true));

        } else {
            session.armor().setPreUpdateHandler(null);
            session.offhand().setPreUpdateHandler(null);
            session.hotbar().setPreUpdateHandler(null);
            session.storage().setPreUpdateHandler(null);
        }

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
                .addIngredient('a', session.armor())
                .addIngredient('o', session.offhand())
                .addIngredient('x', session.storage())
                .addIngredient('h', session.hotbar())
                .addIngredient('#', divider)
                .build();

        var titleComponent = translatable("command.invsee.title").arguments(component("player", target.name()));
        var renderedTitle = GlobalTranslator.render(titleComponent, viewer.locale());

        Window.single()
                .setViewer(viewer)
                .setGui(gui)
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .open(viewer);
    }

    private record Session(ReferencingInventory armor, ReferencingInventory offhand, ReferencingInventory hotbar, ReferencingInventory storage) {
        static Session create(Player target) {
            PlayerInventory inventory = target.getInventory();

            var armor = ofSection(inventory, 39, 38, 37, 36);
            var offhand = ofSection(inventory, 40);
            var hotbar = ofSection(inventory, 0, 1, 2, 3, 4, 5, 6, 7, 8);
            var storage = ofSection(inventory, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);

            Runnable refreshAll = () -> {
                armor.notifyWindows();
                offhand.notifyWindows();
                hotbar.notifyWindows();
                storage.notifyWindows();
            };

            armor.setPostUpdateHandler(event -> refreshAll.run());
            offhand.setPostUpdateHandler(event -> refreshAll.run());
            hotbar.setPostUpdateHandler(event -> refreshAll.run());
            storage.setPostUpdateHandler(event -> refreshAll.run());

            return new Session(armor, offhand, hotbar, storage);
        }

        private static ReferencingInventory ofSection(PlayerInventory inventory, int... slots) {
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
                    (inv, index, item) -> inv.setItem(slots[index], item == null ? null : item.clone()));
        }
    }
}
