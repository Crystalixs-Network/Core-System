package net.crystalixs.core.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jakarta.inject.Inject;
import net.crystalixs.core.common.config.ObjectMapperFactory;
import net.crystalixs.core.velocity.command.CoreCommand;
import net.crystalixs.core.velocity.command.HelpCommand;
import net.crystalixs.core.velocity.command.MaintenanceCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import net.crystalixs.core.velocity.config.jackson.JacksonVelocity;
import net.crystalixs.core.velocity.config.translation.VelocityTranslationRegistry;
import net.crystalixs.core.velocity.listener.MotdListener;
import net.crystalixs.core.velocity.listener.PlayerConnectionListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.AudienceProvider;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class CorePlugin {

    private final MiniMessage miniMessage = MiniMessage.builder()
            .editTags(builder -> builder.tag("prefix", Tag.inserting(Component.translatable("util.prefix"))))
            .build();

    private final PluginContainer pluginContainer;
    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger logger;

    private VelocityConfigLoader loader;
    private VelocityConfig config;

    @Inject
    public CorePlugin(PluginContainer pluginContainer, ProxyServer server, @DataDirectory Path dataDirectory, Logger logger) {
        this.pluginContainer = pluginContainer;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        createOrLoadConfig();
        registerCommands();
        registerTranslations();
        registerListener(server);

        logger.info("Velocity core plugin has been enabled!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        loader.save();
        logger.info("Velocity core plugin has been disabled!");
    }

    private void registerListener(ProxyServer server) {
        server.getEventManager().register(this, new MotdListener(config));
        server.getEventManager().register(this, new PlayerConnectionListener(config));
    }

    private void registerCommands() {
        final VelocityCommandManager<VelocityCommandSource> commandManager = new VelocityCommandManager<>(pluginContainer, server, ExecutionCoordinator.simpleCoordinator(), senderMapper());
        final MinecraftHelp<VelocityCommandSource> help = MinecraftHelp.<VelocityCommandSource>builder()
                .commandManager(commandManager)
                .audienceProvider(AudienceProvider.nativeAudience())
                .commandPrefix("/help")
                .build();

        MinecraftExceptionHandler.<VelocityCommandSource>createNative()
                .defaultHandlers()
                .decorator(component -> text().append(translatable("util.prefix")).append(component).build())
                .registerTo(commandManager);

        // Hier commands registrieren
        new CoreCommand(this, loader).registerTo(commandManager);
        new MaintenanceCommand(this, config).registerTo(commandManager);
        new HelpCommand(this, help).registerTo(commandManager);
    }

    private @NotNull SenderMapper<CommandSource, VelocityCommandSource> senderMapper() {
        return SenderMapper.create(
                source -> source instanceof Player player
                        ? new VelocityPlayerCommandSource(player)
                        : new VelocityCommandSource(source),

                VelocityCommandSource::plattformSender);
    }

    private void createOrLoadConfig() {
        JacksonVelocity jacksonVelocity = JacksonVelocity.builder().withMiniMessage(miniMessage).build();
        ObjectMapper mapper = ObjectMapperFactory.create(builder -> builder.addModule(jacksonVelocity));

        loader = new VelocityConfigLoader(mapper, logger, dataDirectory.resolve("config.json"));
        loader.reload();
        config = loader.get();
    }

    private void registerTranslations() {
        final VelocityTranslationRegistry registry = new VelocityTranslationRegistry(dataDirectory.resolve("lang"), getClass().getClassLoader(), Locale.GERMAN, miniMessage);

        try {
            registry.registerBundle("messages", List.of(Locale.GERMAN));
        } catch (IOException exception) {
            logger.severe("There was an error while registering translations: " + exception.getMessage());
        }
    }
}

