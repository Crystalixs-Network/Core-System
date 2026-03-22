package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.home.HomeError;
import net.crystalixs.core.paper.home.HomeException;
import net.crystalixs.core.paper.home.HomeService;
import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public final class HomeCommand extends PaperCommand {

    private final HomeService service;
    private final StructuredLogger logger;

    public HomeCommand(CorePlugin plugin, HomeService service) {
        super(plugin);
        this.service = service;
        this.logger = plugin.commandLogger("home");
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("home")
                .commandDescription(RichDescription.translatable("command.home.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.home"))
                .literal("create", RichDescription.translatable("command.home.description.create"))
                .required("name", stringParser(), RichDescription.translatable("command.home.description.name"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    String name = context.get("name");

                    try {
                        HomeModel created = service.create(sender.getUniqueId(), name, sender.getLocation());
                        sender.sendMessage(translatable("command.home.create.success").arguments(component("name", text(created.name()))));

                    } catch (HomeException exception) {
                        String key = switch (exception.error()) {
                            case INVALID_NAME -> "command.home.create.error.invalid-name";
                            case INVALID_POSITION -> "command.home.create.error.invalid-position";
                            case HOME_ALREADY_EXISTS -> "command.home.create.error.already-exists";
                            case PLAYER_CREATION_FAILED -> "error.player-load";
                            case HOME_CREATION_FAILED -> "command.home.create.error.persistence";
                        };
                        if (exception.error() == HomeError.PLAYER_CREATION_FAILED || exception.error() == HomeError.HOME_CREATION_FAILED) {
                            logger.warn("home creation failed", LogMetadata
                                    .event("command.home.create.failed")
                                    .and(LogMetadata.Key.ACTOR, sender.getName())
                                    .and(LogMetadata.Key.SUBJECT, sender.getUniqueId().toString())
                                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                        }
                        sender.sendMessage(translatable(key));
                    }
                }));
    }
}
