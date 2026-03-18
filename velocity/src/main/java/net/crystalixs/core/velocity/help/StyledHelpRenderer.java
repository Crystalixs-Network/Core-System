package net.crystalixs.core.velocity.help;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class StyledHelpRenderer {

    public List<Component> render(String query, int page, int pages, List<UnifiedHelpEntry> pageEntries) {
        Component top = text("─────────────────────────────────────────────", DARK_GRAY)
                .append(text("Hilfe", GREEN))
                .append(text(" (" + page + "/" + pages + ")", GOLD))
                .append(text("─────────────────────────────────────────────", DARK_GRAY));

        Component info = text("Zeige Suchergebnisse für Query; ", GRAY)
                .append(text("\"/" + (query == null ? "" : query) + "\"", GREEN));

        Component head = text("└─", DARK_GRAY).append(text("Verfügbare Befehle:", GOLD));

        var rows = pageEntries.stream().map(this::commandRow).toList();
        Component navigation = navigation(query, page, pages);

        return Stream.of(top, info, head)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    list.addAll(rows);
                    list.add(navigation);
                    return list;
                }));
    }

    private Component commandRow(UnifiedHelpEntry entry) {
        return text("  ├─ ", DARK_GRAY)
                .append(text(entry.syntax(), GREEN)
                        .hoverEvent(HoverEvent.showText(text(entry.description(), WHITE)))
                        .clickEvent(ClickEvent.suggestCommand(entry.syntax())))
                .append(text(" ", GRAY))
                .append(text("[" + entry.sourceLabel() + "]", GOLD)
                        .hoverEvent(HoverEvent.showText(text(entry.description(), WHITE))));
    }

    private Component navigation(String query, int page, int pages) {
        Component left = page > 1
                ? text("[←]", GOLD)
                .hoverEvent(HoverEvent.showText(text("Previous page", GRAY)))
                .clickEvent(ClickEvent.runCommand("/help-page " + (page - 1) + " " + safeQuery(query)))
                : text("[←]", DARK_GRAY);

        Component right = page < pages
                ? text("[→]", GOLD)
                .hoverEvent(HoverEvent.showText(text("Next page", GRAY)))
                .clickEvent(ClickEvent.runCommand("/help-page " + (page + 1) + " " + safeQuery(query)))
                : text("[→]", DARK_GRAY);

        return text("└─ ", DARK_GRAY).append(left).append(text(" ", GRAY)).append(right);
    }

    private String safeQuery(String query) {
        return query == null || query.isBlank() ? "\"\"" : "\"" + query.replace("\"", "") + "\"";
    }
}
