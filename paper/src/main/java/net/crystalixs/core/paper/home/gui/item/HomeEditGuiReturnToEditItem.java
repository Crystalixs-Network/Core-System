package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.HomeEditGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class HomeEditGuiReturnToEditItem extends AbstractItem {

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;

    public HomeEditGuiReturnToEditItem(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.back();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        guiFactory.open(player, HomeEditGui.class, null);
    }

}
