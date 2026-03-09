package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.HomeModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeStore {

    Optional<HomeModel> findById(long homeId);

    Optional<HomeModel> findByPlayerAndName(UUID playerId, String name);

    List<HomeModel> findByPlayerId(UUID playerId);

    HomeModel create(HomeModel model);

    void update(HomeModel model);

    boolean deleteById(long homeId);

    boolean deleteByPlayerAndName(UUID playerId, String name);

}
