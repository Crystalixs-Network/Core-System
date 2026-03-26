package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.home.HomeException;
import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.home.HomeGuiItemFactory;
import net.crystalixs.core.paper.home.gui.SelectIconGui;
import net.crystalixs.core.paper.home.logging.HomeLogEvent;
import net.crystalixs.core.paper.home.logging.StructuredHomeLog;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static net.kyori.adventure.text.Component.translatable;

public class HomeIconChoiceItem extends AbstractItem {

    private final HomeGuiItemFactory itemFactory;
    private final HomeGuiFactory guiFactory;
    private final HomeModel model;
    private final Material icon;
    private final StructuredHomeLog homeLog;

    public HomeIconChoiceItem(HomeGuiItemFactory itemFactory, HomeGuiFactory guiFactory, HomeModel model, Material icon, StructuredLogger logger) {
        this.itemFactory = itemFactory;
        this.guiFactory = guiFactory;
        this.model = model;
        this.icon = icon;
        this.homeLog = new StructuredHomeLog(logger);
    }

    @Override
    public ItemProvider getItemProvider() {
        boolean isSelected = model.icon().equalsIgnoreCase(icon.toString());
        return isSelected ? itemFactory.selectedChoice(icon) : itemFactory.choice(icon);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;
        try {
            HomeModel updated = guiFactory.service().updateIcon(player.getUniqueId(), model.name(), icon.name());
            homeLog.info(HomeLogEvent.UPDATE_ICON_SUCCESS, player, model.name() + " -> " + updated.icon());
            new SelectIconGui(itemFactory, guiFactory, updated).open(player);

        } catch (HomeException exception) {
            homeLog.warn(HomeLogEvent.UPDATE_ICON_FAILED, player, model.name() + " -> " + icon.name() + ", error=" + exception.error().name(), exception);
            player.sendMessage(translatable("command.home.icon.update.error"));
        }
    }
}
