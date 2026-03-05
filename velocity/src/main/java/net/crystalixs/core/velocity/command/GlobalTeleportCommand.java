package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.velocity.parser.PlayerParser.playerParser;

public class GlobalTeleportCommand extends VelocityCommand {

    public GlobalTeleportCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("global-teleport", "gtp")
                .commandDescription(RichDescription.translatable("command.global-teleport.description"))
                .senderType(VelocityPlayerCommandSource.class)
                .permission(Permission.of("core.command.global-teleport"))
                .required("player", playerParser(), RichDescription.translatable("command.global-teleport.player.description"))
                .handler(context -> {
                    Player source = context.sender().player();
                    Player target = context.get("player");

                    var connection = target.getCurrentServer().map(ServerConnection::getServer);
                    if (connection.isEmpty()) {
                        source.sendMessage(translatable("command.global-teleport.error-not-connected"));
                        return;
                    }

                    source.sendMessage(translatable("command.global-teleport.success",
                            component("name", text(target.getUsername())),
                            component("server", text(connection.get().getServerInfo().getName()))
                    ));
                    source.createConnectionRequest(connection.get());
                })
        );
    }
}
