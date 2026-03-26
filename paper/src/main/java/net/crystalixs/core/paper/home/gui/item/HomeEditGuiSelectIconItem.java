package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public final class HomeEditGuiSelectIconItem extends AbstractItem {

    private final HomeGuiItemFactory itemFactory;

    public HomeEditGuiSelectIconItem(HomeGuiItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.selectIcon();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
    }
}
