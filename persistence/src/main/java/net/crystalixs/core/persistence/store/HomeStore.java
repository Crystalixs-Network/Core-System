package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.HomeModel;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface HomeStore {

    Optional<HomeModel> findById(long homeId);

    Optional<HomeModel> findByPlayerAndName(UUID playerId, String name);

    Collection<HomeModel> findByPlayerId(UUID playerId);

    HomeModel create(HomeModel model);

    void rename(UUID playerId, String oldName, String newName);

    void updatePosition(long homeId, String worldName, double x, double y, double z, float yaw, float pitch);

    void updateIcon(long homeId, String icon);

    boolean deleteByPlayerAndName(UUID playerId, String name);

}
