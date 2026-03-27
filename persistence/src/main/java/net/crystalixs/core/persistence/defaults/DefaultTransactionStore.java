package net.crystalixs.core.persistence.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.TransactionModel;
import net.crystalixs.core.persistence.store.TransactionStore;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class DefaultTransactionStore implements TransactionStore {

    private final StructuredLogger logger;
    private final QueryConfiguration config;

    public DefaultTransactionStore(StructuredLogger logger, DataSource source) {
        this.logger = logger;
        this.config = QueryConfiguration.builder(source).setThrowExceptions(true).build();
    }

    @Override
    public void create(TransactionModel model) {
        try {
            config.query("""
                            INSERT INTO economy_transactions
                            (type, currency, amount, from_player_uuid, to_player_uuid, actor_player_uuid, reason)
                            VALUES (?, ?, ?, ?, ?, ?, ?);
                            """
                    )
                    .single(call()
                            .bind(model.type().name())
                            .bind(model.currency().name())
                            .bind(model.amount())
                            .bind(model.fromPlayerId() == null ? null : model.fromPlayerId().toString())
                            .bind(model.toPlayerId() == null ? null : model.toPlayerId().toString())
                            .bind(model.actorPlayerId() == null ? null : model.actorPlayerId().toString())
                            .bind(model.reason())
                    )
                    .insert();

        } catch (RuntimeException exception) {
            throw fail("persistence.economy.transaction.create_failed", "Could not create transaction", exception);
        }
    }

    @Override
    public List<TransactionModel> findByPlayer(UUID playerId, int limit) {
        try {
            int safeLimit = Math.clamp(limit, 1, 500);
            return config.query("""
                            SELECT *
                            FROM economy_transactions
                            WHERE from_player_uuid = ? OR to_player_uuid = ? OR actor_player_uuid = ?
                            ORDER BY created_at DESC
                            LIMIT ?;
                            """)
                    .single(call()
                            .bind(playerId.toString())
                            .bind(playerId.toString())
                            .bind(playerId.toString())
                            .bind(safeLimit)
                    )
                    .map(TransactionModel.map())
                    .all();

        } catch (RuntimeException exception) {
            throw fail("persistence.economy.transaction.find_by_player_failed", "Could not find transactions", exception);
        }
    }

    private PersistenceException fail(String event, String message, RuntimeException exception) {
        logger.warn(event, LogMetadata.event(event), exception);
        return new PersistenceException(message, exception);
    }
}
