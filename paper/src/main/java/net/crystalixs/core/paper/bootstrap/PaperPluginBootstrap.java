package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.bootstrap.AbstractPluginBootstrap;
import net.crystalixs.core.paper.config.platform.PaperConfigUpdater;
import net.crystalixs.core.persistence.api.PersistenceContext;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperPluginBootstrap extends AbstractPluginBootstrap<PaperPluginRuntime> {

    private final PaperConfigBootstrap config;
    private final PaperPersistenceBootstrap persistence;
    private final PaperTranslationBootstrap translations;
    private final PaperCommandBootstrap commands;
    private PaperConfigUpdater configUpdater;
    private PersistenceContext persistenceContext;

    private PaperPluginBootstrap(
            PaperPluginRuntime runtime,
            PaperConfigBootstrap config,
            PaperPersistenceBootstrap persistence,
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
        PaperConfigBootstrap config = new PaperConfigBootstrap();
        PaperPersistenceBootstrap persistence = new PaperPersistenceBootstrap();
        PaperTranslationBootstrap translations = PaperTranslationBootstrap.create(runtime);
        PaperCommandBootstrap commands = new PaperCommandBootstrap(runtime);

        return new PaperPluginBootstrap(runtime, config, persistence, translations, commands);
    }

    @Override
    protected void enableInternal() {
        configUpdater = config.load(runtime());
        persistenceContext = persistence.create(runtime(), configUpdater);
        commands.registerCommands();
    }

    @Override
    protected void disableInternal() {
        try {
            translations.close();
        } finally {
            try {
                if (persistence != null) {
                    persistenceContext.close();
                }
            } finally {
                config.save(runtime(), configUpdater);
            }
        }
    }

    public PersistenceContext persistence() {
        if (persistence == null) {
            throw new IllegalStateException("Persistence is not available");
        }
        return persistenceContext;
    }
}
