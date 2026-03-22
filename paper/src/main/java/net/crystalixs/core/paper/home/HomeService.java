package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.model.HomeModel;
import org.bukkit.Location;

import java.util.UUID;

public interface HomeService {

    HomeModel create(UUID playerId, String name, Location location);

    int count(UUID playerId);

}
