package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.TeleportRequestService;
import net.crystalixs.core.paper.command.util.TeleportRequestService.RequestType;
import net.crystalixs.core.paper.ignore.PlayerIgnoreService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class TeleportRequestHereCommand extends PaperCommand {

    private final TeleportRequestService requestService;
    private final PlayerIgnoreService ignoreService;

    public TeleportRequestHereCommand(CorePlugin plugin, TeleportRequestService requestService, PlayerIgnoreService ignoreService) {
        super(plugin);
        this.requestService = requestService;
        this.ignoreService = ignoreService;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("tpahere")
                .commandDescription(RichDescription.translatable("command.tpahere.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.tpahere"))
                .required("player", playerParser(), RichDescription.translatable("command.tpahere.description.player"))
                .handler(context -> {
                    Player requester = context.sender().player();
                    Player target = context.get("player");

                    if (requester.getUniqueId().equals(target.getUniqueId())) {
                        requester.sendMessage(translatable("command.tpa.error.self"));
                        return;
                    }
                    if (ignoreService.isIgnoring(target, requester)) {
                        requester.sendMessage(translatable("command.ignore.error.ignored"));
                        return;
                    }

                    requestService.create(requester, target, RequestType.TPA_HERE);

                    requester.sendMessage(translatable("command.tpahere.sent").arguments(component("player", target.name())));
                    target.sendMessage(translatable("command.tpahere.received").arguments(component("player", requester.name())));
                }));
    }
}
