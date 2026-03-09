package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.config.PaperConfig;
import net.crystalixs.core.paper.config.platform.PaperConfigUpdater;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.api.PersistenceContextFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;

public final class PaperPersistenceBootstrap {

    public PersistenceContext create(PaperPluginRuntime runtime, PaperConfigUpdater configUpdater) {
        return PersistenceContextFactory.create(runtime.logger(), toDatabaseCredentials(configUpdater.current()));
    }

    private static DatabaseCredentials toDatabaseCredentials(PaperConfig config) {
        return new DatabaseCredentials(
                config.database().host(),
                config.database().port(),
                config.database().database(),
                config.database().username(),
                config.database().password()
        );
    }
}
