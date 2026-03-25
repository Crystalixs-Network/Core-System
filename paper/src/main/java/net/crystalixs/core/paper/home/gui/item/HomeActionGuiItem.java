package net.crystalixs.core.paper.home.gui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.function.Consumer;

public final class HomeActionGuiItem extends AbstractItem {

    private final ItemProvider provider;
    private final Consumer<Player> action;

    public HomeActionGuiItem(ItemProvider provider, Consumer<Player> action) {
        this.provider = provider;
        this.action = action;
    }

    public ItemProvider provider() {
        return provider;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        action.accept(player);
    }
}
