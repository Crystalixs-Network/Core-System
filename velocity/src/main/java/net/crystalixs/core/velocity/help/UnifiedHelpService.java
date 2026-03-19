package net.crystalixs.core.velocity.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class UnifiedHelpService {

    private static final String PROXY_SOURCE_LABEL = "Proxy";
    private static final String NO_DESCRIPTION = "-";

    private final CommandManager<VelocityCommandSource> commandManager;
    private final BackendHelpCatalogCache cache;

    public UnifiedHelpService(CommandManager<VelocityCommandSource> commandManager, BackendHelpCatalogCache cache) {
        this.commandManager = commandManager;
        this.cache = cache;
    }

    public List<UnifiedHelpEntry> query(VelocityCommandSource sender, String query) {
        String needle = normalize(query);
        List<UnifiedHelpEntry> out = new ArrayList<>();
        String currentServer = currentServerName(sender);

        commandManager.createHelpHandler()
                .queryRootIndex(sender)
                .entries()
                .forEach(entry -> {
                    String syntax = "/" + entry.syntax();
                    String description = orDefaultDescription(entry.command().commandDescription().description().textDescription());
                    if (matches(needle, syntax, description)) {
                        out.add(new UnifiedHelpEntry(PROXY_SOURCE_LABEL, syntax, description, null, true, entry.syntax()));
                    }
                });

        Map<String, UnifiedHelpEntry> backendUnique = new LinkedHashMap<>();
        for (NetworkHelpCatalog catalog : cache.all()) {
            if (currentServer != null && !catalog.sourceId().equalsIgnoreCase(currentServer)) {
                continue;
            }
            for (var entry : catalog.entries()) {
                if (!matches(needle, entry.syntax(), entry.description())) continue;
                String key = normalize(entry.syntax());
                backendUnique.putIfAbsent(key, new UnifiedHelpEntry(catalog.sourceId(), entry.syntax(), entry.description(), entry.permission(), false, entry.command()));
            }
        }
        out.addAll(backendUnique.values());
        out.sort(Comparator
                .comparing(UnifiedHelpEntry::sourceLabel, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(UnifiedHelpEntry::syntax, String.CASE_INSENSITIVE_ORDER));

        return out;
    }

    private boolean matches(String needle, String syntax, String description) {
        if (needle.isEmpty()) return true;
        return normalize(syntax).contains(needle) || normalize(description).contains(needle);
    }

    private @Nullable String currentServerName(@NotNull VelocityCommandSource sender) {
        if (!(sender instanceof VelocityPlayerCommandSource playerSource)) {
            return null;
        }
        return playerSource.player()
                .getCurrentServer()
                .map(connection -> connection.getServerInfo().getName())
                .orElse(null);
    }

    public @Nullable NetworkHelpCatalog.Entry findBackendEntry(@NotNull String sourceId, @NotNull String detailsQuery) {
        Optional<NetworkHelpCatalog> catalog = cache.all().stream()
                .filter(value -> value.sourceId().equalsIgnoreCase(sourceId))
                .findFirst();

        return catalog.flatMap(networkHelpCatalog -> networkHelpCatalog.entries().stream()
                .filter(entry -> entry.command().equalsIgnoreCase(detailsQuery)
                                 || entry.syntax().equalsIgnoreCase(detailsQuery)
                                 || entry.syntax().equalsIgnoreCase("/" + detailsQuery))
                .findFirst()).orElse(null);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String orDefaultDescription(String value) {
        if (value == null || value.isBlank()) {
            return NO_DESCRIPTION;
        }
        return value;
    }
}
