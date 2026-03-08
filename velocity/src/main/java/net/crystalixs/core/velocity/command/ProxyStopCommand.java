package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;

public class ProxyStopCommand extends VelocityCommand {

    private final StructuredLogger logger;
    private final ProxyServer server;

    public ProxyStopCommand(CorePlugin plugin, ProxyServer server) {
        super(plugin);
        this.logger = plugin.logger().child("commands").child("proxy-stop");
        this.server = server;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("proxy-stop")
                .commandDescription(translatable("command.proxy-stop.description"))
                .senderType(VelocityPlayerCommandSource.class)
                .permission(Permission.of("core.command.proxy-stop"))
                .handler(context -> {
                    logger.info("proxy stop requested", LogMetadata
                            .event("proxy.stop.requested")
                            .and(LogMetadata.Key.ACTOR, actorName(context.sender().plattformSender()))
                            .and(LogMetadata.Key.COMMAND, "proxy-stop"));
                    server.shutdown();
                })
        );
    }
}
