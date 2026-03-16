package net.crystalixs.core.paper.command.util;

import net.crystalixs.core.paper.CorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class TrashSession {

    private final Map<Integer, Integer> slotTaskIds = new ConcurrentHashMap<>();
    private final Map<Integer, ItemStack> trackedItems = new ConcurrentHashMap<>();
    private final Map<Integer, Long> slotExpireAtMillis = new ConcurrentHashMap<>();

    private final CorePlugin plugin;
    private final long deleteTicks;

    private final Inventory backingInventory;
    private final ReferencingInventory reference;

    private final NamespacedKey key;
    private final int loreTickerTaskId;
    private volatile boolean internalLoreUpdate = false;
    private volatile Locale locale;

    public TrashSession(JavaPlugin plugin, int size, long deleteTicks) {
        this.plugin = (CorePlugin) plugin;
        this.deleteTicks = deleteTicks;
        this.locale = this.plugin.defaultTranslationLocale();

        this.backingInventory = Bukkit.createInventory(null, size);
        this.reference = ReferencingInventory.fromContents(backingInventory);

        this.key = new NamespacedKey(plugin, "trash_timer");
        this.loreTickerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tickLore, 20L, 20L);

        this.reference.setPostUpdateHandler(this::onPostUpdate);
    }

    public void open(Player viewer) {
        Locale requested = viewer.locale();
        this.locale = plugin.resolveTranslationLocale(requested);

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
        Bukkit.getScheduler().cancelTask(loreTickerTaskId);
        slotTaskIds.values().forEach(Bukkit.getScheduler()::cancelTask);
        slotTaskIds.clear();
        trackedItems.clear();
        slotExpireAtMillis.clear();
    }

    private void onPostUpdate(ItemPostUpdateEvent event) {
        if (!internalLoreUpdate) {
            reconcileTimers();
        }

        if (event.isRemove() || event.isSwap()) {
            UpdateReason reason = event.getUpdateReason();
            if (reason instanceof PlayerUpdateReason playerReason) {
                cleanupMarkedItems(playerReason.getPlayer());
            }
        }
    }

    private void reconcileTimers() {
        for (int slot = 0; slot < backingInventory.getSize(); slot++) {
            ItemStack current = backingInventory.getItem(slot);

            if (isEmpty(current)) {
                cancelTimer(slot);
                trackedItems.remove(slot);
                continue;
            }

            ItemStack normalizedCurrent = stripTimerLore(current.clone());
            ItemStack previous = trackedItems.get(slot);

            if (previous == null) {
                trackedItems.put(slot, normalizedCurrent);
                scheduleTimer(slot);
                continue;
            }

            if (!isSame(previous, normalizedCurrent)) {
                cancelTimer(slot);
                trackedItems.put(slot, normalizedCurrent);
                scheduleTimer(slot);
            }
        }
    }

    private void tickLore() {
        long now = System.currentTimeMillis();

        internalLoreUpdate = true;
        try {
            for (int slot = 0; slot < backingInventory.getSize(); slot++) {
                ItemStack current = backingInventory.getItem(slot);
                if (isEmpty(current)) continue;

                Long expireAt = slotExpireAtMillis.get(slot);
                if (expireAt == null) continue;

                long secondsLeft = Math.max(0L, (expireAt - now + 999L) / 1_000L);
                ItemStack withLore = withTimerLore(current.clone(), secondsLeft);
                backingInventory.setItem(slot, withLore);
            }
        } finally {
            internalLoreUpdate = false;
        }
    }

    private void scheduleTimer(int slot) {
        cancelTimer(slot);
        slotExpireAtMillis.put(slot, System.currentTimeMillis() + 60_000L);

        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ItemStack current = backingInventory.getItem(slot);
            if (!isEmpty(current)) {
                backingInventory.clear(slot);
            }

            slotTaskIds.remove(slot);
            trackedItems.remove(slot);
            slotExpireAtMillis.remove(slot);

        }, deleteTicks);

        slotTaskIds.put(slot, taskId);
    }

    private void cancelTimer(int slot) {
        Integer taskId = slotTaskIds.remove(slot);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        slotExpireAtMillis.remove(slot);
    }

    private void cleanupMarkedItems(Player player) {
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            ItemStack cleaned = stripTimerLoreIfMarked(itemStack);
            if (cleaned != itemStack) {
                player.getInventory().setItem(slot, cleaned);
            }
        }

        ItemStack offhand = player.getInventory().getItemInOffHand();
        ItemStack cleanedOffhand = stripTimerLoreIfMarked(offhand);
        if (cleanedOffhand != offhand) {
            player.getInventory().setItemInOffHand(cleanedOffhand);
        }

        ItemStack cursor = player.getItemOnCursor();
        ItemStack cleanedCursor = stripTimerLoreIfMarked(cursor);
        if (cleanedCursor != cursor) {
            player.setItemOnCursor(cleanedCursor);
        }
    }

    private ItemStack stripTimerLoreIfMarked(ItemStack itemStack) {
        if (isEmpty(itemStack)) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return itemStack;

        return stripTimerLore(itemStack);
    }

    private ItemStack stripTimerLore(ItemStack itemStack) {
        if (isEmpty(itemStack)) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return itemStack;

        ItemStack copy = itemStack.clone();
        ItemMeta copyMeta = copy.getItemMeta();
        if (copyMeta == null) return itemStack;

        List<Component> lore = copyMeta.lore();
        if (lore != null && !lore.isEmpty()) {
            List<Component> cleaned = new ArrayList<>(lore);
            cleaned.removeLast();

            if (!cleaned.isEmpty() && PlainTextComponentSerializer.plainText().serialize(cleaned.getLast()).isBlank()) {
                cleaned.removeLast();
            }
            copyMeta.lore(cleaned.isEmpty() ? null : cleaned);
        }

        copyMeta.getPersistentDataContainer().remove(key);
        copy.setItemMeta(copyMeta);
        return copy;
    }

    private ItemStack withTimerLore(ItemStack original, long secondsLeft) {
        ItemStack copy = stripTimerLore(original);
        ItemMeta meta = copy.getItemMeta();
        if (meta == null) return copy;

        List<Component> lore = meta.lore();
        List<Component> newLore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);

        newLore.add(empty().decoration(TextDecoration.ITALIC, false));
        newLore.add(renderTimer(secondsLeft).decoration(TextDecoration.ITALIC, false));

        meta.lore(newLore);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        copy.setItemMeta(meta);

        return copy;
    }

    private Component renderTimer(long secondsLeft) {
        return GlobalTranslator.render(translatable("command.trash.timer").arguments(
                component("seconds", text(secondsLeft))
        ), locale != null ? locale : plugin.defaultTranslationLocale());
    }

    private boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir();
    }

    private boolean isSame(ItemStack first, ItemStack second) {
        return first.isSimilar(second) && first.getAmount() == second.getAmount();
    }
}
