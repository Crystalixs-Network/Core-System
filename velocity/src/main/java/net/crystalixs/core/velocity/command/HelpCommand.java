package net.crystalixs.core.velocity.command;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.help.StyledHelpRenderer;
import net.crystalixs.core.velocity.help.UnifiedHelpService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.help.HelpQuery;
import org.incendo.cloud.help.result.HelpQueryResult;
import org.incendo.cloud.help.result.IndexCommandResult;
import org.incendo.cloud.help.result.MultipleCommandResult;
import org.incendo.cloud.help.result.VerboseCommandResult;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.Iterator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public class HelpCommand extends VelocityCommand {

    private final UnifiedHelpService service;
    private CommandManager<VelocityCommandSource> commandManager;

    public HelpCommand(CorePlugin plugin, UnifiedHelpService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        this.commandManager = commandManager;

        commandManager.command(commandManager.commandBuilder("help", "?")
                .commandDescription(translatable("command.help.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .optional("query", greedyStringParser(), translatable("command.help.description.query"))
                .handler(context -> renderPage(context.sender(), context.getOrDefault("query", ""), 1)));

        commandManager.command(commandManager.commandBuilder("help-page")
                .commandDescription(translatable("command.help.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .required("page", integerParser(1), translatable("command.help.description.page"))
                .optional("query", greedyStringParser())
                .handler(context -> renderPage(context.sender(), context.getOrDefault("query", ""), context.get("page"))));
    }

    private void renderPage(VelocityCommandSource sender, String query, int requestedPage) {
        var all = service.query(sender, query);

        int pageSize = 8;
        int pages = Math.max(1, (int) Math.ceil((double) all.size() / pageSize));
        int page = Math.min(Math.max(1, requestedPage), pages);

        int from = Math.min((page - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        var pageEntries = all.subList(from, to);

        createRenderer(sender).render(query, page, pages, pageEntries).forEach(sender.plattformSender()::sendMessage);
    }

    private StyledHelpRenderer createRenderer(VelocityCommandSource sender) {
        return new StyledHelpRenderer(entry -> ClickEvent.callback(audience -> {
            if (entry.isProxyCommand()) {
                renderProxyDetails(sender, entry.detailsQuery());
                return;
            }
            renderBackendDetails(sender, entry.sourceLabel(), entry.detailsQuery());
        }));
    }

    private void renderProxyDetails(VelocityCommandSource sender, String detailsQuery) {
        String query = detailsQuery.startsWith("/") ? detailsQuery.substring(1) : detailsQuery;
        HelpQueryResult<VelocityCommandSource> result = commandManager.createHelpHandler().query(HelpQuery.of(sender, query));
        StyledHelpRenderer renderer = createRenderer(sender);

        switch (result) {
            case VerboseCommandResult<VelocityCommandSource> verbose -> {
                String commandSyntax = commandManager.commandSyntaxFormatter()
                        .apply(sender, verbose.entry().command().components(), null);

                sender.plattformSender().sendMessage(renderer.detailHeader());
                sender.plattformSender().sendMessage(renderer.detailQueryLine("/" + query));
                sender.plattformSender().sendMessage(renderer.detailLine("Command:", "/" + commandSyntax, true));
                sender.plattformSender().sendMessage(renderer.detailLine(
                        "Description:",
                        renderer.descriptionText(verbose.entry().command().commandDescription().verboseDescription()),
                        false
                ));

                if (verbose.entry().command().components().size() > 1) {
                    sender.plattformSender().sendMessage(renderer.detailLine("Arguments:", "", false));
                    Iterator<CommandComponent<VelocityCommandSource>> iterator = verbose.entry().command().components().iterator();
                    iterator.next();
                    int depth = 0;

                    while (iterator.hasNext()) {
                        CommandComponent<VelocityCommandSource> component = iterator.next();
                        String syntax = commandManager.commandSyntaxFormatter()
                                .apply(sender, Collections.singletonList(component), null);

                        Component line = text(" ", GRAY).append(renderer.syntaxComponent(syntax));
                        if (component.optional()) {
                            line = line.append(text(" (Optional)", YELLOW));
                        }

                        String description = renderer.descriptionText(component.description());
                        if (!description.isBlank() && !description.equals("-")) {
                            line = line.append(text(" - ", GRAY)).append(text(description, GRAY));
                        }

                        sender.plattformSender().sendMessage(renderer.nestedArgumentLine(depth, line));
                        depth++;
                    }
                }
                return;
            }
            case MultipleCommandResult<VelocityCommandSource> multiple -> {
                sender.plattformSender().sendMessage(renderer.detailHeader());
                sender.plattformSender().sendMessage(renderer.detailQueryLine("/" + query));
                sender.plattformSender().sendMessage(renderer.detailLine("Available Commands:", "", false));
                multiple.childSuggestions().forEach(suggestion -> sender.plattformSender().sendMessage(
                        renderer.commandSuggestionRow(suggestion, ClickEvent.callback(audience -> renderProxyDetails(sender, suggestion)))
                ));
                return;
            }
            case IndexCommandResult<VelocityCommandSource> index when !index.entries().isEmpty() -> {
                sender.plattformSender().sendMessage(renderer.detailHeader());
                sender.plattformSender().sendMessage(renderer.detailQueryLine("/" + query));
                sender.plattformSender().sendMessage(renderer.detailLine("Available Commands:", "", false));
                index.entries().forEach(entry -> sender.plattformSender().sendMessage(
                        renderer.commandSuggestionRow(entry.syntax(), ClickEvent.callback(audience -> renderProxyDetails(sender, entry.syntax())))
                ));
                return;
            }
            default -> {
            }
        }

        sender.plattformSender().sendMessage(renderer.detailHeader());
        sender.plattformSender().sendMessage(renderer.detailQueryLine("/" + query));
        sender.plattformSender().sendMessage(renderer.detailLine("No Results:", "Kein passender Proxy-Befehl gefunden.", false));
    }

    private void renderBackendDetails(VelocityCommandSource sender, String sourceId, String detailsQuery) {
        StyledHelpRenderer renderer = createRenderer(sender);
        NetworkHelpCatalog.Entry entry = service.findBackendEntry(sourceId, detailsQuery);
        if (entry == null) {
            sender.plattformSender().sendMessage(renderer.detailHeader());
            sender.plattformSender().sendMessage(renderer.detailQueryLine("/" + detailsQuery));
            sender.plattformSender().sendMessage(renderer.detailLine("No Results:", "Kein Backend-Help-Eintrag gefunden.", false));
            return;
        }

        sender.plattformSender().sendMessage(renderer.detailHeader());
        sender.plattformSender().sendMessage(renderer.detailQueryLine("/" + detailsQuery));
        sender.plattformSender().sendMessage(renderer.detailLine("Command:", entry.syntax(), true));
        sender.plattformSender().sendMessage(renderer.detailLine("Description:", entry.description(), false));
        if (entry.permission() != null && !entry.permission().isBlank()) {
            sender.plattformSender().sendMessage(renderer.detailLine("Permission:", entry.permission(), false));
        }
        sender.plattformSender().sendMessage(renderer.detailLine("Source:", sourceId, false));
    }
}
