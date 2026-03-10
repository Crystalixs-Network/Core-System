package net.crystalixs.core.persistence.api;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import net.crystalixs.core.persistence.defaults.DefaultPersistenceContext;

public interface PersistenceContextFactory {

    static PersistenceContext create(StructuredLogger logger, DatabaseCredentials credentials) {
        return new DefaultPersistenceContext(logger, credentials);
    }
}
