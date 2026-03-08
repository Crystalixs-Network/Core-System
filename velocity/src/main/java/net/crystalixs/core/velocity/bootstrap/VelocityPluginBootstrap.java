package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class VelocityPluginBootstrap {

    private final CorePlugin plugin;
    private final VelocityPluginRuntime runtime;
    private final VelocityConfigBootstrap configBootstrap;
    private final VelocityCommandBootstrap commandBootstrap;
    private final VelocityListenerBootstrap listenerBootstrap;

    private VelocityConfigUpdater configUpdater;
    private VelocityTranslationBootstrap translationBootstrap;

    private VelocityPluginBootstrap(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigBootstrap configBootstrap, VelocityCommandBootstrap commandBootstrap, VelocityListenerBootstrap listenerBootstrap) {
        this.plugin = plugin;
        this.runtime = runtime;
        this.configBootstrap = configBootstrap;
        this.commandBootstrap = commandBootstrap;
        this.listenerBootstrap = listenerBootstrap;
    }

    public static VelocityPluginBootstrap create(CorePlugin plugin, PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, Logger platformLogger) {
        return new VelocityPluginBootstrap(plugin,
                VelocityPluginRuntime.create(pluginContainer, server, dataDirectory, platformLogger),
                new VelocityConfigBootstrap(),
                new VelocityCommandBootstrap(),
                new VelocityListenerBootstrap());
    }

    public void enable() {
        configUpdater = configBootstrap.load(runtime);
        translationBootstrap = VelocityTranslationBootstrap.create(runtime, configUpdater);
        commandBootstrap.register(plugin, runtime, configUpdater, translationBootstrap.provider());
        listenerBootstrap.register(plugin, runtime, configUpdater);

        runtime.logger().info("plugin enabled", LogMetadata.event("plugin.enabled"));
    }

    public void disable() {
        try {
            if (translationBootstrap != null) {
                translationBootstrap.close();
            }
        } finally {
            configBootstrap.save(runtime, configUpdater);
            try {
                runtime.logger().info("plugin disabled", LogMetadata.event("plugin.disabled"));
            } finally {
                runtime.close();
            }
        }
    }

    public VelocityPluginRuntime runtime() {
        return runtime;
    }
}
