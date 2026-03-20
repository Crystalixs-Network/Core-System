package net.crystalixs.core.velocity.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.help.result.VerboseCommandResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

public final class StyledHelpRenderer {

    private static final String TREE_BRANCH = "├─";
    private static final String TREE_LAST = "└─";
    private static final String ARROW_LEFT = "←";
    private static final String ARROW_RIGHT = "→";
    private static final String HEADER_TITLE = "Hilfe";
    private static final String SEARCH_LABEL = "Zeige Suchergebnisse für Query: ";
    private static final String AVAILABLE_COMMANDS_LABEL = "Verfügbare Befehle:";

    private final Function<UnifiedHelpEntry, ClickEvent> detailsClickBuilder;

    public StyledHelpRenderer(Function<UnifiedHelpEntry, ClickEvent> detailsClickBuilder) {
        this.detailsClickBuilder = detailsClickBuilder;
    }

    public List<Component> render(String query, int page, int pages, List<UnifiedHelpEntry> pageEntries) {
        var output = new ArrayList<Component>();
        output.add(buildTop(page, pages));
        output.add(buildQueryInfo(query));
        output.add(prefixedLine(0, true, text(AVAILABLE_COMMANDS_LABEL, GRAY)));
        output.addAll(buildOverviewRows(pageEntries));
        output.add(navigation(query, page, pages));
        return output;
    }

    public List<Component> renderProxyVerboseDetails(VelocityCommandSource sender, CommandManager<VelocityCommandSource> commandManager, String query, VerboseCommandResult<VelocityCommandSource> verbose) {
        String commandSyntax = commandManager.commandSyntaxFormatter().apply(sender, verbose.entry().command().components(), null);

        var argumentContents = new ArrayList<Component>();
        if (verbose.entry().command().components().size() > 1) {
            Iterator<CommandComponent<VelocityCommandSource>> iterator = verbose.entry().command().components().iterator();
            iterator.next();

            while (iterator.hasNext()) {
                CommandComponent<VelocityCommandSource> component = iterator.next();
                String syntax = commandManager.commandSyntaxFormatter().apply(sender, Collections.singletonList(component), null);
                argumentContents.add(buildArgumentContent(syntax, component.optional(), descriptionText(component.description())));
            }
        }

        return renderVerboseDetails("/" + query, "/" + commandSyntax, descriptionText(verbose.entry().command().commandDescription().verboseDescription()), argumentContents);
    }

    public List<Component> renderCommandSuggestions(String query, List<String> suggestions, Function<String, ClickEvent> clickBuilder) {
        var output = new ArrayList<Component>();
        output.add(detailHeader());
        output.add(detailQueryLine("/" + query));
        output.add(prefixedLine(0, true, text(AVAILABLE_COMMANDS_LABEL, GRAY)));

        for (int i = 0; i < suggestions.size(); i++) {
            String syntax = suggestions.get(i);
            boolean isLast = i == suggestions.size() - 1;
            output.add(commandSuggestionRow(syntax, clickBuilder.apply(syntax), isLast));
        }
        return output;
    }

    public List<Component> renderBackendDetails(String detailsQuery, NetworkHelpCatalog.Entry entry) {
        List<Component> argumentContents = parseBackendArguments(entry);
        return renderVerboseDetails("/" + detailsQuery, entry.syntax(), entry.description(), argumentContents);
    }

    public List<Component> renderNoResults(String query, Component message) {
        return List.of(
                detailHeader(),
                detailQueryLine("/" + query),
                prefixedKeyValue(0, true, "Keine Ergebnisse:", message, false));
    }

    public Component detailHeader() {
        return text()
                .append(strikeLine())
                .append(text(" ", WHITE))
                .append(text(HEADER_TITLE, GREEN))
                .append(text(" ", WHITE))
                .append(strikeLine())
                .build();
    }

    public Component detailQueryLine(String query) {
        return text()
                .append(text(SEARCH_LABEL, GRAY))
                .append(text("\"", WHITE))
                .append(text(query, GREEN))
                .append(text("\"", WHITE))
                .build();
    }

    public Component commandSuggestionRow(String syntax, ClickEvent clickEvent, boolean isLast) {
        Component content = text()
                .append(text("/" + syntax, GREEN))
                .clickEvent(clickEvent)
                .append(text(" - Details anzeigen", GRAY))
                .build();

        return prefixedLine(1, isLast, content);
    }

    public String descriptionText(Description description) {
        if (description == null || description.isEmpty()) {
            return "-";
        }
        String value = description.textDescription();
        return value.isBlank() ? "-" : value;
    }

    public Component syntaxComponent(String syntax) {
        return colorizedSyntax(syntax);
    }

