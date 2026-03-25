package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public final class HomeEditGui implements HomeGui {

    private final HomeModel model;
    private final HomeGuiItemFactory factory;

    HomeEditGui(HomeModel model, HomeGuiItemFactory factory) {
        this.model = model;
        this.factory = factory;
    }

    @Override
    public String titleKey() {
        return "command.home.ui.edit.title";
    }

    @Override
    public Gui buildGui(Player player) {
        return Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        "x x x x i x x x x",
                        "x x x x x x x x x"
                )
                .addIngredient('x', new SimpleItem(factory.locked()))
                .addIngredient('i', new SimpleItem(factory.icon(model)))
                .build();
    }
}
