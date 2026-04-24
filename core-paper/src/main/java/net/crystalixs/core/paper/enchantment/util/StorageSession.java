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

    private static final NamespacedKey SESSION_KEY = new NamespacedKey("core", "storage_session");

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
        PersistentDataContainer container = shulker.getPersistentDataContainer();
        byte[] payload = container.get(SESSION_KEY, PersistentDataType.BYTE_ARRAY);
        if (payload == null || payload.length == 0) return;

        ItemStack[] content = ItemSerializer.deserialize(payload);
        if (content.length == 0) return;

        ItemStack[] fitted = new ItemStack[inventory.getSize()];
        System.arraycopy(content, 0, fitted, 0, Math.min(content.length, fitted.length));
        inventory.setContents(fitted);
    }

    private void saveContent(Inventory inventory) {
        if (inventory.getContents().length == 0) return;

        PersistentDataContainer container = shulker.getPersistentDataContainer();
        byte[] payload = ItemSerializer.serialize(inventory.getContents());
        container.set(SESSION_KEY, PersistentDataType.BYTE_ARRAY, payload);
    }
}
