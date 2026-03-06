package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.velocity.parser.PlayerParser.playerParser;

public class GlobalFindCommand extends VelocityCommand {

    public GlobalFindCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("global-find", "gfind")
                .commandDescription(RichDescription.translatable("command.global-find.description.main"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.global-find"))
                .required("player", playerParser(), RichDescription.translatable("command.global-find.description.player"))
                .handler(context -> {
                    CommandSource source = context.sender().plattformSender();
                    Player target = context.get("player");

                    var currentServer = target.getCurrentServer().map(ServerConnection::getServer);
                    if (currentServer.isEmpty()) {
                        source.sendMessage(translatable("command.global-find.error.not-connected"));
                        return;
                    }
                    String serverName = currentServer.get().getServerInfo().getName();

                    Component joinButton = translatable("command.global-find.teleport.button")
                            .clickEvent(ClickEvent.runCommand("/global-teleport " + target.getUsername()))
                            .hoverEvent(HoverEvent.showText(translatable("command.global-find.teleport.hover")));

                    Component serverButton = text(serverName)
                            .clickEvent(ClickEvent.runCommand("/global-teleport " + target.getUsername()))
                            .hoverEvent(HoverEvent.showText(translatable("command.global-find.teleport.hover")));

                    source.sendMessage(translatable("command.global-find.success",
                            component("name", text(target.getUsername())),
                            component("server", serverButton),
                            component("button", joinButton)
                    ));
                })
        );
    }
}
