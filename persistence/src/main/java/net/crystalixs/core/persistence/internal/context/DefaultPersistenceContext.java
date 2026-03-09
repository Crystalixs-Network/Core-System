package net.crystalixs.core.persistence.internal.context;

import com.zaxxer.hikari.HikariDataSource;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.config.DataSourceFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import net.crystalixs.core.persistence.internal.sadu.SaduHomeStore;
import net.crystalixs.core.persistence.internal.sadu.SaduPlayerStore;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;

public final class DefaultPersistenceContext implements PersistenceContext {

    private final HikariDataSource dataSource;
    private final PlayerStore playerStore;
    private final HomeStore homeStore;

    public DefaultPersistenceContext(DatabaseCredentials credentials) {
        this.dataSource = DataSourceFactory.create(credentials);
        this.playerStore = new SaduPlayerStore(dataSource);
        this.homeStore = new SaduHomeStore(dataSource);
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
        dataSource.close();
    }
}
