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
import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.LogManager;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.logging.LogMetadata;
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
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    private final LogFactory logging;
    private final StructuredLogger logger;

    private VelocityConfigUpdater configUpdater;
    private TranslationProvider provider;
    private HotReloadWatcher watcher;

    @Inject
    public CorePlugin(PluginContainer pluginContainer, ProxyServer server, @DataDirectory Path dataDirectory, Logger platformLogger) {
        this.pluginContainer = pluginContainer;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logging = LogManager.createForSlf4j(platformLogger, dataDirectory.resolve("logs"));
        this.logger = logging.logger("core");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            createOrLoadConfig();
        } catch (Exception exception) {
            logger.error("config load failed during startup", LogMetadata
                    .event("config.load_failed")
                    .and(LogMetadata.Key.FILE, dataDirectory.resolve("config.json")), exception);

            throw new IllegalStateException("Could not load config", exception);
        }

        registerTranslations();
        registerCommands();
        registerListener(server);

        logger.info("plugin enabled", LogMetadata.event("plugin.enabled"));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        scheduler.shutdownNow();
        if (watcher != null) {
            watcher.close();
        }
        if (configUpdater != null) {
            try {
                configUpdater.save();
            } catch (IOException exception) {
                logger.error("config save failed during shutdown", LogMetadata
                        .event("config.save_failed")
                        .and(LogMetadata.Key.FILE, dataDirectory.resolve("config.json")), exception);
            }
        }

        logger.info("plugin disabled", LogMetadata.event("plugin.disabled"));
        logging.close();
    }

    public StructuredLogger logger() {
        return logger;
    }

    private void registerListener(ProxyServer server) {
        server.getEventManager().register(this, new MotdListener(configUpdater, miniMessage));
        server.getEventManager().register(this, new PlayerConnectionListener(configUpdater, miniMessage));
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
        new CoreCommand(this, configUpdater, provider).registerTo(commandManager);
        new MaintenanceCommand(this, configUpdater, server, miniMessage).registerTo(commandManager);
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

    private void createOrLoadConfig() throws IOException {
        ConfigService<VelocityConfig> configService = ConfigServiceFactory.create(new ConfigDefinition<>(
                dataDirectory.resolve("config.json"), "config.json",
                VelocityConfig.class,
                logger,
                new VelocityConfigurationProvider(miniMessage),
                getClass().getClassLoader()
        ));
        configService.reload();
        configUpdater = new VelocityConfigUpdater(configService);
    }

    private void registerTranslations() {
        provider = TranslationProvider.builder()
                .logger(logger)
                .withMiniMessage(miniMessage)
                .withLoader(new VelocityTranslationBundleLoader(dataDirectory, logger.child("translations")))
                .bundle(TranslationBundleMeta.builder()
                        .bundleName("messages")
                        .defaultLocale(Locale.GERMANY)
                        .build()
                )
                .language(Locale.GERMANY)
                .build();

        if (!configUpdater.current().isHotReloadingEnabled()) return;

        watcher = new HotReloadWatcher(logger, scheduler, dataDirectory.resolve("lang"), 1000L, provider::reload);
        watcher.start();
    }
}
