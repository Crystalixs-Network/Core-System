package net.crystalixs.core.paper.enchantment.util;

import net.crystalixs.core.paper.util.ItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.translatable;

public final class StorageSession {

    private static final NamespacedKey STORAGE_EXTRA_KEY = new NamespacedKey("core", "storage_extra");
    private static final int BASE_SHULKER_SLOTS = 27;

    private final Player player;
    private final ShulkerBox shulker;
    private final int level;

    public StorageSession(Player player, ShulkerBox shulker, int level) {
        this.player = player;
        this.shulker = shulker;
        this.level = level;
    }

    public void open() {
        int rows = Math.clamp(level, 1, 3) + 3;
        Inventory backing = Bukkit.createInventory(null, rows * 9);
        loadContent(backing);

        Gui gui = Gui.normal()
                .setStructure(buildStructure(rows))
                .addIngredient('x', ReferencingInventory.fromContents(backing))
                .build();

        Window.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(translatable("container.shulkerBox")))
                .addCloseHandler(() -> saveContent(backing))
                .setGui(gui)
                .open(player);
    }

    private String[] buildStructure(int rows) {
        String[] structure = new String[rows];
        Arrays.fill(structure, "x x x x x x x x x");
        return structure;
    }

    private void loadContent(Inventory inventory) {
        ItemStack[] baseContent = shulker.getInventory().getContents();
        for (int slot = 0; slot < BASE_SHULKER_SLOTS; slot++) {
            inventory.setItem(slot, baseContent[slot]);
        }

        PersistentDataContainer container = shulker.getPersistentDataContainer();
        byte[] payload = container.get(STORAGE_EXTRA_KEY, PersistentDataType.BYTE_ARRAY);
        if (payload == null || payload.length == 0) return;

        ItemStack[] extraContent = ItemSerializer.deserialize(payload);
        if (extraContent.length == 0) return;

        int start = BASE_SHULKER_SLOTS;
        int maxExtra = Math.max(0, inventory.getSize() - start);
        for (int i = 0; i < Math.min(extraContent.length, maxExtra); i++) {
            inventory.setItem(start + i, extraContent[i]);
        }
    }

    private void saveContent(Inventory inventory) {
        ItemStack[] current = inventory.getContents();
        ItemStack[] baseContent = new ItemStack[BASE_SHULKER_SLOTS];
        System.arraycopy(current, 0, baseContent, 0, Math.min(BASE_SHULKER_SLOTS, current.length));
        shulker.getInventory().setContents(baseContent);

        PersistentDataContainer container = shulker.getPersistentDataContainer();
        if (current.length > BASE_SHULKER_SLOTS) {
            ItemStack[] extraContent = new ItemStack[current.length - BASE_SHULKER_SLOTS];
            System.arraycopy(current, BASE_SHULKER_SLOTS, extraContent, 0, extraContent.length);

            byte[] payload = ItemSerializer.serialize(extraContent);
            container.set(STORAGE_EXTRA_KEY, PersistentDataType.BYTE_ARRAY, payload);

        } else {
            container.remove(STORAGE_EXTRA_KEY);
        }
        shulker.update(true, false);
    }
}
