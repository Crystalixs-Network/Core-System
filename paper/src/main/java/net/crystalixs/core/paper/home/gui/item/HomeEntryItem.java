package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeException;
import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.paper.home.gui.HomeEditGui;
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

public final class HomeEntryItem extends AbstractItem {

    private final HomeService service;
    private final HomeModel home;
    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;

    public HomeEntryItem(HomeService service, HomeModel home, HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory) {
        this.service = service;
        this.home = home;
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.icon(home);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isRightClick()) {
            guiFactory.open(player, HomeEditGui.class, home);
            return;
        }

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
