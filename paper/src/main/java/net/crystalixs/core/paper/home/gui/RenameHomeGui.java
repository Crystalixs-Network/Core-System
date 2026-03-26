package net.crystalixs.core.paper.home.gui;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.home.*;
import net.crystalixs.core.persistence.model.HomeModel;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.AnvilWindow;

import static net.kyori.adventure.text.Component.translatable;

public final class RenameHomeGui {

    private final HomeService service;
    private final HomeModel model;

    private final StructuredLogger logger;
    private final HomeRenameInputValidator renameInputValidator;
    private final HomeRenameErrorMapper renameErrorMapper;

    public RenameHomeGui(CorePlugin plugin, HomeService service, HomeModel model) {
        this.service = service;
        this.model = model;
        this.logger = plugin.componentLogger("home", "gui", "rename");
        this.renameInputValidator = new HomeRenameInputValidator();
        this.renameErrorMapper = new HomeRenameErrorMapper();
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

                    try {
                        player.sendRawMessage(oldNameInput + " -> " + renameText);

                    } catch (HomeException exception) {
                        if (exception.error() == HomeError.HOME_RENAME_FAILED || exception.error() == HomeError.PLAYER_CREATION_FAILED) {
                            logger.warn("home rename failed", LogMetadata
                                    .event("command.home.rename.failed")
                                    .and(LogMetadata.Key.ACTOR, player.getName())
                                    .and(LogMetadata.Key.SUBJECT, player.getUniqueId().toString())
                                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                        }
                        player.sendMessage(renameErrorMapper.toMessage(exception, oldNameInput, renameText));
                    }
                })
                .open(player);
    }

}
