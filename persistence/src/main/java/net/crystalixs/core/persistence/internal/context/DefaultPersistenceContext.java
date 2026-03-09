package net.crystalixs.core.persistence.internal.context;

import com.zaxxer.hikari.HikariDataSource;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.config.DataSourceFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import net.crystalixs.core.persistence.migration.MigrationRunner;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;

import java.util.function.BiFunction;

public final class DefaultPersistenceContext implements PersistenceContext {

    private final HikariDataSource dataSource;
    private final PlayerStore playerStore;
    private final HomeStore homeStore;
    private final StructuredLogger logger;

    public DefaultPersistenceContext(
            StructuredLogger rootLogger,
            DatabaseCredentials credentials,
            BiFunction<HikariDataSource, StructuredLogger, PlayerStore> playerStoreFactory,
            BiFunction<HikariDataSource, StructuredLogger, HomeStore> homeStoreFactory
    ) {

        this.logger = rootLogger.child("persistence");
        this.logger.info("initializing", LogMetadata.event("persistence.initializing")
                .and(LogMetadata.Key.CREDENTIALS, credentials.host() + ":" + credentials.port() + "/" + credentials.database()));

        HikariDataSource createdDataSource = null;
        try {
            StructuredLogger dataSourceLogger = this.logger.child("datasource");
            StructuredLogger migrationLogger = this.logger.child("migration");
            StructuredLogger storeLogger = this.logger.child("store");

            createdDataSource = new DataSourceFactory().create(credentials, dataSourceLogger);
            new MigrationRunner().run(createdDataSource, migrationLogger);

            this.dataSource = createdDataSource;
            this.playerStore = playerStoreFactory.apply(this.dataSource, storeLogger.child("player"));
            this.homeStore = homeStoreFactory.apply(this.dataSource, storeLogger.child("home"));

            this.logger.info("initialized", LogMetadata.event("persistence.initialized"));

        } catch (RuntimeException exception) {
            if (createdDataSource != null) {
                createdDataSource.close();
            }
            this.logger.error("initialization_failed", LogMetadata.event("persistence.initialization_failed"), exception);
            throw exception;
        }
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
    public void close() {
        logger.info("closing", LogMetadata.event("persistence.closing"));
        dataSource.close();
    }
}
