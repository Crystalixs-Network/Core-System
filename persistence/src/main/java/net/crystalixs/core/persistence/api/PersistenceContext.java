package net.crystalixs.core.persistence.api;

import net.crystalixs.core.persistence.store.*;

public interface PersistenceContext extends AutoCloseable {

    PlayerStore players();

    PlayerSettingStore playerSettings();

    HomeStore homes();

    TransactionStore transactions();

    AuditStore audits();

    @Override
    void close();

}
