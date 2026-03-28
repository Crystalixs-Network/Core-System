package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.TeleportRequestService;
import net.crystalixs.core.paper.command.util.TeleportRequestService.RequestType;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class TeleportRequestCommand extends PaperCommand {

    private final TeleportRequestService service;

    public TeleportRequestCommand(CorePlugin plugin, TeleportRequestService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("tpa")
                .commandDescription(RichDescription.translatable("command.tpa.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.tpa"))
                .required("player", playerParser(), RichDescription.translatable("command.tpa.description.player"))
                .handler(context -> {
                    Player requester = context.sender().player();
                    Player target = context.get("player");

                    if (requester.getUniqueId().equals(target.getUniqueId())) {
                        requester.sendMessage(translatable("command.tpa.error.self"));
                        return;
                    }

                    service.create(requester, target, RequestType.TPA);

                    requester.sendMessage(translatable("command.tpa.sent").arguments(component("player", target.name())));
                    target.sendMessage(translatable("command.tpa.received").arguments(component("player", requester.name())));
                }));
    }
}
