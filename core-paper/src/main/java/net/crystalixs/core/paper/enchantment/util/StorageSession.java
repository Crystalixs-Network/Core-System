package net.crystalixs.core.paper.enchantment.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.translatable;

public final class StorageSession {

    private final Player player;
    private final int level;

    public StorageSession(Player player, int level) {
        this.player = player;
        this.level = level;
    }

    public void open() {
        int rows = Math.clamp(level, 1, 3) + 3;
        Inventory backing = Bukkit.createInventory(null, rows * 9);
        ReferencingInventory inventory = ReferencingInventory.fromContents(backing);

        Gui gui = Gui.normal()
                .setStructure(buildStructure(rows))
                .addIngredient('x', inventory)
                .build();

        Window.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(translatable("container.shulkerBox")))
                .setGui(gui)
                .open(player);
    }

    private String[] buildStructure(int rows) {
        String[] structure = new String[rows];
        Arrays.fill(structure, "x x x x x x x x x");
        return structure;
    }
}
