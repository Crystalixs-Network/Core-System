package net.crystalixs.core.velocity.command;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public class HelpCommand extends VelocityCommand {

    private final BackendHelpCatalogCache cache;

    public HelpCommand(CorePlugin plugin, BackendHelpCatalogCache cache) {
        super(plugin);
        this.cache = cache;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        MinecraftHelp<VelocityCommandSource> help = MinecraftHelp.<VelocityCommandSource>builder()
                .commandManager(commandManager)
                .audienceProvider(VelocityCommandSource::plattformSender)
                .commandPrefix("/help")
                .build();

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
                    help.queryCommands(query, context.sender());

                    // POC-Zusatz: Backend-Commands darunter anhängen
                    sendBackendSection(context.sender(), query);
                }));
    }

    private void sendBackendSection(VelocityCommandSource sender, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase();

        sender.plattformSender().sendMessage(text(" "));
        sender.plattformSender().sendMessage(text("Backend-Commands:"));

        cache.all().stream()
                .sorted(Comparator.comparing(NetworkHelpCatalog::sourceId, String.CASE_INSENSITIVE_ORDER))
                .forEach(catalog -> {
                    var filtered = catalog.entries().stream()
                            .filter(entry ->
                                    needle.isEmpty()
                                    || entry.command().toLowerCase().contains(needle)
                                    || entry.syntax().toLowerCase().contains(needle)
                            )
                            .limit(8)
                            .toList();

                    if (filtered.isEmpty()) return;

                    sender.plattformSender().sendMessage(text("- " + catalog.sourceId() + ":"));
                    filtered.forEach(entry -> sender.plattformSender().sendMessage(text(" " + entry.syntax() + " - " + entry.description())));
                });
    }
}
