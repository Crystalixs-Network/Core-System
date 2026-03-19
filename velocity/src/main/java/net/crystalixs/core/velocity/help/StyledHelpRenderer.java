package net.crystalixs.core.velocity.help;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

public final class StyledHelpRenderer {

    private final Function<String, String> queryEncoder;
    private final Function<UnifiedHelpEntry, String> detailsCommandBuilder;

    public StyledHelpRenderer(
            Function<String, String> queryEncoder,
            Function<UnifiedHelpEntry, String> detailsCommandBuilder
    ) {
        this.queryEncoder = queryEncoder;
        this.detailsCommandBuilder = detailsCommandBuilder;
    }

    public List<Component> render(String query, int page, int pages, List<UnifiedHelpEntry> pageEntries) {
        Component top = text()
                .append(text("-".repeat(15), GOLD, STRIKETHROUGH))
                .append(text(" ", WHITE))
                .append(text("Hilfe", GREEN))
                .append(text(" (" + page + "/" + pages + ")", GOLD))
                .append(text(" ", WHITE))
                .append(text("-".repeat(15), GOLD, STRIKETHROUGH))
                .build();

        Component info = text("Zeige Suchergebnisse für Query: ", GRAY)
                .append(text("\"/" + (query == null ? "" : query) + "\"", GREEN));

        Component head = text("└─ ", DARK_GRAY).append(text("Verfügbare Befehle:", GRAY));

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
        return text("   ├─ ", DARK_GRAY)
                .append(text(entry.syntax(), GREEN)
                        .hoverEvent(HoverEvent.showText(text(entry.description(), WHITE)))
                        .clickEvent(ClickEvent.runCommand(detailsCommandBuilder.apply(entry))))
                .append(text(" - ", DARK_GRAY))
                .append(text(entry.description(), GRAY));
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
                .append(text("-".repeat(15), GOLD, STRIKETHROUGH))
                .append(text(" ", WHITE))
                .append(left)
                .append(text("   ", WHITE))
                .append(right)
                .append(text(" ", WHITE))
                .append(text("-".repeat(15), GOLD, STRIKETHROUGH))
                .build();
    }
}
