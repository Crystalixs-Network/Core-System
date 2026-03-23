package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class TeleportHomeGuiItem extends AbstractItem {

    private final HomeModel home;
    private final HomeService service;
    private final HomeGuiItemFactory factory;

    public TeleportHomeGuiItem(HomeModel home, HomeService service, HomeGuiItemFactory factory) {
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

        try {
            service.teleport(player, home.name());
            player.closeInventory();
            player.sendMessage(translatable("command.home.teleport.success").arguments(component("name", text(home.name()))));

        } catch (HomeException exception) {
            String key = switch (exception.error()) {
                case HOME_NOT_FOUND -> "command.home.teleport.error.not-found";
                case HOME_WORLD_NOT_AVAILABLE -> "command.home.teleport.error.world-unavailable";
                default -> "command.home.teleport.error.failed";
            };
            player.sendMessage(translatable(key));
        }
    }
}
