package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.PlayerSettingModel;

import java.util.Optional;
import java.util.UUID;

public interface PlayerSettingStore {

    Optional<PlayerSettingModel> findByPlayerId(UUID playerId);

}
