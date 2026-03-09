package net.crystalixs.core.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mariadb.databases.MariaDb;
import org.mariadb.jdbc.Driver;

public final class DataSourceFactory {

    public HikariDataSource create(DatabaseCredentials credentials, StructuredLogger logger) {
        logger.info("creating", credentialsMetadata("persistence.datasource.create", credentials));
        try {
            HikariDataSource dataSource = DataSourceCreator.create(MariaDb.get())
                    .configure(jdbc -> jdbc
                            .host(credentials.host())
                            .port(credentials.port())
                            .user(credentials.username())
                            .password(credentials.password())
                            .database(credentials.database())
                            .driverClass(Driver.class)
                    )
                    .create()
                    .withMaximumPoolSize(10)
                    .withMinimumIdle(2)
                    .build();

            logger.info("created", credentialsMetadata("persistence.datasource.created", credentials));
            return dataSource;

        } catch (RuntimeException exception) {
            logger.error("create_failed", credentialsMetadata("persistence.datasource.create_failed", credentials), exception);
            throw new IllegalStateException("Could not create MariaDB data source", exception);
        }
    }

    private static LogMetadata credentialsMetadata(String event, DatabaseCredentials credentials) {
        return LogMetadata
                .event(event)
                .and(LogMetadata.Key.CREDENTIALS, credentials.host() + ":" + credentials.port() + "/" + credentials.database() + " as " + credentials.username());
    }
}
