package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.VanishService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class VanishCommand extends PaperCommand {

    private final VanishService vanishService;

    public VanishCommand(CorePlugin plugin, VanishService vanishService) {
        super(plugin);
        this.vanishService = vanishService;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("vanish", "v")
                .commandDescription(RichDescription.translatable("command.vanish.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.vanish"))
                .handler(context -> {
                    Player player = context.sender().player();

                    boolean isVanished = vanishService.toggleVanish(player);
                    player.sendMessage(translatable(isVanished ? "command.vanish.enter.self" : "command.vanish.leave.self"));
                }));

        commandManager.command(commandManager.commandBuilder("vanish", "v")
                .commandDescription(RichDescription.translatable("command.vanish.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.vanish.other"))
                .required("player", playerParser())
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");

                    boolean isVanished = vanishService.toggleVanish(target);
                    if (!sender.equals(target)) {
                        target.sendMessage(translatable(isVanished ? "command.vanish.enter.self" : "command.vanish.leave.self"));
                    }
                    sender.sendMessage(translatable(isVanished ? "command.vanish.enter.other" : "command.vanish.leave.other"));
                }));
    }
}