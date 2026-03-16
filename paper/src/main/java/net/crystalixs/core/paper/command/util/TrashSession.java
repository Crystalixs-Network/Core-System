package net.crystalixs.core.paper.command.util;

import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.window.Window;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.translatable;

public final class TrashSession {

    private final Map<Integer, Integer> slotTaskIds = new ConcurrentHashMap<>();
    private final Map<Integer, ItemStack> trackedItems = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final long deleteTicks;

    private final Inventory backingInventory;
    private final ReferencingInventory reference;

    public TrashSession(JavaPlugin plugin, int size, long deleteTicks) {
        this.plugin = plugin;
        this.deleteTicks = deleteTicks;

        this.backingInventory = Bukkit.createInventory(null, size);
        this.reference = ReferencingInventory.fromContents(backingInventory);

        this.reference.setPostUpdateHandler(event -> reconcileTimers());
    }

    public void open(Player viewer) {
        Gui gui = Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x"
                )
                .addIngredient('x', reference)
                .build();

        var trashcanTitle = translatable("command.trash.title");
        var renderedTitle = GlobalTranslator.render(trashcanTitle, viewer.locale());

        Window.single()
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .setViewer(viewer)
                .setGui(gui)
                .open(viewer);
    }

    public void shutdown() {
        slotTaskIds.values().forEach(Bukkit.getScheduler()::cancelTask);
        slotTaskIds.clear();
        trackedItems.clear();
    }

    private void reconcileTimers() {
        for (int slot = 0; slot < backingInventory.getSize(); slot++) {
            ItemStack current = backingInventory.getItem(slot);

            if (isEmpty(current)) {
                cancelTimer(slot);
                trackedItems.remove(slot);
                continue;
            }

            ItemStack previous = trackedItems.get(slot);
            if (previous == null) {
                trackedItems.put(slot, current.clone());
                scheduleTimer(slot);
                continue;
            }

            if (!isSame(previous, current)) {
                cancelTimer(slot);
                trackedItems.put(slot, current.clone());
                scheduleTimer(slot);
            }
        }
    }

    private boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType().isAir();
    }

    private boolean isSame(ItemStack first, ItemStack second) {
        return first.isSimilar(second) && first.getAmount() == second.getAmount();
    }

    private void scheduleTimer(int slot) {
        cancelTimer(slot);

        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ItemStack current = backingInventory.getItem(slot);
            if (current != null && !current.getType().isAir()) {
                backingInventory.clear(slot);
            }

            slotTaskIds.remove(slot);
            trackedItems.remove(slot);
        }, deleteTicks);

        slotTaskIds.put(slot, taskId);
    }

    private void cancelTimer(int slot) {
        Integer taskId = slotTaskIds.remove(slot);
        if (taskId != null) Bukkit.getScheduler().cancelTask(taskId);
    }
}
