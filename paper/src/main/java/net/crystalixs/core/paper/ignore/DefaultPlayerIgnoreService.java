package net.crystalixs.core.paper.ignore;

import net.crystalixs.core.persistence.store.PlayerIgnoreStore;

import java.util.UUID;

public final class DefaultPlayerIgnoreService implements PlayerIgnoreService {

    private final PlayerIgnoreStore store;

    public DefaultPlayerIgnoreService(PlayerIgnoreStore store) {
        this.store = store;
    }

    @Override
    public void ignorePlayer(UUID actor, UUID target) {
        store.create(actor, target);
    }

    @Override
    public boolean isIgnoredByPlayer(UUID actor, UUID target) {
        return store.findByPlayer(actor).stream().anyMatch(model -> model.ignoredUuid().equals(target));
    }
}
