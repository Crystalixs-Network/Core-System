package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.PrivateMessageService;
import net.crystalixs.core.paper.ignore.PlayerIgnoreService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static net.kyori.adventure.text.minimessage.translation.Argument.string;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public final class MessageCommand extends PaperCommand {

    private final PrivateMessageService messageService;
    private final PlayerIgnoreService ignoreService;

    public MessageCommand(CorePlugin plugin, PrivateMessageService messageService, PlayerIgnoreService ignoreService) {
        super(plugin);
        this.messageService = messageService;
        this.ignoreService = ignoreService;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("message", "msg")
                .commandDescription(RichDescription.translatable("command.message.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.message"))
                .required("player", playerParser(), RichDescription.translatable("command.message.description.player"))
                .required("message", greedyStringParser(), RichDescription.translatable("command.message.description.message"))
                .handler(this::handleMessage));
    }

    private void handleMessage(CommandContext<PaperPlayerCommandSource> context) {
        Player sender = context.sender().player();
        Player receiver = context.get("player");
        String message = context.get("message");

        if (sender.getUniqueId().equals(receiver.getUniqueId())) {
            sender.sendMessage(translatable("command.message.error.self"));
            return;
        }
        if (ignoreService.isIgnoring(sender, receiver)) {
            sender.sendMessage(translatable("command.ignore.error.ignored"));
            return;
        }

        sender.sendMessage(translatable("command.message.sent").arguments(
                component("player", receiver.name()),
                string("message", message)));
        receiver.sendMessage(translatable("command.message.received").arguments(
                component("player", sender.name()),
                string("message", message)));

        messageService.rememberConversation(sender, receiver);
    }
}
