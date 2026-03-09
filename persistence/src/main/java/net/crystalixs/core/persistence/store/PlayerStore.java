package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.PlayerModel;

import java.util.Optional;
import java.util.UUID;

public interface PlayerStore {

    Optional<PlayerModel> findById(UUID playerId);

    boolean exists(UUID playerId);

    void create(UUID playerId);

    void update(PlayerModel model);

    boolean delete(UUID playerId);

}
