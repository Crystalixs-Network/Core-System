package net.crystalixs.core.persistence.defaults;

import net.crystalixs.core.persistence.model.PlayerSettingModel;
import net.crystalixs.core.persistence.store.PlayerSettingStore;

import java.util.Optional;
import java.util.UUID;

public final class DefaultPlayerSettingStore implements PlayerSettingStore {

    @Override
    public Optional<PlayerSettingModel> findByPlayerId(UUID playerId) {
        return Optional.empty();
    }
}
