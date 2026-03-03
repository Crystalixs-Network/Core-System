package net.crystalixs.core.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import jakarta.inject.Inject;
import net.crystalixs.core.common.config.ObjectMapperFactory;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import net.crystalixs.core.velocity.config.jackson.JacksonVelocity;
import net.crystalixs.core.velocity.listener.MotdListener;
import net.crystalixs.core.velocity.listener.PlayerConnectionListener;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.logging.Logger;

public class CorePlugin {

    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger logger;
    private VelocityConfig config;

    @Inject
    public CorePlugin(ProxyServer server, @DataDirectory Path dataDirectory, Logger logger) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        createOrLoadConfig();
        registerListener(server);

        logger.info("Velocity core plugin has been enabled!");
    }

    private void registerListener(ProxyServer server) {
        server.getEventManager().register(this, new MotdListener(config));
        server.getEventManager().register(this, new PlayerConnectionListener(config));
    }

    private void createOrLoadConfig() {
        JacksonVelocity jacksonVelocity = JacksonVelocity.builder().withMiniMessage().build();
        ObjectMapper mapper = ObjectMapperFactory.create(builder -> builder.addModule(jacksonVelocity));
        VelocityConfigLoader loader = new VelocityConfigLoader(mapper, logger, dataDirectory.resolve("config.json"));
        loader.reload();

        config = loader.get();
    }
}

