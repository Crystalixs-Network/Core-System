package net.crystalixs.core.velocity.help;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.incendo.cloud.description.Description;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

public final class StyledHelpRenderer {

    private final Function<String, String> queryEncoder;
    private final Function<UnifiedHelpEntry, ClickEvent> detailsClickBuilder;

    public StyledHelpRenderer(
            Function<String, String> queryEncoder,
            Function<UnifiedHelpEntry, ClickEvent> detailsClickBuilder
    ) {
        this.queryEncoder = queryEncoder;
        this.detailsClickBuilder = detailsClickBuilder;
    }

    public List<Component> render(String query, int page, int pages, List<UnifiedHelpEntry> pageEntries) {
        Component top = text()
                .append(strikeLine())
                .append(text(" ", WHITE))
                .append(text("Hilfe", GREEN))
                .append(text(" (" + page + "/" + pages + ")", GOLD))
                .append(text(" ", WHITE))
                .append(strikeLine())
                .build();

        Component info = text("Zeige Suchergebnisse für Query: ", GRAY).append(text("\"/" + (query == null ? "" : query) + "\"", GREEN));
        Component head = text("`- ", DARK_GRAY).append(text("Verfügbare Befehle:", GRAY));

        var rows = pageEntries.stream().map(this::commandRow).toList();
        Component navigation = navigation(query, page, pages);

        return Stream.of(top, info, head)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    list.addAll(rows);
                    list.add(navigation);
                    return list;
                }));
    }

    public Component detailHeader() {
        return text()
                .append(strikeLine())
                .append(text(" ", WHITE))
                .append(text("Hilfe", GREEN))
                .append(text(" ", WHITE))
                .append(strikeLine())
                .build();
    }

    public Component detailQueryLine(String query) {
        return text()
                .append(text("Zeige Suchergebnisse für Query: ", GRAY))
                .append(text("\"", WHITE))
                .append(text(query, GREEN))
                .append(text("\"", WHITE))
                .build();
    }

    public Component detailLine(String key, String value, boolean commandLike) {
        Component base = text("`- ", DARK_GRAY).append(text(key + " ", GOLD));
        if (value == null || value.isBlank()) {
            return base;
        }
        return commandLike
                ? base.append(colorizedSyntax(value))
                : base.append(text(value, GRAY));
    }

    public Component commandSuggestionRow(String syntax, ClickEvent clickEvent) {
        return text()
                .append(text("   |- ", DARK_GRAY))
                .append(text("/" + syntax, GREEN)
                        .clickEvent(clickEvent)
                        .append(text(" - Details anzeigen", GRAY)))
                .build();
    }

    public Component nestedArgumentLine(int depth, Component content) {
        String indent = "   " + "  ".repeat(Math.max(0, depth));
        return text(indent + "|- ", DARK_GRAY).append(content);
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

    private Component commandRow(UnifiedHelpEntry entry) {
        return text()
                .append(text("   |- ", DARK_GRAY))
                .append(colorizedSyntax(entry.syntax())
                        .hoverEvent(HoverEvent.showText(text(entry.description(), WHITE)))
                        .clickEvent(detailsClickBuilder.apply(entry)))
                .append(text(" - ", DARK_GRAY))
                .append(text(entry.description(), GRAY))
                .build();
    }

    private Component navigation(String query, int page, int pages) {
        String q = queryEncoder.apply(query);
        String previous = q.isBlank() ? "/help-page " + (page - 1) : "/help-page " + (page - 1) + " " + q;
        String next = q.isBlank() ? "/help-page " + (page + 1) : "/help-page " + (page + 1) + " " + q;

        Component left = page > 1
                ? text("[←]", GOLD).clickEvent(ClickEvent.runCommand(previous))
                : text("[←]", DARK_GRAY);

        Component right = page < pages
                ? text("[→]", GOLD).clickEvent(ClickEvent.runCommand(next))
                : text("[→]", DARK_GRAY);

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
