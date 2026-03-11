package net.crystalixs.core.persistence.defaults;

import com.zaxxer.hikari.HikariDataSource;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.config.DataSourceFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import net.crystalixs.core.persistence.migration.MigrationRunner;
import net.crystalixs.core.persistence.store.AuditStore;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;
import net.crystalixs.core.persistence.store.TransactionStore;

public final class DefaultPersistenceContext implements PersistenceContext {

    private final StructuredLogger logger;
    private final HikariDataSource dataSource;
    private final PlayerStore playerStore;
    private final HomeStore homeStore;
    private final TransactionStore transactionStore;
    private final AuditStore auditStore;

    public DefaultPersistenceContext(StructuredLogger logger, DatabaseCredentials credentials) {
        this.logger = logger.child("persistence");
        this.logger.info("initializing", LogMetadata
                .event("persistence.init")
                .and(LogMetadata.Key.CREDENTIALS, credentials.host() + ":" + credentials.port() + "/" + credentials.database()));

        this.dataSource = DataSourceFactory.create(credentials);

        // Run database migration
        new MigrationRunner().run(dataSource, this.logger.child("migration"));

        // Initialize stores
        final StructuredLogger storeLogger = this.logger.child("store");
        this.playerStore = new DefaultPlayerStore(storeLogger.child("player"), dataSource);
        this.homeStore = new DefaultHomeStore(storeLogger.child("home"), dataSource);
        this.transactionStore = new DefaultTransactionStore(storeLogger.child("transaction"), dataSource);
        this.auditStore = new DefaultAuditStore(storeLogger.child("audit"), dataSource);

        this.logger.info("initialized", LogMetadata.event("persistence.started"));
    }

    @Override
    public PlayerStore players() {
        return playerStore;
    }

    @Override
    public HomeStore homes() {
        return homeStore;
    }

    @Override
    public TransactionStore transactions() {
        return transactionStore;
    }

    @Override
    public AuditStore audits() {
        return auditStore;
    }

    @Override
    public void close() {
        logger.info("closing", LogMetadata.event("persistence.close"));
        dataSource.close();
        logger.info("closed", LogMetadata.event("persistence.closed"));
    }
}
