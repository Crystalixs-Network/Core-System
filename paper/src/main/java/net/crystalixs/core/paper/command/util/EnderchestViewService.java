package net.crystalixs.core.paper.command.util;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.window.Window;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class EnderchestViewService {

    private final StructuredLogger logger;

    public EnderchestViewService(CorePlugin plugin) {
        this.logger = plugin.commandLogger("enderchest");
    }

    public void open(Player viewer, Player target, boolean canInteract) {
        var reference = ReferencingInventory.fromContents(target.getEnderChest());

        if (!canInteract) {
            reference.setPreUpdateHandler(event -> event.setCancelled(true));
        } else {
            reference.setPostUpdateHandler(event -> logUpdate(event, target));
        }

        String translationKey = viewer.equals(target) ? "command.enderchest.view.self" : "command.enderchest.view.other";
        Component titleComponent = translatable(translationKey).arguments(component("player", target.name()));
        Component renderedTitle = GlobalTranslator.render(titleComponent, viewer.locale());

        Gui gui = Gui.normal()
                .setStructure(
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x"
                )
                .addIngredient('x', reference)
                .build();

        Window.single()
                .setGui(gui)
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .open(viewer);
    }

    private void logUpdate(ItemPostUpdateEvent event, Player chestOwner) {
        if (!(event.getUpdateReason() instanceof PlayerUpdateReason reason)) return;

        String action = event.isAdd() ? "add" : event.isRemove() ? "remove" : event.isSwap() ? "swap" : "update";
        logger.info("enderchest inventory updated", LogMetadata
                .event("command.enderchest.inventory.update")
                .and(LogMetadata.Key.ACTOR, reason.getPlayer().getName())
                .and(LogMetadata.Key.SUBJECT, chestOwner.getUniqueId().toString())
                .and(LogMetadata.Key.DESCRIPTION, "slot=" + event.getSlot() + ", action=" + action)
                .and(LogMetadata.Key.COMMAND, "enderchest"));
    }
}
