package net.crystalixs.core.persistence.migration;

import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.updater.SqlUpdater;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public final class MigrationRunner {

    private static final String VERSION_TABLE = "core_schema_version";

    public void run(DataSource source, StructuredLogger logger) {
        logger.info("starting", LogMetadata
                .event("persistence.migration.start")
                .and(LogMetadata.Key.SUBJECT, VERSION_TABLE));
        try {
            SqlUpdater.builder(source, MariaDb.get())
                    .setVersionTable(VERSION_TABLE)
                    .execute();
            logger.info("completed", LogMetadata
                    .event("persistence.migration.completed")
                    .and(LogMetadata.Key.SUBJECT, VERSION_TABLE));

        } catch (IOException | SQLException exception) {
            logger.error("failed", LogMetadata.
                    event("persistence.migration.failed")
                    .and(LogMetadata.Key.SUBJECT, VERSION_TABLE), exception);
            throw new IllegalStateException("Could not execute database migration", exception);
        }
    }

}
