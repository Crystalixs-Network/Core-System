package net.crystalixs.core.persistence.internal.sadu;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.PlayerModel;
import net.crystalixs.core.persistence.store.PlayerStore;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class SaduPlayerStore implements PlayerStore {

    private final QueryConfiguration queryConfiguration;
    private final StructuredLogger logger;

    public SaduPlayerStore(DataSource dataSource, StructuredLogger logger) {
        this.queryConfiguration = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
        this.logger = logger;
    }

    @Override
    public Optional<PlayerModel> findById(UUID playerId) {
        try {
            return queryConfiguration.query("""
                    SELECT uuid, playtime, coins, gems
                    FROM player
                    WHERE uuid = ?
                    """)
                    .single(call().bind(playerId.toString()))
                    .map(PlayerModel.map())
                    .first();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.find_failed", playerId, "Could not load player", exception);
        }
    }

    @Override
    public boolean exists(UUID playerId) {
        try {
            return queryConfiguration.query("""
                    SELECT 1
                    FROM player
                    WHERE uuid = ?
                    """)
                    .single(call().bind(playerId.toString()))
                    .map(row -> true)
                    .first()
                    .orElse(false);
        } catch (RuntimeException exception) {
            throw failure("persistence.player.exists_failed", playerId, "Could not check if player exists", exception);
        }
    }

    @Override
    public void create(PlayerModel model) {
        try {
            queryConfiguration.query("""
                    INSERT INTO player (uuid, playtime, coins, gems)
                    VALUES (?, ?, ?, ?)
                    """)
                    .single(call()
                            .bind(model.uuid().toString())
                            .bind(model.playtime())
                            .bind(model.coins())
                            .bind(model.gems()))
                    .insert();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.create_failed", model.uuid(), "Could not create player", exception);
        }
    }

    @Override
    public void update(PlayerModel model) {
        try {
            queryConfiguration.query("""
                    UPDATE player
                    SET playtime = ?, coins = ?, gems = ?
                    WHERE uuid = ?
                    """)
                    .single(call()
                            .bind(model.playtime())
                            .bind(model.coins())
                            .bind(model.gems())
                            .bind(model.uuid().toString()))
                    .update();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.update_failed", model.uuid(), "Could not update player", exception);
        }
    }

    @Override
    public boolean delete(UUID playerId) {
        try {
            return queryConfiguration.query("""
                    DELETE FROM player
                    WHERE uuid = ?
                    """)
                    .single(call().bind(playerId.toString()))
                    .delete()
                    .changed();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.delete_failed", playerId, "Could not delete player", exception);
        }
    }

    private PersistenceException failure(String event, UUID playerId, String message, RuntimeException exception) {
        logger.error(event, LogMetadata
                .event(event)
                .and(LogMetadata.Key.SUBJECT, "player:" + playerId), exception);

        return new PersistenceException(message, exception);
    }
}
