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
            return fail(HomeError.HOME_ALREADY_EXISTS, "Home with name " + normalizedName + " already exists for player " + playerId);
        }
        try {
            return homes.create(new HomeModel(0L, playerId, normalizedName, position, null));

        } catch (PersistenceException exception) {
            if (isDuplicate(exception)) {
                return fail(HomeError.HOME_ALREADY_EXISTS, "Home with name " + normalizedName + " already exists for player " + playerId);
            }
            return fail(HomeError.HOME_CREATION_FAILED, "Could not create home '" + name + "' for player " + playerId);
        }
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
                fail(HomeError.HOME_NOT_FOUND, "Home with name " + normalizedName + " does not exist for player " + playerId);
            }

        } catch (PersistenceException exception) {
            fail(HomeError.HOME_DELETION_FAILED, "Could not delete home '" + normalizedName + "' for player " + playerId);
        }
    }

    @Override
    public HomeModel rename(UUID playerId, String oldName, String newName) {
        String normalizedOldName = normalizeRenameOldName(oldName);
        String normalizedNewName = normalizeRenameNewName(newName);

        try {
            HomeModel existing = homes
                    .findByPlayerAndName(playerId, normalizedOldName)
                    .orElseThrow(() -> fail(HomeError.HOME_NOT_FOUND, "Home with name " + normalizedOldName + " does not exist for player " + playerId));

            // no-op: the name remains the same
            if (existing.name().equalsIgnoreCase(normalizedNewName)) {
                return existing;
            }

            if (homes.findByPlayerAndName(playerId, normalizedNewName).isPresent()) {
                return fail(HomeError.HOME_ALREADY_EXISTS, "Home with name " + normalizedNewName + " already exists for player " + playerId);
            }

            HomeModel renamed = new HomeModel(existing.id(), existing.playerId(), normalizedNewName, existing.position(), existing.createdAt());
            try {
                homes.update(renamed);
                return homes.findById(existing.id()).orElse(renamed);

            } catch (PersistenceException exception) {
                if (isDuplicate(exception)) {
                    return fail(HomeError.HOME_ALREADY_EXISTS, "Home with name " + normalizedNewName + " already exists for player " + playerId);
                }
                return fail(HomeError.HOME_RENAME_FAILED, "Could not rename home '" + normalizedOldName + "' to '" + normalizedNewName + "' for player " + playerId);
            }

        } catch (PersistenceException exception) {
            return fail(HomeError.HOME_RENAME_FAILED, "Could not rename home '" + normalizedOldName + "' to '" + normalizedNewName + "' for player " + playerId);
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

    private String normalizeRenameOldName(String oldName) {
        try {
            return normalizeName(oldName);
        } catch (HomeException exception) {
            if (exception.error() == HomeError.INVALID_NAME) {
                return fail(HomeError.INVALID_NAME, "Old home name must be valid");
            }
            throw exception;
        }
    }

    private String normalizeRenameNewName(String newName) {
        try {
            return normalizeName(newName);
        } catch (HomeException exception) {
            if (exception.error() == HomeError.INVALID_NAME) {
                return fail(HomeError.INVALID_NAME, "New home name must be valid");
            }
            throw exception;
        }
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

    private static <T> T fail(HomeError error, String message) {
        throw new HomeException(error, message);
    }
}
