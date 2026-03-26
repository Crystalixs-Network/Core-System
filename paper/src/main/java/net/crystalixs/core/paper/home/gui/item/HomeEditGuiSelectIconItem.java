package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.SelectIconGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public final class HomeEditGuiSelectIconItem extends AbstractItem {

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;

    public HomeEditGuiSelectIconItem(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.selectIcon();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        new SelectIconGui(itemFactory, guiFactory).open(player);
    }
}
