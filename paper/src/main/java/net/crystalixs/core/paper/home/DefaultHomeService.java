package net.crystalixs.core.paper.home;

import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.HomeModel;
import net.crystalixs.core.persistence.model.HomePositionModel;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
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
            return failAsAlreadyExistent(playerId, normalizedName);
        }
        try {
            return homes.create(new HomeModel(0L, playerId, normalizedName, null, position, null));

        } catch (PersistenceException exception) {
            if (isDuplicate(exception)) {
                return failAsAlreadyExistent(playerId, normalizedName);
            }
            return fail(HomeError.HOME_CREATION_FAILED, "Could not create home '" + name + "' for player " + playerId);
        }
    }

    @Override
    public int count(UUID playerId) {
        return homes.findByPlayerId(playerId).size();
    }

    @Override
    public Collection<HomeModel> all(UUID playerId) {
        return homes.findByPlayerId(playerId).stream()
                .sorted(Comparator.comparing(HomeModel::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public void delete(UUID playerId, String name) {
        String normalizedName = normalizeName(name);

        try {
            boolean deleted = homes.deleteByPlayerAndName(playerId, normalizedName);
            if (!deleted) {
                failAsNotExistent(playerId, normalizedName);
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
                    .orElseThrow(() -> failAsNotExistent(playerId, normalizedOldName));

            // no-op: the name remains the same
            if (existing.name().equalsIgnoreCase(normalizedNewName)) {
                return existing;
            }

            if (homes.findByPlayerAndName(playerId, normalizedNewName).isPresent()) {
                return failAsAlreadyExistent(playerId, normalizedNewName);
            }

            HomeModel renamed = new HomeModel(existing.id(), existing.playerId(), normalizedNewName, existing.icon(), existing.position(), existing.createdAt());
            try {
                homes.rename(playerId, normalizedOldName, normalizedNewName);
                return homes.findById(existing.id()).orElse(renamed);

            } catch (PersistenceException exception) {
                if (isDuplicate(exception)) {
                    return failAsAlreadyExistent(playerId, normalizedNewName);
                }
                return fail(HomeError.HOME_RENAME_FAILED, "Could not rename home '" + normalizedOldName + "' to '" + normalizedNewName + "' for player " + playerId);
            }

        } catch (PersistenceException exception) {
            return fail(HomeError.HOME_RENAME_FAILED, "Could not rename home '" + normalizedOldName + "' to '" + normalizedNewName + "' for player " + playerId);
        }
    }

    @Override
    public HomeModel updatePosition(UUID playerId, String name, Location location) {
        String normalizedName = normalizeName(name);
        HomePositionModel position = toPosition(location);
        HomeModel existing = homes
                .findByPlayerAndName(playerId, name)
                .orElseThrow(() -> failAsNotExistent(playerId, normalizedName));

        if (isSamePosition(existing.position(), position)) {
            return existing;
        }

        HomeModel updated = new HomeModel(existing.id(), existing.playerId(), existing.name(), existing.icon(), position, existing.createdAt());
        try {
            homes.updatePosition(existing.id(), position.worldName(), position.x(), position.y(), position.z(), position.yaw(), position.pitch());
            return homes.findById(existing.id()).orElse(updated);

        } catch (PersistenceException exception) {
            return fail(HomeError.HOME_UPDATE_FAILED, "Could not update home '" + normalizedName + "' for player " + playerId);
        }
    }

    @Override
    public void teleport(Player player, String homeName) {
        String normalizedName = normalizeName(homeName);
        HomeModel home;
        try {
            home = homes.findByPlayerAndName(player.getUniqueId(), normalizedName).orElseThrow(() -> failAsNotExistent(player.getUniqueId(), normalizedName));

        } catch (PersistenceException exception) {
            fail(HomeError.HOME_TELEPORT_FAILED, "Could not load home '" + normalizedName + "' for player " + player.getUniqueId());
            return;
        }

        World world = Bukkit.getWorld(home.position().worldName());
        if (world == null) {
            fail(HomeError.HOME_WORLD_NOT_AVAILABLE, "Home world '" + home.position().worldName() + "' is not available for home '" + normalizedName + "'");
        }
        try {
            if (!player.teleport(toLocation(home, world))) {
                fail(HomeError.HOME_TELEPORT_FAILED, "Could not teleport player " + player.getUniqueId() + " to home '" + normalizedName + "'");
            }
        } catch (RuntimeException exception) {
            fail(HomeError.HOME_TELEPORT_FAILED, "Unexpected teleport failure for player " + player.getUniqueId() + " and home '" + normalizedName + "'");
        }
    }

    private HomePositionModel toPosition(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new HomeException(HomeError.INVALID_POSITION, "Location/world must not be null");
        }
        return new HomePositionModel(location.getWorld().getName(), location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
    }

    private Location toLocation(HomeModel model, World world) {
        if (model == null || world == null) {
            throw new HomeException(HomeError.INVALID_POSITION, "Home/world must not be null");
        }
        return new Location(world, model.position().x(), model.position().y(), model.position().z(), model.position().yaw(), model.position().pitch());
    }

    private boolean isSamePosition(HomePositionModel a, HomePositionModel b) {
        return a.worldName().equals(b.worldName())
               && Double.compare(a.x(), b.x()) == 0
               && Double.compare(a.y(), b.y()) == 0
               && Double.compare(a.z(), b.z()) == 0
               && Float.compare(a.yaw(), b.yaw()) == 0
               && Float.compare(a.pitch(), b.pitch()) == 0;
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

    private void getOrCreatePlayer(UUID playerId) {
        players.findById(playerId).orElseGet(() -> {
            players.create(playerId);
            return players.findById(playerId).orElseThrow(() -> new HomeException(HomeError.PLAYER_CREATION_FAILED, "Could not create player " + playerId));
        });
    }

    private static <T> T failAsAlreadyExistent(UUID playerId, String newName) {
        return fail(HomeError.HOME_ALREADY_EXISTS, "Home with name " + newName + " already exists for player " + playerId);
    }

    private static HomeException failAsNotExistent(UUID playerId, String name) {
        return fail(HomeError.HOME_NOT_FOUND, "Home with name " + name + " does not exist for player " + playerId);
    }

    private static <T> T fail(HomeError error, String message) {
        throw new HomeException(error, message);
    }
}
