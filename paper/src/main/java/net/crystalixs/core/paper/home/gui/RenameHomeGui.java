package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.HomeError;
import net.crystalixs.core.paper.home.HomeException;
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

    public RenameHomeGui(CorePlugin plugin, HomeModel model) {
        this.model = model;
        this.logger = plugin.componentLogger("home", "gui", "rename");
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
                    String oldName = model.name();

                    try {
                        player.sendRawMessage(oldName + " --> " + renameText);

                    } catch (HomeException exception) {
                        String key = switch (exception.error()) {
                            case INVALID_NAME -> "command.home.create.error.invalid-name";
                            case HOME_NOT_FOUND -> "command.home.rename.error.not-found";
                            case HOME_ALREADY_EXISTS -> "command.home.rename.error.already-exists";
                            case PLAYER_CREATION_FAILED -> "error.player-load";
                            default -> "command.home.rename.error.persistence";
                        };

                        if (exception.error() == HomeError.HOME_RENAME_FAILED || exception.error() == HomeError.PLAYER_CREATION_FAILED) {
                            logger.warn("home rename failed", LogMetadata
                                    .event("command.home.rename.failed")
                                    .and(LogMetadata.Key.ACTOR, player.getName())
                                    .and(LogMetadata.Key.SUBJECT, player.getUniqueId().toString())
                                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                        }

                        if (exception.error() == HomeError.HOME_NOT_FOUND) {
                            player.sendMessage(translatable(key).arguments(component("old_name", text(oldName.trim()))));
                        } else if (exception.error() == HomeError.HOME_ALREADY_EXISTS) {
                            player.sendMessage(translatable(key).arguments(component("new_name", text(renameText.trim()))));
                        } else {
                            player.sendMessage(translatable(key));
                        }
                    }
                })
                .open(player);
    }

}
