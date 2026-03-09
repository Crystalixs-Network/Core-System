package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.bootstrap.AbstractPluginBootstrap;
import net.crystalixs.core.paper.config.PaperConfig;
import net.crystalixs.core.paper.config.platform.PaperConfigUpdater;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.api.PersistenceContextFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperPluginBootstrap extends AbstractPluginBootstrap<PaperPluginRuntime> {

    private final PaperConfigUpdater config;
    private final PersistenceContext persistence;
    private final PaperTranslationBootstrap translations;
    private final PaperCommandBootstrap commands;

    private PaperPluginBootstrap(
            PaperPluginRuntime runtime,
            PaperConfigUpdater config,
            PersistenceContext persistence,
            PaperTranslationBootstrap translations,
            PaperCommandBootstrap commands
    ) {
        super(runtime);
        this.config = config;
        this.persistence = persistence;
        this.translations = translations;
        this.commands = commands;
    }

    public static PaperPluginBootstrap create(JavaPlugin plugin) {
        PaperPluginRuntime runtime = PaperPluginRuntime.create(plugin);
        PaperConfigUpdater config = new PaperConfigBootstrap().load(runtime);
        PersistenceContext persistence = PersistenceContextFactory.create(runtime.logger(), toDatabaseCredentials(config.current()));
        PaperTranslationBootstrap translations = PaperTranslationBootstrap.create(runtime);
        PaperCommandBootstrap commands = new PaperCommandBootstrap(runtime);

        return new PaperPluginBootstrap(runtime, config, persistence, translations, commands);
    }

    @Override
    protected void enableInternal() {
        commands.registerCommands();
    }

    @Override
    protected void disableInternal() {
        try {
            translations.close();
        } finally {
            persistence.close();
        }
    }

    public PaperConfigUpdater config() {
        return config;
    }

    public PersistenceContext persistence() {
        return persistence;
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
