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
import java.util.Locale;
import java.util.StringJoiner;

public final class PaperHelpCatalogPublisher {

    private final JavaPlugin plugin;

    public PaperHelpCatalogPublisher(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull NetworkHelpCatalog snapshot(@NotNull CommandManager<PaperCommandSource> commandManager) {
        var entries = commandManager.commands().stream()
                .map(this::toEntry)
                .sorted((left, right) -> left.command().compareToIgnoreCase(right.command()))
                .toList();

        String sourceId = plugin.getServer().getName();
        return new NetworkHelpCatalog(sourceId.isBlank() ? "paper-unknown" : sourceId, SourceType.BACKEND, Instant.now(), entries);
    }

    public void publishPreview(@NotNull CommandManager<PaperCommandSource> commandManager) {
        NetworkHelpCatalog catalog = snapshot(commandManager);
        plugin.getLogger().info("[help-poc] backend catalog size=" + catalog.entries().size() + ", source=" + catalog.sourceId());
    }

    private @NotNull Entry toEntry(@NotNull Command<PaperCommandSource> command) {
        String syntax = buildSyntax(command);
        String description = command.commandDescription().description().textDescription();
        if (description.isBlank()) {
            description = "-";
        }
        String commandKey = syntax.startsWith("/") ? syntax.substring(1) : syntax;
        return new Entry(syntax, description, null, commandKey.toLowerCase(Locale.ROOT));
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
}
