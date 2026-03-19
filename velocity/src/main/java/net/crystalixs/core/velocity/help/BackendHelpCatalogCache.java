package net.crystalixs.core.velocity.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import org.jetbrains.annotations.NotNull;

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

}
