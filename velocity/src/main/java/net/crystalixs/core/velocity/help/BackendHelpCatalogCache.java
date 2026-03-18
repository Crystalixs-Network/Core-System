package net.crystalixs.core.velocity.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Entry;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.SourceType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public final class BackendHelpCatalogCache {

    private final Map<String, NetworkHelpCatalog> catalogs = new ConcurrentHashMap<>();

    public void upsert(NetworkHelpCatalog catalog) {
        requireNonNull(catalog, "The catalog cannot be null");
        catalogs.put(catalog.sourceId(), catalog);
    }

    public @NotNull Collection<NetworkHelpCatalog> all() {
        return List.copyOf(catalogs.values());
    }

    public int totalEntries() {
        return catalogs.values().stream().mapToInt(catalogs -> catalogs.entries().size()).sum();
    }

    // POC helper: fake one backend snapshot until transport is wired
    public void loadPreviewSample() {
        NetworkHelpCatalog sample = new NetworkHelpCatalog("lobby-1", SourceType.BACKEND, Instant.now(), List.of(
                new Entry("/coins", "Zeigt deinen aktuellen Coin-Kontostand", "core.command.coins", "coins"),
                new Entry("/balance", "Zeigt deinen aktuellen Kontostand", "core.command.balance", "balance"),
                new Entry("/tpa <spieler>", "Sendet eine Teleport-Anfrage", "core.command.tpa", "tpa")
        ));
        upsert(sample);
    }

}
