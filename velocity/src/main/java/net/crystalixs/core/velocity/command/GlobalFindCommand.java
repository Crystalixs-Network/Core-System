package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.velocity.parser.PlayerParser.playerParser;

public class GlobalFindCommand extends VelocityCommand {

    public GlobalFindCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("global-find", "gfind")
                .commandDescription(RichDescription.translatable("command.global-find.description"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.global-find"))
                .required("player", playerParser(), RichDescription.translatable("command.global-find.player.description"))
                .handler(context -> {
                    CommandSource source = context.sender().plattformSender();
                    Player target = context.get("player");

                    var currentServer = target.getCurrentServer().map(ServerConnection::getServer);
                    if (currentServer.isEmpty()) {
                        source.sendMessage(translatable("command.global-find.error-not-connected"));
                        return;
                    }
                    String serverName = currentServer.get().getServerInfo().getName();

                    source.sendMessage(translatable("command.global-find.success",
                            component("name", text(target.getUsername())),
                            component("server", text(serverName))));
                })
        );
    }
}
