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

public class TeleportOverrideCommand extends PaperCommand {

    public TeleportOverrideCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("tpo")
                .commandDescription(RichDescription.translatable("command.tpo.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.tpo"))
                .required("player", playerParser(), RichDescription.translatable("command.tpo.description.player"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");

                    if (sender.getUniqueId().equals(target.getUniqueId())) {
                        sender.sendMessage(translatable("command.tpo.error.self"));
                        return;
                    }

                    sender.teleport(target.getLocation());
                    sender.sendMessage(translatable("command.tpo.success").arguments(component("player", target.name())));
                }));
    }
}
