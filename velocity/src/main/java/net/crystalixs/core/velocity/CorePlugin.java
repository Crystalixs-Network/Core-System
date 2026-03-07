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
import net.crystalixs.core.common.config.ConfigDefinition;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.common.config.ConfigServiceFactory;
import net.crystalixs.core.common.config.ConfigUpdater;
import net.crystalixs.core.common.translation.HotReloadWatcher;
import net.crystalixs.core.common.translation.TranslationBundleMeta;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.command.*;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigUpdater;
import net.crystalixs.core.velocity.config.VelocityConfigurationProvider;
import net.crystalixs.core.velocity.listener.MotdListener;
import net.crystalixs.core.velocity.listener.PlayerConnectionListener;
import net.crystalixs.core.velocity.translation.VelocityTranslationBundleLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class CorePlugin {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final MiniMessage miniMessage = MiniMessage.builder()
            .editTags(builder -> builder.tag("prefix", Tag.inserting(Component.translatable("prefix"))))
            .build();

    private final PluginContainer pluginContainer;
    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger logger;

    private ConfigService<VelocityConfig> configService;
    private ConfigUpdater<VelocityConfig> configUpdater;
    private VelocityConfigUpdater velocityConfigUpdater;
    private VelocityConfig config;
    private TranslationProvider provider;
    private HotReloadWatcher watcher;

    @Inject
    public CorePlugin(PluginContainer pluginContainer, ProxyServer server, @DataDirectory Path dataDirectory) {
        this.pluginContainer = pluginContainer;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = Logger.getLogger(getClass().getSimpleName());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        createOrLoadConfig();
        registerTranslations();
        registerCommands();
        registerListener(server);

        logger.info("Velocity core plugin has been enabled!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        scheduler.shutdownNow();
        if (watcher != null) {
            watcher.stop();
        }
        try {
            configService.save();
        } catch (IOException exception) {
            logger.severe("Could not save config: " + exception.getMessage());
        }

        logger.info("Velocity core plugin has been disabled!");
    }

    private void registerListener(ProxyServer server) {
        server.getEventManager().register(this, new MotdListener(configService));
        server.getEventManager().register(this, new PlayerConnectionListener(configService));
    }

    private void registerCommands() {
        final VelocityCommandManager<VelocityCommandSource> commandManager = new VelocityCommandManager<>
                (pluginContainer, server, ExecutionCoordinator.<VelocityCommandSource>builder().build(), senderMapper());

        MinecraftExceptionHandler.create(VelocityCommandSource::plattformSender)
                .decorator(component -> text().append(translatable("prefix")).append(component).build())
                .defaultHandlers()
                .registerTo(commandManager);

        // Hier commands registrieren
        new ProxyStopCommand(this, server).registerTo(commandManager);
        new CoreCommand(this, configService, provider).registerTo(commandManager);
        new MaintenanceCommand(this, velocityConfigUpdater, server).registerTo(commandManager);
        new HelpCommand(this).registerTo(commandManager);
        new GlobalFindCommand(this).registerTo(commandManager);
        new GlobalTeleportCommand(this).registerTo(commandManager);
        new OnlineCommand(this).registerTo(commandManager);
    }

    private @NotNull SenderMapper<CommandSource, VelocityCommandSource> senderMapper() {
        return SenderMapper.create(
                source -> source instanceof Player player
                        ? new VelocityPlayerCommandSource(player)
                        : new VelocityCommandSource(source),

                VelocityCommandSource::plattformSender);
    }

    private void createOrLoadConfig() {
        try {
            configService = ConfigServiceFactory.create(new ConfigDefinition<>(
                    dataDirectory.resolve("config.json"),
                    "config.json",
                    VelocityConfig.class,
                    logger,
                    new VelocityConfigurationProvider(miniMessage),
                    getClass().getClassLoader()
            ));
            configService.reload();
            configUpdater = ConfigUpdater.create(configService);
            velocityConfigUpdater = new VelocityConfigUpdater(configUpdater, logger);
            config = configService.get();

        } catch (Exception exception) {
            logger.severe("Could not load config: " + exception.getMessage());
        }
    }

    private void registerTranslations() {
        provider = TranslationProvider.builder()
                .withMiniMessage(miniMessage)
                .withLoader(VelocityTranslationBundleLoader.builder()
                        .dataDirectory(dataDirectory)
                        .build()
                )
                .bundle(TranslationBundleMeta.builder()
                        .bundleName("messages")
                        .defaultLocale(Locale.GERMANY)
                        .build()
                )
                .language(Locale.GERMANY)
                .build();

        if (!config.isHotReloadingEnabled()) return;

        watcher = new HotReloadWatcher(scheduler, dataDirectory.resolve("lang"), 1000L, provider::reload);
        watcher.start();
    }
}
