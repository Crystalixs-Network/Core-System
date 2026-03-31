package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.paper.home.gui.item.HomeEditGuiDeleteItem;
import net.crystalixs.core.paper.home.gui.item.HomeEditGuiRenameItem;
import net.crystalixs.core.paper.home.gui.item.HomeEditGuiReturnToListItem;
import net.crystalixs.core.paper.home.gui.item.HomeEditGuiSelectIconItem;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public final class HomeEditGui implements HomeGui {

    private final HomeService service;
    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;
    private final HomeModel model;

    public HomeEditGui(HomeService service, HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory, HomeModel model) {
        this.service = service;
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
        this.model = model;
    }

    @Override
    public String titleKey() {
        return "command.home.ui.edit.title";
    }

    @Override
    public Gui buildGui(Player player) {
        return Gui.normal()
                .setStructure(
                        "x x x x i x x x x",
                        "x x x x x x x x x",
                        "x x s x r x d x x",
                        "x x x x x x x x x",
                        "b x x x x x x x x"
                )
                .addIngredient('x', new SimpleItem(itemFactory.locked()))
                .addIngredient('i', new SimpleItem(itemFactory.icon(model)))
                .addIngredient('b', new HomeEditGuiReturnToListItem(itemFactory, guiFactory))
                .addIngredient('d', new HomeEditGuiDeleteItem(service, itemFactory, model))
                .addIngredient('r', new HomeEditGuiRenameItem(itemFactory, guiFactory, model))
                .addIngredient('s', new HomeEditGuiSelectIconItem(itemFactory, guiFactory, model))
                .build();
    }
}
