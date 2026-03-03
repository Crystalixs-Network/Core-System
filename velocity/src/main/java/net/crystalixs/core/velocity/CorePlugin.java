package net.crystalixs.core.velocity;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jakarta.inject.Inject;
import net.crystalixs.core.common.config.ObjectMapperFactory;
import net.crystalixs.core.velocity.command.VelocityCommandManagerTypeLiteral;
import net.crystalixs.core.velocity.command.VelocityCommandSource;
import net.crystalixs.core.velocity.command.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import net.crystalixs.core.velocity.config.jackson.JacksonVelocity;
import net.crystalixs.core.velocity.listener.MotdListener;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.CloudInjectionModule;
import org.incendo.cloud.velocity.VelocityCommandManager;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.logging.Logger;

final class CorePlugin {

    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger logger;
    private VelocityConfig config;

    @Inject private Injector injector;

    @Inject
    public CorePlugin(ProxyServer server, @DataDirectory Path dataDirectory, Logger logger) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        createOrLoadConfig();
        registerCommands();
        registerListener(server);

        logger.info("Velocity core plugin has been enabled!");
    }

    private void registerListener(ProxyServer server) {
        server.getEventManager().register(this, new MotdListener(config));
    }

    private void registerCommands() {
        final Injector injector = createInjector();
        final Key<VelocityCommandManager<VelocityCommandSource>> key = Key.get(new VelocityCommandManagerTypeLiteral());
        final VelocityCommandManager<VelocityCommandSource> commandManager = injector.getInstance(key);

        // Hier commands registrieren
    }

    private Injector createInjector() {
        return injector.createChildInjector(new CloudInjectionModule<>(
                VelocityCommandSource.class,
                ExecutionCoordinator.simpleCoordinator(),
                senderMapper()));
    }

    private SenderMapper<CommandSource, VelocityCommandSource> senderMapper() {
        return SenderMapper.create(
                source -> source instanceof Player player
                        ? new VelocityPlayerCommandSource(player)
                        : new VelocityCommandSource(source),

                VelocityCommandSource::plattformSender);
    }

    private void createOrLoadConfig() {
        JacksonVelocity jacksonVelocity = JacksonVelocity.builder().withMiniMessage().build();
        ObjectMapper mapper = ObjectMapperFactory.create(builder -> builder.addModule(jacksonVelocity));
        VelocityConfigLoader loader = new VelocityConfigLoader(mapper, logger, dataDirectory.resolve("config.json"));
        loader.reload();

        config = loader.get();
    }
}

