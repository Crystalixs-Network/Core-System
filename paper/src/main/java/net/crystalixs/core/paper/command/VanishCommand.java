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
                    vanishService.toggleVanish(player);
                    boolean vanished = vanishService.isVanished(player);
                    player.sendMessage(vanished ? "§aDu bist jetzt im Vanish." : "§cDu bist nicht mehr im Vanish.");
                }));

        commandManager.command(commandManager.commandBuilder("vanish", "v")
                .commandDescription(RichDescription.translatable("command.vanish.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.vanish.other"))
                .required("player", playerParser())
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");

                    vanishService.toggleVanish(target);
                    boolean vanished = vanishService.isVanished(target);

                    sender.sendMessage(vanished
                            ? "§aSpieler ist jetzt im Vanish."
                            : "§cSpieler ist nicht mehr im Vanish.");

                    if (!sender.equals(target)) {
                        target.sendMessage(vanished
                                ? "§7Du bist jetzt im Vanish."
                                : "§7Du bist nicht mehr im Vanish.");
                    }
                }));
    }
}