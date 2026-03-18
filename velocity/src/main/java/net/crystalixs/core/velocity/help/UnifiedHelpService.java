package net.crystalixs.core.velocity.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import org.incendo.cloud.CommandManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class UnifiedHelpService {

    private final CommandManager<VelocityCommandSource> commandManager;
    private final BackendHelpCatalogCache cache;

    public UnifiedHelpService(CommandManager<VelocityCommandSource> commandManager, BackendHelpCatalogCache cache) {
        this.commandManager = commandManager;
        this.cache = cache;
    }

    public List<UnifiedHelpEntry> query(VelocityCommandSource sender, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        List<UnifiedHelpEntry> out = new ArrayList<>();

        commandManager.createHelpHandler()
                .queryRootIndex(sender)
                .entries()
                .forEach(entry -> {
                    String syntax = "/" + entry.syntax();
                    String description = entry.command().commandDescription().description().textDescription();
                    if (matches(needle, syntax, description)) {
                        out.add(new UnifiedHelpEntry("Proxy", syntax, description, null, true));
                    }
                });

        for (NetworkHelpCatalog catalog : cache.all()) {
            for (var entry : catalog.entries()) {
                if (!matches(needle, entry.syntax(), entry.description())) continue;
                out.add(new UnifiedHelpEntry(catalog.sourceId(), entry.syntax(), entry.description(), entry.permission(), false));
            }
        }
        out.sort(Comparator
                .comparing(UnifiedHelpEntry::sourceLabel, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(UnifiedHelpEntry::syntax, String.CASE_INSENSITIVE_ORDER));

        return out;
    }

    private boolean matches(String needle, String syntax, String description) {
        if (needle.isEmpty()) return true;
        return syntax.toLowerCase().contains(needle) || description.toLowerCase().contains(needle);
    }
}
