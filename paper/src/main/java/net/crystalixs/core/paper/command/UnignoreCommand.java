package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.setting.PlayerSettingService;
import net.crystalixs.core.paper.setting.SettingException;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

public final class UnignoreCommand extends PaperCommand {

    private final PlayerSettingService service;
    private final StructuredLogger logger;

    public UnignoreCommand(CorePlugin plugin, PlayerSettingService service) {
        super(plugin);
        this.service = service;
        this.logger = commandLogger("ignore");
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("unignore")
                .commandDescription(RichDescription.translatable("command.unignore.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.unignore"))
                .handler(context -> {
                    Player sender = context.sender().player();

                    try {
                        boolean isToggled = service.isIgnored(sender.getUniqueId());

                        if (!isToggled) {
                            sender.sendMessage(translatable("command.unignore.error.not-ignored"));
                            return;
                        }

                        service.updateIgnoreSetting(sender.getUniqueId(), true);
                        sender.sendMessage(translatable("command.unignore.success"));

                    } catch (SettingException exception) {
                        logger.warn("command.unignore.failed", LogMetadata
                                .event("command.unignore.failed")
                                .and(LogMetadata.Key.ACTOR, sender.getName())
                                .and(LogMetadata.Key.SUBJECT, sender.getUniqueId().toString())
                                .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);

                        sender.sendMessage(translatable("error.player-load"));
                    }
                }));
    }
}
