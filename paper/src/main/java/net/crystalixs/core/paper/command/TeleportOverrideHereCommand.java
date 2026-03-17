package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class TeleportOverrideHereCommand extends PaperCommand {

    public TeleportOverrideHereCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("tpohere")
                .commandDescription(RichDescription.translatable("command.tpohere.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.tpohere"))
                .required("player", playerParser(), RichDescription.translatable("command.tpohere.description.player"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");

                    if (sender.getUniqueId().equals(target.getUniqueId())) {
                        sender.sendMessage(translatable("command.tpohere.error.self"));
                        return;
                    }

                    target.teleport(sender.getLocation());
                    sender.sendMessage(translatable("command.tpohere.success.sender").arguments(component("player", target.name())));
                    target.sendMessage(translatable("command.tpohere.success.target").arguments(component("player", sender.name())));
                }));
    }
}
