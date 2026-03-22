package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

public interface HomeService {

    HomeModel create(UUID playerId, String name, Location location);

    int count(UUID playerId);

    Collection<HomeModel> all(UUID playerId);

    void delete(UUID playerId, String name);

    HomeModel rename(UUID playerId, String oldName, String newName);

    HomeModel updatePosition(UUID playerId, String name, Location location);

}
