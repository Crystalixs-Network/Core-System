package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class HomeEditGuiRenameItem extends AbstractItem {

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;
    private final HomeModel model;

    public HomeEditGuiRenameItem(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory, HomeModel model) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
        this.model = model;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.rename();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        guiFactory.openRename(player, model);
    }
}
