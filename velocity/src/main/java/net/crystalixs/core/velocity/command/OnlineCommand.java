package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.velocity.parser.ServerParser.serverParser;

public class OnlineCommand extends VelocityCommand {

    public OnlineCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("online")
                .commandDescription(RichDescription.translatable("command.online.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .permission(Permission.of("core.command.online"))
                .required("server", serverParser(), RichDescription.translatable("command.online.description.server"))
                .handler(context -> {
                    Player source = context.sender().player();
                    RegisteredServer requested = context.get("server");

                    requested.ping().whenComplete(((ping, throwable) -> source.sendMessage(translatable("command.online.status").arguments(
                            Argument.component("server", text(requested.getServerInfo().getName())),
                            Argument.tagResolver(Formatter.booleanChoice("status", throwable == null || ping != null))
                    ))));
                })
        );
    }
}
