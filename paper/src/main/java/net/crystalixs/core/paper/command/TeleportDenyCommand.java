package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.TeleportRequestService;
import net.crystalixs.core.paper.command.util.TeleportRequestService.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public class TeleportDenyCommand extends PaperCommand {

    private final TeleportRequestService service;

    public TeleportDenyCommand(CorePlugin plugin, TeleportRequestService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("tpdeny")
                .commandDescription(RichDescription.translatable("command.tpdeny.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.tpa.deny"))
                .handler(context -> {
                    Player target = context.sender().player();
                    TeleportRequest request = service.consumeTarget(target);

                    if (request == null) {
                        target.sendMessage(translatable("command.tpdeny.error.none"));
                        return;
                    }

                    Player requester = Bukkit.getPlayer(request.requester());
                    if (requester == null || !requester.isOnline()) {
                        target.sendMessage(translatable("command.tpdeny.success.target.offline"));
                        return;
                    }
                    target.sendMessage(translatable("command.tpdeny.success.target").arguments(component("player", requester.name())));
                    requester.sendMessage(translatable("command.tpdeny.success.requester").arguments(component("player", target.name())));
                }));
    }
}
