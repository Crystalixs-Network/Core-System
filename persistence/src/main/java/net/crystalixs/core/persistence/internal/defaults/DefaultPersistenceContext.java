package net.crystalixs.core.persistence.internal.defaults;

import com.zaxxer.hikari.HikariDataSource;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.config.DataSourceFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import net.crystalixs.core.persistence.migration.MigrationRunner;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;

public final class DefaultPersistenceContext implements PersistenceContext {

    private final StructuredLogger logger;
    private final HikariDataSource dataSource;
    private final PlayerStore playerStore;
    private final HomeStore homeStore;

    public DefaultPersistenceContext(StructuredLogger logger, DatabaseCredentials credentials) {
        this.logger = logger.child("persistence");
        this.logger.info("initializing", LogMetadata
                .event("persistence.init")
                .and(LogMetadata.Key.CREDENTIALS, credentials.host() + ":" + credentials.port() + "/" + credentials.database()));

        this.dataSource = DataSourceFactory.create(credentials);

        new MigrationRunner().run(dataSource, this.logger.child("migration"));

        this.playerStore = new DefaultPlayerStore(this.logger.child("store").child("player"), dataSource);
        this.homeStore = new DefaultHomeStore(this.logger.child("store").child("home"), dataSource);

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
    public void close() {
        logger.info("closing", LogMetadata.event("persistence.close"));
        dataSource.close();
        logger.info("closed", LogMetadata.event("persistence.closed"));
    }
}
