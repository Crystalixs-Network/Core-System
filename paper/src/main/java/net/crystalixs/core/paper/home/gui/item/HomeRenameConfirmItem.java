package net.crystalixs.core.paper.home.gui.item;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.home.HomeRenameExecutor;
import net.crystalixs.core.paper.home.HomeRenameExecutor.Outcome;
import net.crystalixs.core.paper.home.HomeRenameFailureHandler;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class HomeRenameConfirmItem extends AbstractItem {

    private final String oldName;
    private final Supplier<String> newNameSupplier;
    private final HomeRenameExecutor executor;
    private final HomeRenameFailureHandler failureHandler;
    private final StructuredLogger logger;

    public HomeRenameConfirmItem(String oldName, Supplier<String> newNameSupplier, HomeRenameExecutor executor, HomeRenameFailureHandler failureHandler, StructuredLogger logger) {
        this.oldName = oldName;
        this.newNameSupplier = newNameSupplier;
        this.executor = executor;
        this.failureHandler = failureHandler;
        this.logger = logger;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName(new AdventureComponentWrapper(empty()));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (!clickType.isLeftClick()) return;

        Outcome outcome = executor.execute(player.getUniqueId(), oldName, newNameSupplier.get());
        if (outcome instanceof HomeRenameExecutor.Success(String normalizedOldName, HomeModel renamed)) {
            player.sendMessage(translatable("command.home.rename.success").arguments(
                    component("old_name", text(normalizedOldName)),
                    component("new_name", text(renamed.name()))));

            player.closeInventory();
            return;
        }
        failureHandler.handle(player, (HomeRenameExecutor.Failure) outcome, logger);
    }
}
