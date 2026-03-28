package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.ignore.PlayerIgnoreService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class IgnoreCommand extends PaperCommand {

    private final PlayerIgnoreService service;

    public IgnoreCommand(CorePlugin plugin, PlayerIgnoreService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("ignore")
                .commandDescription(RichDescription.translatable("command.ignore.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.ignore"))
                .required("player", playerParser())
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");

                    if (sender.getUniqueId().equals(target.getUniqueId())) {
                        sender.sendMessage(translatable("command.ignore.error.self"));
                        return;
                    }
                    if (service.isIgnoring(sender, target)) {
                        sender.sendMessage(translatable("command.ignore.error.already-ignored"));
                        return;
                    }

                    service.ignorePlayer(sender, target);
                    sender.sendMessage(translatable("command.ignore.success").arguments(component("name", target.name())));
                }));
    }
}
