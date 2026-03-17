package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.PrivateMessageService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static net.kyori.adventure.text.minimessage.translation.Argument.string;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public class ReplyCommand extends PaperCommand {

    private final PrivateMessageService service;

    public ReplyCommand(CorePlugin plugin, PrivateMessageService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("reply", "r")
                .commandDescription(RichDescription.translatable("command.reply.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.reply"))
                .required("message", greedyStringParser(), RichDescription.translatable("command.reply.description.message"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    String message = context.get("message");

                    Player receiver = service.lastRecipient(sender);
                    if (receiver == null || !receiver.isOnline()) {
                        sender.sendMessage(translatable("command.reply.error.no-target"));
                        return;
                    }

                    sender.sendMessage(translatable("command.message.sent").arguments(
                            component("player", receiver.name()),
                            string("message", message)));
                    receiver.sendMessage(translatable("command.message.received").arguments(
                            component("player", sender.name()),
                            string("message", message)));

                    service.rememberConversation(sender, receiver);
                }));
    }
}
