package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.HomeModel;
import net.crystalixs.core.persistence.model.HomePositionModel;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;
import org.bukkit.Location;

import java.util.UUID;

public final class DefaultHomeService implements HomeService {

    private static final int MAX_HOME_NAME_LENGTH = 64;

    private final PlayerStore players;
    private final HomeStore homes;

    public DefaultHomeService(PlayerStore players, HomeStore homes) {
        this.players = players;
        this.homes = homes;
    }

    @Override
    public HomeModel create(UUID playerId, String name, Location location) {
        String normalizedName = normalizeName(name);
        HomePositionModel position = toPosition(location);

        getOrCreatePlayer(playerId);

        if (homes.findByPlayerAndName(playerId, normalizedName).isPresent()) {
            throw new HomeException(HomeError.HOME_ALREADY_EXISTS, "Home with name " + normalizedName + " already exists for player " + playerId);
        }
        try {
            return homes.create(new HomeModel(0L, playerId, normalizedName, position, null));

        } catch (PersistenceException exception) {
            if (isDuplicate(exception)) {
                throw new HomeException(HomeError.HOME_ALREADY_EXISTS, "Home with name " + normalizedName + " already exists for player " + playerId);
            }
            fail(playerId, normalizedName);
        }
        return null;
    }

    @Override
    public int count(UUID playerId) {
        return homes.findByPlayerId(playerId).size();
    }

    @Override
    public void delete(UUID playerId, String name) {
        String normalizedName = normalizeName(name);

        try {
            boolean deleted = homes.deleteByPlayerAndName(playerId, normalizedName);
            if (!deleted) {
                throw new HomeException(HomeError.HOME_NOT_FOUND, "Home with name " + normalizedName + " does not exist for player " + playerId);
            }

        } catch (PersistenceException exception) {
            throw new HomeException(HomeError.HOME_DELETION_FAILED, "Could not delete home '" + normalizedName + "' for player " + playerId);
        }
    }

    private boolean isDuplicate(Throwable throwable) {
        var current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                if (message.toLowerCase().contains("duplicate entry") || message.toLowerCase().contains("uq_homes_player_name")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new HomeException(HomeError.INVALID_NAME, "Name must not be null");
        }

        String normalized = name.trim();
        if (normalized.isBlank()) {
            throw new HomeException(HomeError.INVALID_NAME, "Name must not be blank");
        }
        if (normalized.length() > MAX_HOME_NAME_LENGTH) {
            throw new HomeException(HomeError.INVALID_NAME, "Name must not be longer than " + MAX_HOME_NAME_LENGTH + " characters");
        }
        return normalized;
    }

    private HomePositionModel toPosition(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new HomeException(HomeError.INVALID_POSITION, "Location/world must not be null");
        }
        return new HomePositionModel(location.getWorld().getName(), location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
    }

    private void getOrCreatePlayer(UUID playerId) {
        players.findById(playerId).orElseGet(() -> {
            players.create(playerId);
            return players.findById(playerId).orElseThrow(() -> new HomeException(HomeError.PLAYER_CREATION_FAILED, "Could not create player " + playerId));
        });
    }

    private void fail(UUID playerId, String name) {
        throw new HomeException(HomeError.HOME_CREATION_FAILED, "Could not create home '" + name + "' for player " + playerId);
    }
}
