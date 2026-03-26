package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.gui.HomeListGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public final class HomeMenuButtonItem extends AbstractItem {

    public enum Action { BACK_TO_LIST, DELETE }

    private final HomeGuiFactory guiFactory;

    private final ItemProvider provider;
    private final Action action;

    public HomeMenuButtonItem(HomeGuiFactory guiFactory, ItemProvider provider, Action action) {
        this.guiFactory = guiFactory;
        this.provider = provider;
        this.action = action;
    }

    @Override
    public ItemProvider getItemProvider() {
        return provider;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;

        switch (action) {
            case BACK_TO_LIST -> guiFactory.open(player, HomeListGui.class, null);
        }
    }
}
