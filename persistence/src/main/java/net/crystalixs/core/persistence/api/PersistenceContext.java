package net.crystalixs.core.persistence.api;

import net.crystalixs.core.persistence.store.AuditStore;
import net.crystalixs.core.persistence.store.HomeStore;
import net.crystalixs.core.persistence.store.PlayerStore;
import net.crystalixs.core.persistence.store.TransactionStore;

public interface PersistenceContext extends AutoCloseable {

    PlayerStore players();

    HomeStore homes();

    TransactionStore transactions();

    AuditStore audits();

    @Override
    void close();

}
