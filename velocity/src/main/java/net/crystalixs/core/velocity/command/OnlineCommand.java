package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import kotlin._Assertions;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.velocity.parser.ServerParser.serverParser;

public class OnlineCommand extends VelocityCommand {

    public OnlineCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("online")
                .commandDescription(RichDescription.translatable(""))
                .senderType(VelocityPlayerCommandSource.class)
                .permission(Permission.of("core.command.online"))
                .required("server", serverParser(), RichDescription.translatable(""))
                .handler(context -> {
                    Player source = context.sender().player();
                    RegisteredServer requested = context.get("server");

                    requested.ping().whenComplete(((ping, throwable) -> {
                        if (ping == null || throwable != null) {
                            source.sendMessage(text("Server offline. exception"));
                            return;
                        }

                        source.sendMessage(text("Server online"));
                    }));
                })
        );
    }
}
