package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public class MessageCommand extends PaperCommand {

    public MessageCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("message", "msg")
                .commandDescription(RichDescription.translatable("command.message.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.message"))
                .required("player", playerParser(), RichDescription.translatable("command.message.description.player"))
                .required("message", greedyStringParser(), RichDescription.translatable("command.message.description.message"))
                .handler(context -> {
                }));
    }
}
