package net.crystalixs.core.persistence.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.Currency;
import net.crystalixs.core.persistence.model.PlayerModel;
import net.crystalixs.core.persistence.store.PlayerStore;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class DefaultPlayerStore implements PlayerStore {

    private final StructuredLogger logger;
    private final QueryConfiguration config;

    public DefaultPlayerStore(StructuredLogger logger, DataSource dataSource) {
        this.logger = logger;
        this.config = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
    }

    @Override
    public Optional<PlayerModel> findById(UUID playerId) {
        try {
            return config.query("SELECT * FROM player WHERE uuid = ?;")
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
            return findById(playerId).isPresent();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.exists_failed", playerId, "Could not check if player exists", exception);
        }
    }

    @Override
    public void create(UUID playerId) {
        try {
            config.query("INSERT INTO player (uuid) VALUES (?);")
                    .single(call().bind(playerId.toString()))
                    .insert();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.create_failed", playerId, "Could not create player", exception);
        }
    }

    @Override
    public void update(PlayerModel model) {
        try {
            config.query("UPDATE player SET playtime = ?, coins = ?, gems = ? WHERE uuid = ?;")
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
            return config.query("DELETE FROM player WHERE uuid = ?")
                    .single(call().bind(playerId.toString()))
                    .delete()
                    .changed();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.delete_failed", playerId, "Could not delete player", exception);
        }
    }

    @Override
    public void addCurrency(UUID playerId, Currency currency, long amount) {
        String column = column(currency);
        try {
            config.query("UPDATE player SET " + column + " = " + column + " + ? WHERE uuid = ?;")
                    .single(call()
                            .bind(amount)
                            .bind(playerId.toString()))
                    .update();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.addCurrency_failed", playerId, "Could not add currency", exception);
        }
    }

    @Override
    public void setCurrency(UUID playerId, Currency currency, long amount) {
        String column = column(currency);
        try {
            config.query("UPDATE player SET " + column + " = ? WHERE uuid = ?;")
                    .single(call()
                            .bind(amount)
                            .bind(playerId.toString()))
                    .update();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.set_currency_failed", playerId, "Could not set currency", exception);
        }
    }

    @Override
    public boolean takeCurrency(UUID playerId, Currency currency, long amount) {
        String column = column(currency);
        try {
            return config.query("UPDATE player SET " + column + " = " + column + " - ? WHERE uuid = ? AND " + column + " >= ?;")
                    .single(call()
                            .bind(amount)
                            .bind(playerId.toString())
                            .bind(amount))
                    .update()
                    .changed();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.take_currency_failed", playerId, "Could not take currency", exception);
        }
    }

    @Override
    public boolean transferCoins(UUID fromPlayerId, UUID toPlayerId, long amount) {
        try {
            return config.query("""
                            UPDATE player
                            SET coins = CASE
                                WHEN uuid = ? THEN coins - ?
                                WHEN uuid = ? THEN coins + ?
                                ELSE coins
                            END
                            WHERE uuid IN (?, ?)
                              AND (SELECT COUNT(*) FROM player WHERE uuid IN (?, ?)) = 2
                              AND (SELECT coins FROM player WHERE uuid = ?) >= ?;
                            """)
                    .single(call()
                            .bind(fromPlayerId.toString())
                            .bind(amount)
                            .bind(toPlayerId.toString())
                            .bind(amount)
                            .bind(fromPlayerId.toString())
                            .bind(toPlayerId.toString())
                            .bind(fromPlayerId.toString())
                            .bind(toPlayerId.toString())
                            .bind(fromPlayerId.toString())
                            .bind(amount)
                    )
                    .update()
                    .changed();
        } catch (RuntimeException exception) {
            throw failure("persistence.player.transfer_failed", fromPlayerId, "Could not transfer coins", exception);
        }
    }

    private String column(Currency currency) {
        return switch (currency) {
            case COINS -> "coins";
            case GEMS -> "gems";
        };
    }

    private PersistenceException failure(String event, UUID playerId, String message, RuntimeException exception) {
        logger.warn(event, LogMetadata
                .event(event)
                .and(LogMetadata.Key.SUBJECT, "player:" + playerId), exception);
        return new PersistenceException(message, exception);
    }
}
