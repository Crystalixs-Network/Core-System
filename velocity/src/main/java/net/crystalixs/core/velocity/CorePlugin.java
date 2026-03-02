package net.crystalixs.core.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.eldoria.jacksonbukkit.JacksonPaper;
import jakarta.inject.Inject;
import net.crystalixs.core.common.config.ObjectMapperFactory;
import net.crystalixs.core.velocity.config.Motd;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.newline;

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
        ObjectMapper mapper = ObjectMapperFactory.create(builder -> builder.addModule(JacksonPaper.builder().build()));
        VelocityConfigLoader loader = new VelocityConfigLoader(mapper, logger, dataDirectory.resolve("config.json"));
        loader.reload();

        config = loader.get();

        server.getEventManager().register(this, this);
        logger.info("Velocity core plugin has been enabled!");
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        Motd motd = config.motd();

        ServerPing ping = event.getPing().asBuilder()
                .description(motd.firstLine().append(newline()).append(motd.secondLine()))
                .build();

        event.setPing(ping);
    }

}

