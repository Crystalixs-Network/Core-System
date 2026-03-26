package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;

public class HomeEditGuiReturnToListItem extends AbstractLeftClickItem {

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;

    public HomeEditGuiReturnToListItem(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.back();
    }

    @Override
    protected void handleLeftClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        guiFactory.openList(player);
    }
}
