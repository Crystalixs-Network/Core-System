package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public final class TeleportHomeGuiItem extends AbstractItem {

    private final Player player;
    private final HomeModel home;
    private final HomeService service;
    private final HomeGuiItemFactory factory;

    public TeleportHomeGuiItem(Player player, HomeModel home, HomeService service, HomeGuiItemFactory factory) {
        this.player = player;
        this.home = home;
        this.service = service;
        this.factory = factory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return factory.icon(home);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        service.teleport(player, home.name());
    }
}
