package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Entry;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.SourceType;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Locale;

public final class PaperHelpCatalogPublisher {

    private final JavaPlugin plugin;

    public PaperHelpCatalogPublisher(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull NetworkHelpCatalog snapshot(@NotNull CommandManager<PaperCommandSource> commandManager) {
        PaperCommandSource helpSource = new PaperCommandSource(consoleSender(), null);
        var entries = commandManager.createHelpHandler()
                .queryRootIndex(helpSource)
                .entries()
                .stream()
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

    private @NotNull Entry toEntry(@NotNull org.incendo.cloud.help.result.CommandEntry<PaperCommandSource> entry) {
        String syntax = "/" + entry.syntax();
        String description = entry.command().commandDescription().description().textDescription();
        if (description.isBlank()) {
            description = "-";
        }
        String command = entry.syntax().toLowerCase(Locale.ROOT);
        return new Entry(syntax, description, null, command);
    }

    private CommandSender consoleSender() {
        return plugin.getServer().getConsoleSender();
    }
}
