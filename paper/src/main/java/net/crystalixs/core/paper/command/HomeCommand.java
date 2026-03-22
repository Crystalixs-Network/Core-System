package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.home.HomeError;
import net.crystalixs.core.paper.home.HomeException;
import net.crystalixs.core.paper.home.HomeLimitResolver;
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
import static net.kyori.adventure.text.minimessage.translation.Argument.numeric;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public final class HomeCommand extends PaperCommand {

    private static final Permission PERMISSION = Permission.of("core.command.home");

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
                .permission(PERMISSION)
                .literal("create", RichDescription.translatable("command.home.description.create"))
                .required("name", stringParser(), RichDescription.translatable("command.home.description.name"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    String name = context.get("name");

                    int limit = HomeLimitResolver.resolve(sender);
                    int current = service.count(sender.getUniqueId());
                    if (current >= limit) {
                        sender.sendMessage(translatable("command.home.create.error.limit-reached").arguments(numeric("limit", limit)));
                        return;
                    }

                    try {
                        HomeModel created = service.create(sender.getUniqueId(), name, sender.getLocation());
                        sender.sendMessage(translatable("command.home.create.success").arguments(component("name", text(created.name()))));

                    } catch (HomeException exception) {
                        String key = switch (exception.error()) {
                            case INVALID_NAME -> "command.home.create.error.invalid-name";
                            case INVALID_POSITION -> "command.home.create.error.invalid-position";
                            case HOME_ALREADY_EXISTS -> "command.home.create.error.already-exists";
                            case PLAYER_CREATION_FAILED -> "error.player-load";
                            case HOME_LIMIT_REACHED -> "command.home.create.error.limit-reached";
                            default -> "command.home.create.error.persistence";
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

        commandManager.command(commandManager.commandBuilder("home")
                .commandDescription(RichDescription.translatable("command.home.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(PERMISSION)
                .literal("delete", RichDescription.translatable("command.home.description.delete"))
                .required("name", stringParser(), RichDescription.translatable("command.home.description.name"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    String name = context.get("name");

                    try {
                        service.delete(sender.getUniqueId(), name);
                        sender.sendMessage(translatable("command.home.delete.success").arguments(component("name", text(name))));

                    } catch (HomeException exception) {
                        String key = switch (exception.error()) {
                            case INVALID_NAME -> "command.home.create.error.invalid-name";
                            case HOME_NOT_FOUND -> "command.home.delete.error.not-found";
                            case PLAYER_CREATION_FAILED -> "error.player-load";
                            default -> "command.home.create.error.persistence";
                        };
                        if (exception.error() == HomeError.PLAYER_CREATION_FAILED || exception.error() == HomeError.HOME_DELETION_FAILED) {
                            logger.warn("home delete failed", LogMetadata
                                    .event("command.home.delete.failed")
                                    .and(LogMetadata.Key.ACTOR, sender.getName())
                                    .and(LogMetadata.Key.SUBJECT, sender.getUniqueId().toString())
                                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                        }
                        sender.sendMessage(translatable(key));
                    }
                }));

        commandManager.command(commandManager.commandBuilder("home")
                .commandDescription(RichDescription.translatable("command.home.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(PERMISSION)
                .literal("rename", RichDescription.translatable("command.home.description.rename"))
                .required("old-name", stringParser(), RichDescription.translatable("command.home.description.old-name"))
                .required("new-name", stringParser(), RichDescription.translatable("command.home.description.new-name"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    String oldName = context.get("old-name");
                    String newName = context.get("new-name");

                    try {
                        HomeModel renamed = service.rename(sender.getUniqueId(), oldName, newName);
                        sender.sendMessage(translatable("command.home.rename.success").arguments(
                                component("old_name", text(oldName)),
                                component("new_name", text(renamed.name()))));

                        logger.info("home renamed", LogMetadata
                                .event("command.home.rename.success")
                                .and(LogMetadata.Key.ACTOR, sender.getName())
                                .and(LogMetadata.Key.SUBJECT, sender.getUniqueId().toString())
                                .and(LogMetadata.Key.DESCRIPTION, oldName + " → " + renamed.name()));

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
                                    .and(LogMetadata.Key.ACTOR, sender.getName())
                                    .and(LogMetadata.Key.SUBJECT, sender.getUniqueId().toString())
                                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                        }

                        if (exception.error() == HomeError.HOME_NOT_FOUND) {
                            sender.sendMessage(translatable(key).arguments(component("old_name", text(oldName.trim()))));
                        } else if (exception.error() == HomeError.HOME_ALREADY_EXISTS) {
                            sender.sendMessage(translatable(key).arguments(component("new_name", text(newName.trim()))));
                        } else {
                            sender.sendMessage(translatable(key));
                        }
                    }
                }));

        commandManager.command(commandManager.commandBuilder("home")
                .commandDescription(RichDescription.translatable("command.home.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(PERMISSION)
                .literal("update", RichDescription.translatable("command.home.description.update"))
                .required("name", stringParser(), RichDescription.translatable("command.home.description.name"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    String name = context.get("name");

                    HomeModel updated = service.updatePosition(sender.getUniqueId(), name, sender.getLocation());
                    sender.sendMessage(translatable("command.home.update.success").arguments(component("name", text(updated.name()))));
                }));
    }
}
