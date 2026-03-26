package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.HomeException;
import net.crystalixs.core.paper.home.HomeRenameExecutor;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.persistence.model.HomeModel;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.AnvilWindow;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class RenameHomeGui {

    private final HomeModel model;
    private final StructuredLogger logger;
    private final HomeRenameExecutor renameExecutor;

    public RenameHomeGui(CorePlugin plugin, HomeService service, HomeModel model) {
        this.model = model;
        this.logger = plugin.componentLogger("home", "gui", "rename");
        this.renameExecutor = new HomeRenameExecutor(service);
    }

    public void open(Player player) {
        var title = translatable("command.home.ui.edit.rename.title");
        var renderedTitle = GlobalTranslator.render(title, player.locale());

        Gui gui = Gui.normal()
                .setStructure("x x x")
                .addIngredient('x', new ItemBuilder(Material.AIR))
                .build();

        AnvilWindow.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(renderedTitle))
                .setGui(gui)
                .addRenameHandler(renameText -> {
                    String oldNameInput = model.name();

                    HomeRenameExecutor.Outcome outcome = renameExecutor.execute(player.getUniqueId(), oldNameInput, renameText);
                    if (outcome instanceof HomeRenameExecutor.Success(String oldName, HomeModel renamed)) {
                        player.sendMessage(translatable("command.home.rename.success").arguments(
                                component("old_name", text(oldName)),
                                component("new_name", text(renamed.name()))));
                        player.closeInventory();
                        return;
                    }

                    HomeRenameExecutor.Failure failure = (HomeRenameExecutor.Failure) outcome;
                    if (failure.shouldLogWarn()) {
                        HomeException exception = failure.exception();
                        logger.warn("home rename failed", LogMetadata
                                .event("command.home.rename.failed")
                                .and(LogMetadata.Key.ACTOR, player.getName())
                                .and(LogMetadata.Key.SUBJECT, player.getUniqueId().toString())
                                .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                    }
                    player.sendMessage(failure.message());
                })
                .open(player);
    }
}
