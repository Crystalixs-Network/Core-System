package net.crystalixs.core.persistence.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.HomeModel;
import net.crystalixs.core.persistence.store.HomeStore;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class DefaultHomeStore implements HomeStore {

    private final StructuredLogger logger;
    private final QueryConfiguration config;

    public DefaultHomeStore(StructuredLogger logger, DataSource dataSource) {
        this.logger = logger;
        this.config = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
    }

    @Override
    public Optional<HomeModel> findById(long homeId) {
        try {
            return config.query("SELECT * FROM homes WHERE id = ?;")
                    .single(call().bind(homeId))
                    .map(HomeModel.map())
                    .first();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.find_failed", "home:" + homeId, "Could not load home", exception);
        }
    }

    @Override
    public Optional<HomeModel> findByPlayerAndName(UUID playerId, String name) {
        try {
            return config.query("SELECT * FROM homes WHERE player_id = ? AND name = ?;")
                    .single(call()
                            .bind(playerId.toString())
                            .bind(name))
                    .map(HomeModel.map())
                    .first();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.find_by_player_name_failed", subject(playerId, name), "Could not load home", exception);
        }
    }

    @Override
    public List<HomeModel> findByPlayerId(UUID playerId) {
        try {
            return config.query("SELECT * FROM homes WHERE player_id = ? ORDER BY name;")
                    .single(call().bind(playerId.toString()))
                    .map(HomeModel.map())
                    .all();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.find_by_player_failed", "player:" + playerId, "Could not load homes", exception);
        }
    }

    @Override
    public HomeModel create(HomeModel model) {
        try {
            long homeId = insertHome(model);
            return findById(homeId).orElseThrow(() -> reloadFailure(model, homeId));
        } catch (RuntimeException exception) {
            throw failure("persistence.home.create_failed", subject(model.playerId(), model.name()), "Could not create home", exception);
        }
    }

    @Override
    public void update(HomeModel model) {
        try {
            config.query("UPDATE homes SET player_id = ?, name = ?, world_name = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?;")
                    .single(call()
                            .bind(model.playerId().toString())
                            .bind(model.name())
                            .bind(model.position().worldName())
                            .bind(model.position().x())
                            .bind(model.position().y())
                            .bind(model.position().z())
                            .bind(model.position().yaw())
                            .bind(model.position().pitch())
                            .bind(model.id()))
                    .update();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.update_failed", "home:" + model.id(), "Could not update home", exception);
        }
    }

    @Override
    public void rename(UUID playerId, String oldName, String newName) {
        try {
            config.query("UPDATE homes SET name = ? WHERE player_id = ? AND name = ?;")
                    .single(call()
                            .bind(newName)
                            .bind(playerId.toString())
                            .bind(oldName))
                    .update();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.rename_failed", "player: " + playerId + ", from: " + oldName + ", to: " + newName, "Could not rename home", exception);
        }
    }

    @Override
    public boolean deleteById(long homeId) {
        try {
            return config.query("DELETE FROM homes WHERE id = ?;")
                    .single(call().bind(homeId))
                    .delete()
                    .changed();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.delete_failed", "home:" + homeId, "Could not delete home", exception);
        }
    }

    @Override
    public boolean deleteByPlayerAndName(UUID playerId, String name) {
        try {
            return config.query("DELETE FROM homes WHERE player_id = ? AND name = ?;")
                    .single(call()
                            .bind(playerId.toString())
                            .bind(name))
                    .delete()
                    .changed();
        } catch (RuntimeException exception) {
            throw failure("persistence.home.delete_by_player_name_failed", subject(playerId, name), "Could not delete home", exception);
        }
    }

    private long insertHome(HomeModel model) {
        return config.query("INSERT INTO homes (player_id, name, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?);")
                .single(call()
                        .bind(model.playerId().toString())
                        .bind(model.name())
                        .bind(model.position().worldName())
                        .bind(model.position().x())
                        .bind(model.position().y())
                        .bind(model.position().z())
                        .bind(model.position().yaw())
                        .bind(model.position().pitch()))
                .insertAndGetKeys()
                .keys()
                .stream()
                .findFirst()
                .orElseThrow(() -> new PersistenceException(
                        "Could not read generated home key",
                        new IllegalStateException(subject(model.playerId(), model.name()))
                ));
    }

    private PersistenceException failure(String event, String subject, String message, RuntimeException exception) {
        logger.warn(event, LogMetadata.event(event).and(LogMetadata.Key.SUBJECT, subject), exception);
        return new PersistenceException(message, exception);
    }

    private PersistenceException reloadFailure(HomeModel model, long homeId) {
        String subject = subject(model.playerId(), model.name());
        IllegalStateException exception = new IllegalStateException("home:" + homeId);
        logger.warn("persistence.home.reload_failed", LogMetadata
                .event("persistence.home.reload_failed")
                .and(LogMetadata.Key.SUBJECT, subject), exception);

        return new PersistenceException("Could not reload created home", exception);
    }

    private static String subject(UUID playerId, String name) {
        return "player:" + playerId + ", home:" + name;
    }
}
