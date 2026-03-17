package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.EnderchestViewService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class EnderchestCommand extends PaperCommand {

    private final EnderchestViewService service;

    public EnderchestCommand(CorePlugin plugin) {
        super(plugin);
        this.service = new EnderchestViewService();
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("enderchest", "ec")
                .commandDescription(RichDescription.translatable("command.enderchest.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.enderchest"))
                .handler(this::openSelf));

        commandManager.command(commandManager.commandBuilder("enderchest", "ec")
                .commandDescription(RichDescription.translatable("command.enderchest.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.enderchest.other"))
                .required("player", playerParser(), RichDescription.translatable("command.enderchest.description.player"))
                .handler(this::openOther));
    }

    private void openSelf(CommandContext<PaperPlayerCommandSource> context) {
        Player sender = context.sender().player();
        service.open(sender, sender, true);
    }

    private void openOther(CommandContext<PaperPlayerCommandSource> context) {
        Player sender = context.sender().player();
        Player target = context.get("player");
        boolean canInteract = sender.hasPermission("core.bypass.enderchest.other");

        service.open(sender, target, canInteract);
    }
}
