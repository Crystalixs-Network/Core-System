package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.config.ExternalConfigModificationException;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.config.Maintenance;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;

public class MaintenanceCommand extends VelocityCommand {

    private final StructuredLogger logger;
    private final VelocityConfigUpdater updater;
    private final ProxyServer proxy;
    private final MiniMessage miniMessage;

    public MaintenanceCommand(CorePlugin plugin, VelocityConfigUpdater updater, ProxyServer proxy, MiniMessage miniMessage) {
        super(plugin);
        this.logger = commandLogger("maintenance");
        this.updater = updater;
        this.proxy = proxy;
        this.miniMessage = miniMessage;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("maintenance")
                .commandDescription(RichDescription.translatable("command.maintenance.description.main"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.maintenance"))
                .required("state", booleanParser(), RichDescription.translatable("command.maintenance.description.state"))
                .handler(context -> {
                    final CommandSource source = context.sender().plattformSender();
                    final Maintenance maintenance = updater.current().maintenance();

                    boolean state = context.get("state");

                    if (state && maintenance.isEnabled()) {
                        source.sendMessage(translatable("command.maintenance.error.already_enabled"));
                        return;
                    }
                    if (!state && !maintenance.isEnabled()) {
                        source.sendMessage(translatable("command.maintenance.error.already_disabled"));
                        return;
                    }

                    if (!toggleMaintenance(source, state)) {
                        return;
                    }
                    kickUnauthorized();
                })
        );
    }

    private boolean toggleMaintenance(CommandSource source, boolean state) {
        try {
            updater.setMaintenance(state);
        } catch (ExternalConfigModificationException exception) {
            logger.warn("maintenance toggle failed due to external config change", LogMetadata.event("maintenance.toggle_external_change")
                    .and(LogMetadata.Key.ACTOR, actorName(source))
                    .and(LogMetadata.Key.COMMAND, "maintenance")
                    .and(LogMetadata.Key.STATE, state), exception);
            source.sendMessage(translatable("command.core.reload.error.external-change"));
            return false;
        } catch (IOException exception) {
            logger.warn("maintenance toggle failed due to io error", LogMetadata.event("maintenance.toggle_io_failed")
                    .and(LogMetadata.Key.ACTOR, actorName(source))
                    .and(LogMetadata.Key.COMMAND, "maintenance")
                    .and(LogMetadata.Key.STATE, state), exception);
            source.sendMessage(translatable("command.core.reload.error.io"));
            return false;
        }
        logger.info(state ? "maintenance enabled via command" : "maintenance disabled via command",
                LogMetadata.event(state ? "maintenance.enabled" : "maintenance.disabled")
                        .and(LogMetadata.Key.ACTOR, actorName(source))
                        .and(LogMetadata.Key.COMMAND, "maintenance")
                        .and(LogMetadata.Key.STATE, state));
        source.sendMessage(translatable(state ? "command.maintenance.enabled" : "command.maintenance.disabled"));
        return true;
    }

    private void kickUnauthorized() {
        for (Player player : proxy.getAllPlayers()) {
            if (player.hasPermission("core.bypass.maintenance"))
                continue;

            player.disconnect(updater.current().maintenance().screen().construct(miniMessage));
        }
    }
}
