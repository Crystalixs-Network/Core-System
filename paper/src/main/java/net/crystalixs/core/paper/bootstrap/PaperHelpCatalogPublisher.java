package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Argument;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Entry;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.SourceType;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public final class PaperHelpCatalogPublisher {

    private static final String NO_DESCRIPTION = "-";

    private final String configuredSourceId;

    public PaperHelpCatalogPublisher(String configuredSourceId) {
        this.configuredSourceId = configuredSourceId;
    }

    public @NotNull NetworkHelpCatalog snapshot(@NotNull CommandManager<PaperCommandSource> commandManager) {
        var entries = commandManager.commands().stream()
                .map(this::toEntry)
                .sorted((left, right) -> left.command().compareToIgnoreCase(right.command()))
                .toList();

        String sourceId = resolveSourceId();
        return new NetworkHelpCatalog(sourceId, SourceType.BACKEND, Instant.now(), entries);
    }

    private @NotNull Entry toEntry(@NotNull Command<PaperCommandSource> command) {
        String syntax = buildSyntax(command);
        String description = normalizedDescription(command.commandDescription().description().textDescription());

        var arguments = extractArguments(command);
        String key = syntax.startsWith("/") ? syntax.substring(1) : syntax;
        String permission = normalizedPermission(command.commandPermission().permissionString());

        return new Entry(syntax, description, permission, key.toLowerCase(Locale.ROOT), arguments);
    }

    private String buildSyntax(@NotNull Command<PaperCommandSource> command) {
        StringJoiner joiner = new StringJoiner(" ", "/", "");
        for (CommandComponent<PaperCommandSource> component : command.components()) {
            joiner.add(formatComponent(component));
        }
        return joiner.toString();
    }

    private String formatComponent(@NotNull CommandComponent<PaperCommandSource> component) {
        if (component.type() == CommandComponent.ComponentType.LITERAL) {
            return component.name();
        }

        String variable = component.name();
        if (component.optional()) {
            return "[" + variable + "]";
        }
        return "<" + variable + ">";
    }

    private List<Argument> extractArguments(@NotNull Command<PaperCommandSource> command) {
        var arguments = new ArrayList<Argument>();
        var components = command.components();

        for (int i = 1; i < components.size(); i++) {
            var component = components.get(i);
            String description = normalizedDescription(component.description().textDescription());
            arguments.add(new Argument(formatComponent(component), component.optional(), description));
        }
        return arguments;
    }

    private String normalizedPermission(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private String normalizedDescription(String value) {
        if (value == null || value.isBlank()) {
            return NO_DESCRIPTION;
        }
        return value;
    }

    private String resolveSourceId() {
        return configuredSourceId.trim();
    }
}
