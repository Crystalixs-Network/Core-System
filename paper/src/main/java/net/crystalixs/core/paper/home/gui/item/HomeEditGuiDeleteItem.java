package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class HomeEditGuiDeleteItem extends AbstractLeftClickItem {

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
    protected void handleLeftClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        service.delete(player.getUniqueId(), model.name());

        player.closeInventory();
        player.sendMessage(translatable("command.home.delete.success").arguments(component("name", text(model.name()))));
    }
}
