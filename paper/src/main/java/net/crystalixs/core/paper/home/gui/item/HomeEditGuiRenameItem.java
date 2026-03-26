package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.paper.home.gui.RenameHomeGui;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class HomeEditGuiRenameItem extends AbstractItem {

    private final CorePlugin plugin;
    private final HomeService service;
    private final HomeGuiItemFactory itemFactory;
    private final HomeModel model;

    public HomeEditGuiRenameItem(CorePlugin plugin, HomeService service, HomeGuiItemFactory itemFactory, HomeModel model) {
        this.plugin = plugin;
        this.service = service;
        this.itemFactory = itemFactory;
        this.model = model;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemFactory.rename();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        new RenameHomeGui(plugin, service, itemFactory, model).open(player);
    }
}
