package net.crystalixs.core.persistence.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.model.AuditModel;
import net.crystalixs.core.persistence.store.AuditStore;

import javax.sql.DataSource;
import java.util.List;

import static de.chojo.sadu.queries.api.call.Call.call;

public class DefaultAuditStore implements AuditStore {

    private final StructuredLogger logger;
    private final QueryConfiguration config;

    public DefaultAuditStore(StructuredLogger logger, DataSource source) {
        this.logger = logger;
        this.config = QueryConfiguration.builder(source).setThrowExceptions(true).build();
    }

    @Override
    public void create(AuditModel model) {
        try {
            config.query("""
                            INSERT INTO economy_audit (action, payload, status)
                            VALUES (?, ?, ?);
                            """)
                    .single(call()
                            .bind(model.action())
                            .bind(model.payload())
                            .bind(model.status())
                    )
                    .insert();
        } catch (RuntimeException exception) {
            throw fail("persistence.economy.audit.create_failed", "Could not create audit entry", exception);
        }
    }

    @Override
    public List<AuditModel> findByAction(String action, int limit) {
        try {
            int safeLimit = Math.max(1, Math.min(limit, 500));
            return config.query("""
                            SELECT *
                            FROM economy_audit
                            WHERE action = ?
                            ORDER BY created_at DESC
                            LIMIT ?;
                            """)
                    .single(call()
                            .bind(action)
                            .bind(safeLimit)
                    )
                    .map(AuditModel.map())
                    .all();

        } catch (RuntimeException exception) {
            throw fail("persistence.economy.audit.find_by_action_failed", "Could not find audit entries", exception);
        }
    }

    private PersistenceException fail(String event, String message, RuntimeException exception) {
        logger.warn(event, LogMetadata.event(event), exception);
        throw new PersistenceException(message, exception);
    }
}
