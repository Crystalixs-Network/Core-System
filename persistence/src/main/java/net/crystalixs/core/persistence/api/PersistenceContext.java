package net.crystalixs.core.persistence.api;

import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;

public interface PersistenceContext extends AutoCloseable {

    PlayerStore players();

    HomeStore homes();

    @Override
    void close();

}
