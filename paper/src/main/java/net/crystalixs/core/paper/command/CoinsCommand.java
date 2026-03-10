package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.economy.EconomyException;
import net.crystalixs.core.paper.economy.EconomyService;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class CoinsCommand extends PaperCommand {

    private final StructuredLogger logger;
    private final EconomyService service;


    public CoinsCommand(CorePlugin plugin, StructuredLogger logger, EconomyService service) {
        super(plugin);
        this.logger = logger;
        this.service = service;
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
                        context.sender().sendMessage(translatable("command.coins.success").arguments(component("amount", text(coins))));

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
