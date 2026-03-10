package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.economy.EconomyError;
import net.crystalixs.core.paper.economy.EconomyException;
import net.crystalixs.core.paper.economy.EconomyService;
import net.crystalixs.core.persistence.model.Currency;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;
import static net.kyori.adventure.text.minimessage.translation.Argument.numeric;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.LongParser.longParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;
import static org.incendo.cloud.suggestion.SuggestionProvider.noSuggestions;

public class EconomyCommand extends PaperCommand {

    private final EconomyService service;
    private final StructuredLogger logger;

    public EconomyCommand(CorePlugin plugin, EconomyService service) {
        super(plugin);
        this.service = service;
        this.logger = commandLogger("economy");
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("economy", "eco")
                .commandDescription(RichDescription.translatable("command.economy.description.main"))
                .senderType(PaperCommandSource.class)
                .permission(Permission.of("core.command.economy"))
                .literal("give", RichDescription.translatable("command.economy.description.give"))
                .required("player", playerParser(), RichDescription.translatable("command.economy.description.player"))
                .required("currency", stringParser(),
                        RichDescription.translatable("command.economy.description.currency"),
                        SuggestionProvider.suggestingStrings("coins", "gems"))
                .required("amount", longParser(),
                        RichDescription.translatable("command.economy.description.amount"),
                        noSuggestions())
                .handler(context -> {
                    CommandSender sender = context.sender().plattformSender();
                    Player target = context.get("player");
                    long amount = context.get("amount");
                    Currency currency = parseCurrency(context.get("currency"));

                    if (currency == null) {
                        sender.sendMessage("error.invalid-currency");
                        return;
                    }

                    try {
                        service.addCurrency(target.getUniqueId(), currency, amount);
                        sender.sendMessage(translatable("command.economy.success.give").arguments(
                                component("player", target.name()),
                                numeric("amount", amount),
                                component("currency", text(currency.name()))));

                    } catch (EconomyException exception) {
                        if (exception.error() == EconomyError.PLAYER_CREATION_FAILED) {
                            logger.warn("eco give command failed", LogMetadata
                                    .event("command.eco.give.failed")
                                    .and(LogMetadata.Key.ACTOR, sender.getName())
                                    .and(LogMetadata.Key.SUBJECT, target.getUniqueId().toString())
                                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
                        }

                        String key = switch (exception.error()) {
                            case PLAYER_CREATION_FAILED -> "error.player-load";
                            case INVALID_AMOUNT -> "error.invalid-amount";
                            default -> null;
                        };

                        if (key != null) {
                            sender.sendMessage(translatable(key));
                        }
                    }
                }));
    }

    private Currency parseCurrency(String value) {
        return switch (value.toLowerCase()) {
            case "coins" -> Currency.COINS;
            case "gems" -> Currency.GEMS;
            default -> null;
        };
    }
}
