package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.bootstrap.AbstractPluginBootstrap;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.persistence.api.PersistenceContext;
import net.crystalixs.core.persistence.api.PersistenceContextFactory;
import net.crystalixs.core.persistence.config.DatabaseCredentials;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class VelocityPluginBootstrap extends AbstractPluginBootstrap<VelocityPluginRuntime> {

    private final CorePlugin plugin;
    private final VelocityConfigBootstrap configBootstrap;
    private final VelocityCommandBootstrap commandBootstrap;
    private final VelocityListenerBootstrap listenerBootstrap;
    private final BackendHelpCatalogCache backendHelpCache;

    private VelocityConfigUpdater configUpdater;
    private VelocityTranslationBootstrap translationBootstrap;
    private PersistenceContext persistenceContext;

    private VelocityPluginBootstrap(CorePlugin plugin,
                                    VelocityPluginRuntime runtime,
                                    VelocityConfigBootstrap configBootstrap,
                                    VelocityCommandBootstrap commandBootstrap,
                                    VelocityListenerBootstrap listenerBootstrap,
                                    BackendHelpCatalogCache backendHelpCache) {
        super(runtime);
        this.plugin = plugin;
        this.configBootstrap = configBootstrap;
        this.commandBootstrap = commandBootstrap;
        this.listenerBootstrap = listenerBootstrap;
        this.backendHelpCache = backendHelpCache;
    }

    public static VelocityPluginBootstrap create(CorePlugin plugin, PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, Logger platformLogger) {
        return new VelocityPluginBootstrap(plugin, VelocityPluginRuntime.create(pluginContainer, server, dataDirectory, platformLogger),
                new VelocityConfigBootstrap(),
                new VelocityCommandBootstrap(),
                new VelocityListenerBootstrap(),
                new BackendHelpCatalogCache());
    }

    @Override
    protected void enableInternal() {
        VelocityPluginRuntime runtime = runtime();
        configUpdater = configBootstrap.load(runtime);
        persistenceContext = PersistenceContextFactory.create(
                runtime().logger(),
                new DatabaseCredentials(
                        configUpdater.current().database().host(),
                        configUpdater.current().database().port(),
                        configUpdater.current().database().database(),
                        configUpdater.current().database().username(),
                        configUpdater.current().database().password()
                )
        );
        translationBootstrap = VelocityTranslationBootstrap.create(runtime, configUpdater);
        commandBootstrap.register(plugin, runtime, configUpdater, translationBootstrap.provider(), backendHelpCache);
        listenerBootstrap.register(plugin, runtime, configUpdater, backendHelpCache);
    }

    @Override
    protected void disableInternal() {
        VelocityPluginRuntime runtime = runtime();
        try {
            if (translationBootstrap != null) translationBootstrap.close();
            if (persistenceContext != null) {
                try {
                    persistenceContext.playerSettings().resetAllVanishFlags();
                } catch (RuntimeException exception) {
                    runtime.componentLogger("shutdown").warn(
                            "failed to reset persisted vanish flags during proxy shutdown",
                            LogMetadata.event("velocity.shutdown.vanish_reset.failed"), exception);
                }
            }
        } finally {
            try {
                listenerBootstrap.close();
            } finally {
                if (persistenceContext != null) persistenceContext.close();
                configBootstrap.save(runtime, configUpdater);
            }
        }
    }
}
