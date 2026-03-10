package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.economy.Balance;
import net.crystalixs.core.paper.economy.EconomyException;
import net.crystalixs.core.paper.economy.EconomyService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.tag.resolver.Formatter.number;
import static net.kyori.adventure.text.minimessage.translation.Argument.tagResolver;

public class BalanceCommand extends PaperCommand {

    private final EconomyService service;
    private final StructuredLogger logger;

    public BalanceCommand(CorePlugin plugin, EconomyService service) {
        super(plugin);
        this.service = service;
        this.logger = commandLogger("balance");
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("balance", "bal")
                .commandDescription(RichDescription.translatable("command.balance.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.balance"))
                .handler(context -> {
                    Player player = context.sender().player();

                    try {
                        Balance balance = service.getBalance(player.getUniqueId());
                        player.sendMessage(translatable("command.balance.success").arguments(
                                tagResolver(number("coins", balance.coins())),
                                tagResolver(number("gems", balance.gems()))));

                    } catch (EconomyException exception) {
                        logger.warn("balance command failed", LogMetadata
                                .event("command.balance.failed")
                                .and(LogMetadata.Key.ACTOR, player.getName())
                                .and(LogMetadata.Key.SUBJECT, player.getUniqueId().toString())
                                .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);

                        player.sendMessage(translatable("error.player-load"));
                    }
                })
        );
    }
}
