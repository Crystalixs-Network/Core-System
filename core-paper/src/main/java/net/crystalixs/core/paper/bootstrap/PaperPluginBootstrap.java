package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.bootstrap.AbstractPluginBootstrap;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.paper.config.platform.PaperConfigHotReloadWatcher;
import net.crystalixs.core.paper.config.platform.PaperConfigUpdater;
import net.crystalixs.core.paper.display.ScoreboardService;
import net.crystalixs.core.paper.display.TablistService;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.persistence.api.PersistenceContext;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

import java.util.Collection;
import java.util.Locale;

public final class PaperPluginBootstrap extends AbstractPluginBootstrap<PaperPluginRuntime> {

    private final PaperConfigBootstrap config;
    private final PaperPersistenceBootstrap persistence;
    private final PaperTranslationBootstrap translations;
    private final PaperCommandBootstrap commands;
    private final PaperCustomEnchantmentBootstrap enchantments;
    private final PaperListenerBootstrap listeners;

    private PaperConfigUpdater configUpdater;
    private PaperConfigHotReloadWatcher configWatcher;
    private PersistenceContext persistenceContext;
    private ScoreboardService scoreboardService;

    private PaperPluginBootstrap(PaperPluginRuntime runtime,
                                 PaperConfigBootstrap config,
                                 PaperPersistenceBootstrap persistence,
                                 PaperTranslationBootstrap translations,
                                 PaperCommandBootstrap commands,
                                 PaperCustomEnchantmentBootstrap enchantments,
                                 PaperListenerBootstrap listeners
    ) {
        super(runtime);
        this.config = config;
        this.persistence = persistence;
        this.translations = translations;
        this.commands = commands;
        this.enchantments = enchantments;
        this.listeners = listeners;
    }

    public static PaperPluginBootstrap create(JavaPlugin plugin) {
        PaperPluginRuntime runtime = PaperPluginRuntime.create(plugin);
        PaperConfigBootstrap config = new PaperConfigBootstrap();
        PaperPersistenceBootstrap persistence = new PaperPersistenceBootstrap();
        PaperTranslationBootstrap translations = PaperTranslationBootstrap.create(runtime);
        PaperCommandBootstrap commands = new PaperCommandBootstrap(runtime);
        PaperCustomEnchantmentBootstrap enchantments = new PaperCustomEnchantmentBootstrap();
        PaperListenerBootstrap listeners = new PaperListenerBootstrap();

        return new PaperPluginBootstrap(runtime, config, persistence, translations, commands, enchantments, listeners);
    }

    public Locale resolveTranslationLocale(Locale requested) {
        return translations.resolveLocale(requested);
    }

    public Locale defaultTranslationLocale() {
        return translations.defaultLocale();
    }

    @Override
    protected void enableInternal() {
        configUpdater = config.load(runtime());
        persistenceContext = persistence.create(runtime(), configUpdater);

        TablistService tablistService = TablistService.create(runtime().plugin(), runtime().componentLogger("tablist"));
        if (tablistService != null) {
            tablistService.subscribe();
            tablistService.refreshAll();
        } else {
            runtime().componentLogger("display").warn(
                    "tablist service disabled due to missing dependency or setup",
                    LogMetadata.event("display.tablist.disabled"));
        }

        commands.registerCommands(configUpdater);
        scoreboardService = ScoreboardService.create(runtime().plugin(), configUpdater.current(), commands.economyService());

        if (scoreboardService != null) {
            scoreboardService.subscribe();
        } else {
            runtime().componentLogger("display").warn(
                    "scoreboard service disabled because scoreboard config is missing",
                    LogMetadata.event("display.scoreboard.disabled"));
        }

        Collection<CustomEnchantment> customEnchantments = enchantments.createEnchantments();
        listeners.register(
                runtime(),
                commands.sitService(),
                commands.inventorySeeService(),
                commands.vanishService(),
                customEnchantments,
                tablistService,
                scoreboardService
        );

        if (configUpdater.current().isHotReloadingEnabled()) {
            configWatcher = new PaperConfigHotReloadWatcher(
                    runtime().componentLogger("config"),
                    runtime().scheduler(),
                    runtime().plugin().getDataPath().resolve("config.json"),
                    1_000L,
                    () -> {
                        try {
                            configUpdater.reload();
                        } catch (Exception exception) {
                            runtime().componentLogger("config").warn(
                                    "config reload failed via hot-reloading",
                                    LogMetadata.event("config.watch.reload_failed"),
                                    exception);
                        }
                    }
            );
            configWatcher.start();
        }

        InvUI.getInstance().setPlugin(runtime().plugin());
    }

    @Override
    protected void disableInternal() {
        try {
            if (configWatcher != null) {
                configWatcher.close();
            }
            translations.close();
            commands.shutdown();

            if (scoreboardService != null) {
                scoreboardService.shutdown();
            }
        } finally {
            try {
                if (persistenceContext != null) {
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
