package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.item.HomeMenuButtonItem;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public final class HomeEditGui implements HomeGui {

    private final HomeModel model;
    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;

    public HomeEditGui(HomeModel model, HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory) {
        this.model = model;
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
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
                        "x x x x b x x x x"
                )
                .addIngredient('x', new SimpleItem(itemFactory.locked()))
                .addIngredient('i', new SimpleItem(itemFactory.icon(model)))
                .addIngredient('b', new HomeMenuButtonItem(guiFactory, itemFactory.back(), HomeMenuButtonItem.Action.BACK_TO_LIST))
                .build();
    }
}
