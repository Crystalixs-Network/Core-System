package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.economy.EconomyException;
import net.crystalixs.core.paper.economy.EconomyService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.translatable;

public final class CoinsCommand extends PaperCommand {

    private final EconomyService service;
    private final StructuredLogger logger;

    public CoinsCommand(CorePlugin plugin, EconomyService service) {
        super(plugin);
        this.service = service;
        this.logger = commandLogger("coins");
    }

    @Override
    public void registerTo(@NonNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("coins")
                .commandDescription(RichDescription.translatable("command.coins.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.coins"))
                .handler(context -> {
                    Player player = context.sender().player();

                    try {
                        long coins = service.getCoins(player.getUniqueId());
                        player.sendMessage(translatable("command.coins.success").arguments(number("amount", coins, player)));

                    } catch (EconomyException exception) {
                        logger.warn("coins command failed", LogMetadata.event("command.coins.failed")
                                .and(LogMetadata.Key.ACTOR, player.getName())
                                .and(LogMetadata.Key.SUBJECT, player.getUniqueId().toString())
                                .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);

                        player.sendMessage(translatable("error.player-load"));
                    }
                }));
    }
}
