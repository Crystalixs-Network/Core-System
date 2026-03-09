package net.crystalixs.core.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mariadb.databases.MariaDb;
import org.mariadb.jdbc.Driver;


public final class DataSourceFactory {

    public HikariDataSource create(DatabaseCredentials credentials) {
        return DataSourceCreator.create(MariaDb.get())
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
    }

}
