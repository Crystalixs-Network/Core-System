package net.crystalixs.core.persistence.migration;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.updater.SqlUpdater;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.config.DataSourceFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public final class MigrationRunner {

    private static final String VERSION_TABLE = "core_schema_version";
    private static final Pattern DATABASE_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{1,64}$");
    private static final String CREATE_DATABASE_TEMPLATE =
            "CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;";

    public HikariDataSource migrate(DatabaseCredentials credentials, StructuredLogger logger) {
        logger.info("starting", metadata("persistence.migration.starting", credentials));

        // Establish a connection to the admin database to ensure the database exists or create it
        final HikariDataSource adminDatasource;
        try {
            adminDatasource = DataSourceFactory.createAdmin(credentials);
        } catch (RuntimeException exception) {
            logger.error("admin datasource init failed", metadata("persistence.migration.admin_datasource.init_failed", credentials), exception);
            throw new IllegalStateException("Could not initialize admin datasource for schema bootstrap", exception);
        }

        // Ensure the database exists or create it
        final String safeDatabaseName;
        try {
            safeDatabaseName = requireValidDatabaseName(credentials.database());
        } catch (IllegalArgumentException exception) {
            logger.error("invalid database name", metadata("persistence.migration.database.invalid_name", credentials), exception);
            throw exception;
        }

        try (HikariDataSource adminSource = adminDatasource) {
            ensureDatabaseExists(adminSource, safeDatabaseName);
            logger.info("schema ensured", metadata("persistence.migration.schema.ensured", credentials));

        } catch (SQLException exception) {
            logger.error("admin connection failed", metadata("persistence.migration.admin_connection.failed", credentials), exception);
            throw new IllegalStateException("Could not connect with admin datasource for schema bootstrap", exception);
        }

        // Establish a connection to the runtime database for schema migration
        final HikariDataSource source;
        try {
            source = DataSourceFactory.create(credentials);
        } catch (RuntimeException exception) {
            logger.error("runtime datasource init failed", metadata("persistence.migration.runtime_datasource.init_failed", credentials), exception);
            throw new IllegalStateException("Could not initialize runtime datasource for schema migration", exception);
        }

        // Run the migration.
        // Catching Exception is intentional here: SqlUpdater can throw checked and unchecked failures.
        // We must always close the runtime datasource on any failure path.
        try {
            SqlUpdater.builder(source, MariaDb.get())
                    .withClassLoader(getClass().getClassLoader())
                    .setVersionTable(VERSION_TABLE)
                    .execute();

            logger.info("completed", metadata("persistence.migration.completed", credentials));
            return source;

        } catch (Exception exception) {
            source.close();
            logger.error("failed", metadata("persistence.migration.failed", credentials), exception);
            throw new IllegalStateException("Could not execute database migration", exception);
        }
    }

    private void ensureDatabaseExists(DataSource adminDataSource, String safeDatabaseName) throws SQLException {
        final String createDatabaseQuery = createDatabaseSql(safeDatabaseName);
        try (Connection connection = adminDataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(createDatabaseQuery);
        }
    }

    private String requireValidDatabaseName(String database) {
        final String normalized = database == null ? null : database.trim();
        if (database == null || !DATABASE_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid database name: " + database);
        }
        return normalized;
    }

    private String createDatabaseSql(String database) {
        return CREATE_DATABASE_TEMPLATE.formatted(database);
    }

    private LogMetadata metadata(String event, DatabaseCredentials credentials) {
        final String database = credentials.database() == null ? "<null>" : credentials.database().trim();
        return LogMetadata
                .event(event)
                .and(LogMetadata.Key.CREDENTIALS, credentials.host() + ":" + credentials.port() + "/" + database)
                .and(LogMetadata.Key.VERSION_TABLE, VERSION_TABLE);
    }
}
