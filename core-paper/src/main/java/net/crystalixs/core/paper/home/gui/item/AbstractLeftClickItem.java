package net.crystalixs.core.paper.home.gui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.impl.AbstractItem;

abstract class AbstractLeftClickItem extends AbstractItem {

    @Override
    public final void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        handleLeftClick(player, event);
    }

    protected abstract void handleLeftClick(@NotNull Player player, @NotNull InventoryClickEvent event);
}
