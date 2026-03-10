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
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.LongParser.longParser;

public class PayCommand extends PaperCommand {

    private final EconomyService service;
    private final StructuredLogger logger;

    public PayCommand(CorePlugin plugin, EconomyService service) {
        super(plugin);
        this.service = service;
        this.logger = commandLogger("pay");
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("pay")
                .commandDescription(RichDescription.translatable("command.pay.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission("core.command.pay")
                .required("player", playerParser(), RichDescription.translatable("command.pay.description.player"))
                .required("amount", longParser(1L), RichDescription.translatable("command.pay.description.amount"))
                .handler(context -> {
                    Player sender = context.sender().player();
                    Player target = context.get("player");
                    long amount = context.get("amount");

                    try {
                        service.transferCoins(sender.getUniqueId(), target.getUniqueId(), amount);

                        sender.sendMessage(translatable("command.pay.success.actor").arguments(
                                component("player", target.name()),
                                component("amount", text(amount))));
                        target.sendMessage(translatable("command.pay.success.receiver").arguments(
                                component("player", sender.name()),
                                component("amount", text(amount))));

                    } catch (EconomyException exception) {
                        logger.warn("pay command failed", LogMetadata.event("command.pay.failed")
                                .and(LogMetadata.Key.ACTOR, sender.getName())
                                .and(LogMetadata.Key.SUBJECT, sender.getUniqueId().toString())
                                .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);

                        String key = switch (exception.error()) {
                            case SELF_TRANSFER -> "command.pay.error.self-transfer";
                            case INVALID_AMOUNT -> "command.pay.error.invalid-amount";
                            case INSUFFICIENT_FUNDS -> "command.pay.error.insufficient-funds";
                            default -> null;
                        };

                        if (key != null) {
                            sender.sendMessage(translatable(key));
                        }
                    }
                }));
    }
}
