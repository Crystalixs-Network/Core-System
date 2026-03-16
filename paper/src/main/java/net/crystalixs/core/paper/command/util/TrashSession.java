package net.crystalixs.core.paper.command.util;

import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.window.Window;

import static net.kyori.adventure.text.Component.translatable;

public final class TrashSession {

    private final ReferencingInventory reference;

    public TrashSession(int size) {
        Inventory backingInventory = Bukkit.createInventory(null, size);
        this.reference = ReferencingInventory.fromContents(backingInventory);
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
}
