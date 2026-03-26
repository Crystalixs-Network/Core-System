package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;

public final class HomeEditGuiSelectIconItem extends AbstractLeftClickItem {

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;
    private final HomeModel model;

    public HomeEditGuiSelectIconItem(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory, HomeModel model) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
        this.model = model;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.selectIcon();
    }

    @Override
    protected void handleLeftClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        guiFactory.openIconSelection(player, model);
    }
}
