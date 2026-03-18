package net.crystalixs.core.velocity.command;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.help.UnifiedHelpService;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public class HelpCommand extends VelocityCommand {

    private final UnifiedHelpService service;

    public HelpCommand(CorePlugin plugin, UnifiedHelpService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("help", "?")
                .commandDescription(translatable("command.help.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .optional("query", greedyStringParser(), translatable("command.help.description.query"),
                        SuggestionProvider.blocking(((context, input) -> commandManager.createHelpHandler()
                                .queryRootIndex(context.sender())
                                .entries()
                                .stream()
                                .map(CommandEntry::syntax)
                                .map(Suggestion::suggestion)
                                .collect(Collectors.toList()))))
                .handler(context -> {
                    String query = context.getOrDefault("query", "");
                    var entries = service.query(context.sender(), query);

                    context.sender().sendMessage(text(" "));
                    context.sender().plattformSender().sendMessage(text("Help (" + entries.size() + "):"));

                    for (var entry : entries.stream().limit(40).toList()) {
                        context.sender().plattformSender().sendMessage(text(
                                "[" + entry.sourceLabel() + "] " + entry.syntax() + " - " + entry.description()
                        ));
                    }
                }));
    }
}
