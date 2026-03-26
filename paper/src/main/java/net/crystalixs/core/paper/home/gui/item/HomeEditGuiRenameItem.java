package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.RenameHomeGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class HomeEditGuiRenameItem extends AbstractItem {

    private final HomeGuiItemFactory itemFactory;

    public HomeEditGuiRenameItem(HomeGuiItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.rename();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        new RenameHomeGui().open(player);
    }
}