    private List<Component> buildOverviewRows(List<UnifiedHelpEntry> entries) {
        List<Component> rows = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            UnifiedHelpEntry entry = entries.get(i);
            boolean isLast = i == entries.size() - 1;
            rows.add(commandRow(entry, isLast));
        }
        return rows;
    }

    private Component buildTop(int page, int pages) {
        return text()
                .append(strikeLine())
                .append(text(" ", WHITE))
                .append(text(HEADER_TITLE, GREEN))
                .append(text(" (" + page + "/" + pages + ")", GOLD))
                .append(text(" ", WHITE))
                .append(strikeLine())
                .build();
    }

    private Component buildQueryInfo(String query) {
        return text(SEARCH_LABEL, GRAY).append(text("\"/" + (query == null ? "" : query) + "\"", GREEN));
    }

    private List<Component> renderVerboseDetails(String shownQuery, String commandSyntax, String description, List<Component> argumentContents) {
        var output = new ArrayList<Component>();
        output.add(detailHeader());
        output.add(detailQueryLine(shownQuery));
        output.add(prefixedKeyValue(0, true, "Befehl:", commandSyntax, true));

        boolean hasArguments = !argumentContents.isEmpty();
        output.add(prefixedKeyValue(1, !hasArguments, "Beschreibung:", description, false));

        if (hasArguments) {
            output.add(prefixedKeyValue(1, true, "Argumente:", "", false));
            for (int i = 0; i < argumentContents.size(); i++) {
                boolean isLast = i == argumentContents.size() - 1;
                output.add(prefixedLine(2, isLast, argumentContents.get(i)));
            }
        }
        return output;
    }

    private List<Component> parseBackendArguments(NetworkHelpCatalog.Entry entry) {
        if (!entry.arguments().isEmpty()) {
            var lines = new ArrayList<Component>();
            for (NetworkHelpCatalog.Argument argument : entry.arguments()) {
                lines.add(buildArgumentContent(argument.syntax(), argument.optional(), argument.description()));
            }
            return lines;
        }
        return parseBackendArgumentsFromSyntax(entry.syntax());
    }

    private List<Component> parseBackendArgumentsFromSyntax(String syntax) {
        String normalized = syntax.startsWith("/") ? syntax.substring(1) : syntax;
        var tokens = splitSyntaxTokens(normalized);

        if (tokens.size() <= 1) return Collections.emptyList();

        var lines = new ArrayList<Component>();
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            boolean optional = token.startsWith("[") && token.endsWith("]");
            lines.add(buildArgumentContent(token, optional, ""));
        }
        return lines;
    }

    private List<String> splitSyntaxTokens(String syntax) {
        var tokens = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int bracketDepth = 0;

        for (int i = 0; i < syntax.length(); i++) {
            char ch = syntax.charAt(i);
            if (Character.isWhitespace(ch) && bracketDepth == 0) {
                flushToken(tokens, current);
                continue;
            }
            if (ch == '[') {
                bracketDepth++;
            } else if (ch == ']' && bracketDepth > 0) {
                bracketDepth--;
            }
            current.append(ch);
        }

        flushToken(tokens, current);
        return tokens;
    }

    private void flushToken(List<String> tokens, StringBuilder current) {
        if (!current.isEmpty()) {
            tokens.add(current.toString());
            current.setLength(0);
        }
    }

    private Component buildArgumentContent(String syntax, boolean optional, String description) {
        Component line = text(" ", GRAY).append(syntaxComponent(syntax));
        if (optional) {
            line = line.append(text(" (Optional)", YELLOW));
        }
        if (description != null && !description.isBlank() && !description.equals("-")) {
            line = line.append(text(" - ", GRAY)).append(text(description, GRAY));
        }
        return line;
    }

    private Component commandRow(UnifiedHelpEntry entry, boolean isLast) {
        Component content = text()
                .append(colorizedSyntax(entry.syntax()))
                .hoverEvent(HoverEvent.showText(text(entry.description(), WHITE)))
                .clickEvent(detailsClickBuilder.apply(entry))
                .append(text(" - ", DARK_GRAY))
                .append(text(entry.description(), GRAY))
                .build();

        return prefixedLine(1, isLast, content);
    }

    private Component navigation(String query, int page, int pages) {
        String q = query == null ? "" : query.trim();
        String previous = q.isBlank() ? "/help --page " + (page - 1) : "/help " + q + " --page " + (page - 1);
        String next = q.isBlank() ? "/help --page " + (page + 1) : "/help " + q + " --page " + (page + 1);

        Component left = page > 1
                ? text("[" + ARROW_LEFT + "]", GOLD).clickEvent(ClickEvent.runCommand(previous))
                : text("[" + ARROW_LEFT + "]", DARK_GRAY);

        Component right = page < pages
                ? text("[" + ARROW_RIGHT + "]", GOLD).clickEvent(ClickEvent.runCommand(next))
                : text("[" + ARROW_RIGHT + "]", DARK_GRAY);

        return text()
                .append(strikeLine())
                .append(text(" ", WHITE))
                .append(left)
                .append(text("   ", WHITE))
                .append(right)
                .append(text(" ", WHITE))
                .append(strikeLine())
                .build();
    }

    private Component strikeLine() {
        return text("-".repeat(15), GOLD, STRIKETHROUGH);
    }

    private Component prefixedKeyValue(int level, boolean isLast, String key, String value, boolean commandLike) {
        Component line = text(key + " ", GOLD);
        if (value != null && !value.isBlank()) {
            line = line.append(commandLike ? colorizedSyntax(value) : text(value, GRAY));
        }
        return prefixedLine(level, isLast, line);
    }

    private Component prefixedKeyValue(int level, boolean isLast, String key, Component value, boolean commandLike) {
        Component line = text(key + " ", GOLD);
        if (value != null) {
            line = line.append(value);
        }
        return prefixedLine(level, isLast, line);
    }

    private Component prefixedLine(int level, boolean isLast, Component content) {
        String branch = isLast ? TREE_LAST : TREE_BRANCH;
        String indent = "   ".repeat(Math.max(0, level));
        return text(indent + branch + " ", DARK_GRAY).append(content);
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

            int close = findMatchingBracket(syntax, open);
            if (close == -1) {
                builder.append(text(syntax.substring(open), YELLOW));
                break;
            }

            builder.append(text(syntax.substring(open, close + 1), YELLOW));
            index = close + 1;
        }

        return builder.build();
    }

    private int findMatchingBracket(String input, int openIndex) {
        int depth = 0;
        for (int i = openIndex; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '[') {
                depth++;
            }
            if (c == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}
