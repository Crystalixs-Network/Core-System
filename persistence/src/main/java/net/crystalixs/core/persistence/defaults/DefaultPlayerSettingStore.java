package net.crystalixs.core.persistence.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.PlayerSettingModel;
import net.crystalixs.core.persistence.store.PlayerSettingStore;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class DefaultPlayerSettingStore implements PlayerSettingStore {

    private final StructuredLogger logger;
    private final QueryConfiguration config;

    public DefaultPlayerSettingStore(StructuredLogger logger, DataSource dataSource) {
        this.logger = logger;
        this.config = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
    }

    @Override
    public Optional<PlayerSettingModel> findByPlayerId(UUID playerId) {
        try {
            return config.query("SELECT * FROM player_setting WHERE player_id = ?;")
                    .single(call().bind(playerId.toString()))
                    .map(PlayerSettingModel.map())
                    .first();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.find_failed", playerId, "Could not load player setting", exception);
        }
    }

    @Override
    public void updateIgnoreFlag(UUID playerId, boolean isIgnored) {
    }

    @Override
    public void updateVanishFlag(UUID playerId, boolean isVanished) {
    }

    private PersistenceException failure(String event, UUID playerId, String message, RuntimeException exception) {
        logger.warn(event, LogMetadata
                .event(event)
                .and(LogMetadata.Key.SUBJECT, "player:" + playerId), exception);
        return new PersistenceException(message, exception);
    }
}
