package net.crystalixs.core.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import jakarta.inject.Inject;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.bootstrap.VelocityPluginBootstrap;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class CorePlugin {

    private final PluginContainer pluginContainer;
    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger platformLogger;
    private VelocityPluginBootstrap bootstrap;

    @Inject
    public CorePlugin(PluginContainer pluginContainer, ProxyServer server, @DataDirectory Path dataDirectory, Logger platformLogger) {
        this.pluginContainer = pluginContainer;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.platformLogger = platformLogger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        bootstrap = VelocityPluginBootstrap.create(this, pluginContainer, server, dataDirectory, platformLogger);
        bootstrap.enable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (bootstrap != null) {
            bootstrap.disable();
        }
    }

    public StructuredLogger logger() {
        if (bootstrap == null) {
            throw new IllegalStateException("Plugin bootstrap is not available");
        }
        return bootstrap.runtime().logger();
    }

    public StructuredLogger componentLogger(String component, String... nestedComponents) {
        if (bootstrap == null) {
            throw new IllegalStateException("Plugin bootstrap is not available");
        }
        return bootstrap.runtime().componentLogger(component, nestedComponents);
    }

    public StructuredLogger commandLogger(String commandName) {
        return componentLogger("commands", commandName);
    }

    public StructuredLogger listenerLogger(String listenerName) {
        return componentLogger("listeners", listenerName);
    }
}
