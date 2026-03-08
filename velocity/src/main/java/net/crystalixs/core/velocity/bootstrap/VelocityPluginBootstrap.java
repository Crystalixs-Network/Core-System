package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.bootstrap.AbstractPluginBootstrap;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class VelocityPluginBootstrap extends AbstractPluginBootstrap<VelocityPluginRuntime> {

    private final CorePlugin plugin;
    private final VelocityConfigBootstrap configBootstrap;
    private final VelocityCommandBootstrap commandBootstrap;
    private final VelocityListenerBootstrap listenerBootstrap;

    private VelocityConfigUpdater configUpdater;
    private VelocityTranslationBootstrap translationBootstrap;

    private VelocityPluginBootstrap(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigBootstrap configBootstrap, VelocityCommandBootstrap commandBootstrap, VelocityListenerBootstrap listenerBootstrap) {
        super(runtime);
        this.plugin = plugin;
        this.configBootstrap = configBootstrap;
        this.commandBootstrap = commandBootstrap;
        this.listenerBootstrap = listenerBootstrap;
    }

    public static VelocityPluginBootstrap create(CorePlugin plugin, PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, Logger platformLogger) {
        return new VelocityPluginBootstrap(plugin, VelocityPluginRuntime.create(pluginContainer, server, dataDirectory, platformLogger),
                new VelocityConfigBootstrap(),
                new VelocityCommandBootstrap(),
                new VelocityListenerBootstrap());
    }

    @Override
    protected void enableInternal() {
        VelocityPluginRuntime runtime = runtime();
        configUpdater = configBootstrap.load(runtime);
        translationBootstrap = VelocityTranslationBootstrap.create(runtime, configUpdater);
        commandBootstrap.register(plugin, runtime, configUpdater, translationBootstrap.provider());
        listenerBootstrap.register(plugin, runtime, configUpdater);
    }

    @Override
    protected void disableInternal() {
        VelocityPluginRuntime runtime = runtime();
        try {
            if (translationBootstrap != null) {
                translationBootstrap.close();
            }
        } finally {
            configBootstrap.save(runtime, configUpdater);
        }
    }
}
