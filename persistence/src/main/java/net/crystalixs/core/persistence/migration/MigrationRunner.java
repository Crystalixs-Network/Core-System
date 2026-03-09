package net.crystalixs.core.persistence.migration;

import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.updater.SqlUpdater;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public final class MigrationRunner {

    public void run(DataSource source) throws IOException, SQLException {
        SqlUpdater.builder(source, MariaDb.get())
                .setVersionTable("core_schema_version")
                .execute();
    }

}
