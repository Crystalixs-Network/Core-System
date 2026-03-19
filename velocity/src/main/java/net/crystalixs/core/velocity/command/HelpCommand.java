package net.crystalixs.core.velocity.command;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.help.StyledHelpRenderer;
import net.crystalixs.core.velocity.help.UnifiedHelpEntry;
import net.crystalixs.core.velocity.help.UnifiedHelpService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.help.HelpQuery;
import org.incendo.cloud.help.result.HelpQueryResult;
import org.incendo.cloud.help.result.IndexCommandResult;
import org.incendo.cloud.help.result.MultipleCommandResult;
import org.incendo.cloud.help.result.VerboseCommandResult;
import org.jspecify.annotations.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;
import static org.incendo.cloud.minecraft.extras.RichDescription.translatable;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

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
                .optional("query", stringParser())
                .handler(context -> {
                    int page = context.get("page");
                    String encoded = context.getOrDefault("query", "");
                    String query = decodeQuery(encoded);
                    renderPage(context.sender(), query, page);
                }));

        commandManager.command(commandManager.commandBuilder("help-show")
                .commandDescription(translatable("command.help.description.main"))
                .senderType(VelocityPlayerCommandSource.class)
                .required("source", stringParser())
                .required("query", stringParser())
                .handler(context -> {
                    String source = decodeQuery(context.get("source"));
                    String detailQuery = decodeQuery(context.get("query"));

                    if ("proxy".equalsIgnoreCase(source)) {
                        renderProxyDetails(context.sender(), detailQuery);
                        return;
                    }

                    renderBackendDetails(context.sender(), source, detailQuery);
                }));
    }

    private void renderPage(VelocityCommandSource sender, String query, int requestedPage) {
        var all = service.query(sender, query);

        int pageSize = 8;
        int pages = Math.max(1, (int) Math.ceil((double) all.size() / pageSize));
        int page = Math.min(Math.max(1, requestedPage), pages);

        int from = Math.min((page - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        var pageEntries = all.subList(from, to);

        StyledHelpRenderer renderer = new StyledHelpRenderer(this::encodeQuery, this::detailsCommand);
        renderer.render(query, page, pages, pageEntries).forEach(sender.plattformSender()::sendMessage);
    }

    private String detailsCommand(UnifiedHelpEntry entry) {
        String source = entry.isProxyCommand() ? "proxy" : entry.sourceLabel();
        return "/help-show " + encodeQuery(source) + " " + encodeQuery(entry.detailsQuery());
    }

    private void renderProxyDetails(VelocityCommandSource sender, String detailsQuery) {
        String query = detailsQuery.startsWith("/") ? detailsQuery.substring(1) : detailsQuery;
        HelpQueryResult<VelocityCommandSource> result = commandManager.createHelpHandler().query(HelpQuery.of(sender, query));

        switch (result) {
            case VerboseCommandResult<VelocityCommandSource> verbose -> {
                String commandSyntax = commandManager.commandSyntaxFormatter()
                        .apply(sender, verbose.entry().command().components(), null);

                sender.plattformSender().sendMessage(detailHeader());
                sender.plattformSender().sendMessage(detailQueryLine("/" + query));
                sender.plattformSender().sendMessage(detailLine("Command:", "/" + commandSyntax, true));
                sender.plattformSender().sendMessage(detailLine("Description:", descriptionText(verbose.entry().command().commandDescription().verboseDescription()), false));

                if (verbose.entry().command().components().size() > 1) {
                    sender.plattformSender().sendMessage(detailLine("Arguments:", "", false));
                    Iterator<CommandComponent<VelocityCommandSource>> iterator = verbose.entry().command().components().iterator();
                    iterator.next();
                    int depth = 0;

                    while (iterator.hasNext()) {
                        CommandComponent<VelocityCommandSource> component = iterator.next();
                        String syntax = commandManager.commandSyntaxFormatter()
                                .apply(sender, Collections.singletonList(component), null);

                        Component line = text(" ", GRAY).append(colorizedSyntax(syntax));
                        if (component.optional()) {
                            line = line.append(text(" (Optional)", YELLOW));
                        }

                        String description = descriptionText(component.description());
                        if (!description.isBlank() && !description.equals("-")) {
                            line = line.append(text(" - ", GRAY)).append(text(description, GRAY));
                        }

                        sender.plattformSender().sendMessage(nestedArgumentLine(depth, line));

                        depth++;
                    }
                }
                return;
            }
            case MultipleCommandResult<VelocityCommandSource> multiple -> {
                sender.plattformSender().sendMessage(detailHeader());
                sender.plattformSender().sendMessage(detailQueryLine("/" + query));
                sender.plattformSender().sendMessage(detailLine("Available Commands:", "", false));
                multiple.childSuggestions().forEach(suggestion -> sender.plattformSender().sendMessage(commandSuggestionRow(suggestion)));
                return;
            }
            case IndexCommandResult<VelocityCommandSource> index when !index.entries().isEmpty() -> {
                sender.plattformSender().sendMessage(detailHeader());
                sender.plattformSender().sendMessage(detailQueryLine("/" + query));
                sender.plattformSender().sendMessage(detailLine("Available Commands:", "", false));
                index.entries().forEach(entry -> sender.plattformSender().sendMessage(commandSuggestionRow(entry.syntax())));
                return;
            }
            default -> {
            }
        }

        sender.plattformSender().sendMessage(detailHeader());
        sender.plattformSender().sendMessage(detailQueryLine("/" + query));
        sender.plattformSender().sendMessage(detailLine("No Results:", "Kein passender Proxy-Befehl gefunden.", false));
    }

    private void renderBackendDetails(VelocityCommandSource sender, String sourceId, String detailsQuery) {
        NetworkHelpCatalog.Entry entry = service.findBackendEntry(sourceId, detailsQuery);
        if (entry == null) {
            sender.plattformSender().sendMessage(detailHeader());
            sender.plattformSender().sendMessage(detailQueryLine("/" + detailsQuery));
            sender.plattformSender().sendMessage(detailLine("No Results:", "Kein Backend-Help-Eintrag gefunden.", false));
            return;
        }

        sender.plattformSender().sendMessage(detailHeader());
        sender.plattformSender().sendMessage(detailQueryLine("/" + detailsQuery));
        sender.plattformSender().sendMessage(detailLine("Command:", entry.syntax(), true));
        sender.plattformSender().sendMessage(detailLine("Description:", entry.description(), false));
        if (entry.permission() != null && !entry.permission().isBlank()) {
            sender.plattformSender().sendMessage(detailLine("Permission:", entry.permission(), false));
        }
        sender.plattformSender().sendMessage(detailLine("Source:", sourceId, false));
    }

    private Component detailHeader() {
        return text()
                .append(strikeLine())
                .append(text(" ", WHITE))
                .append(text("Hilfe", GREEN))
                .append(text(" ", WHITE))
                .append(strikeLine())
                .build();
    }

    private Component detailQueryLine(String query) {
        return text()
                .append(text("Zeige Suchergebnisse für Query: ", GRAY))
                .append(text("\"", WHITE))
                .append(text(query, GREEN))
                .append(text("\"", WHITE))
                .build();
    }

    private Component detailLine(String key, String value, boolean commandLike) {
        Component base = text("└─ ", DARK_GRAY).append(text(key + " ", GOLD));
        if (value == null || value.isBlank()) {
            return base;
        }
        return commandLike
                ? base.append(colorizedSyntax(value))
                : base.append(text(value, GRAY));
    }

    private Component commandSuggestionRow(String syntax) {
        String command = "/help-show " + encodeQuery("proxy") + " " + encodeQuery(syntax);
        return text("   |- ", DARK_GRAY)
                .append(text("/" + syntax, GREEN)
                        .clickEvent(ClickEvent.runCommand(command))
                        .append(text(" - Details anzeigen", GRAY)));
    }

    private Component nestedArgumentLine(int depth, Component content) {
        String indent = "   " + "  ".repeat(Math.max(0, depth));
        return text(indent + "├─ ", DARK_GRAY).append(content);
    }

    private String descriptionText(Description description) {
        if (description == null || description.isEmpty()) {
            return "-";
        }

        String text = description.textDescription();
        return text.isBlank() ? "-" : text;
    }

    private Component colorizedSyntax(String syntax) {
        var builder = text();
        int index = 0;
        while (index < syntax.length()) {
            int open = syntax.indexOf('[', index);
            if (open < 0) {
                builder.append(text(syntax.substring(index), GREEN));
                break;
            }
            if (open > index) {
                builder.append(text(syntax.substring(index, open), GREEN));
            }
            int close = syntax.indexOf(']', open + 1);
            if (close < 0) {
                builder.append(text(syntax.substring(open), YELLOW));
                break;
            }
            builder.append(text(syntax.substring(open, close + 1), YELLOW));
            index = close + 1;
        }
        return builder.build();
    }

    private Component strikeLine() {
        return text("-".repeat(10), GOLD, STRIKETHROUGH);
    }

    private String encodeQuery(String query) {
        if (query == null || query.isBlank()) return "";
        return Base64.getUrlEncoder().withoutPadding().encodeToString(query.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeQuery(String encoded) {
        if (encoded == null || encoded.isBlank()) return "";
        try {
            return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }
}
