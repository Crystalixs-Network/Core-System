package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Entry;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.SourceType;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import org.bukkit.plugin.java.JavaPlugin;
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
    private static final String UNKNOWN_SOURCE = "paper-unknown";

    private final JavaPlugin plugin;
    private final String configuredSourceId;

    public PaperHelpCatalogPublisher(JavaPlugin plugin, String configuredSourceId) {
        this.plugin = plugin;
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

        List<NetworkHelpCatalog.Argument> arguments = extractArguments(command);
        String commandKey = syntax.startsWith("/") ? syntax.substring(1) : syntax;
        return new Entry(syntax, description, null, commandKey.toLowerCase(Locale.ROOT), arguments);
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

    private List<NetworkHelpCatalog.Argument> extractArguments(@NotNull Command<PaperCommandSource> command) {
        List<NetworkHelpCatalog.Argument> arguments = new ArrayList<>();
        List<CommandComponent<PaperCommandSource>> components = command.components();
        for (int i = 1; i < components.size(); i++) {
            CommandComponent<PaperCommandSource> component = components.get(i);
            String argumentDescription = normalizedDescription(component.description().textDescription());
            arguments.add(new NetworkHelpCatalog.Argument(
                    formatComponent(component),
                    component.optional(),
                    argumentDescription
            ));
        }
        return arguments;
    }

    private String normalizedDescription(String value) {
        if (value == null || value.isBlank()) {
            return NO_DESCRIPTION;
        }
        return value;
    }

    private String resolveSourceId() {
        if (configuredSourceId != null && !configuredSourceId.isBlank()) {
            return configuredSourceId.trim();
        }

        String implementationName = plugin.getServer().getName();
        if (!implementationName.isBlank()) {
            return implementationName;
        }

        return UNKNOWN_SOURCE;
    }
}
