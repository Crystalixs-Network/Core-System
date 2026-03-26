package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class HomeEditGuiDeleteItem extends AbstractItem {

    private final HomeService service;
    private final HomeGuiItemFactory factory;
    private final HomeModel model;

    public HomeEditGuiDeleteItem(HomeService service, HomeGuiItemFactory factory, HomeModel model) {
        this.service = service;
        this.factory = factory;
        this.model = model;
    }

    @Override
    public ItemProvider getItemProvider() {
        return factory.delete();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;

        service.delete(player.getUniqueId(), model.name());
    }
}
