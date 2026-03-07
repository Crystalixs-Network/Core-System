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
import net.crystalixs.core.common.config.legacy.ObjectMapperFactory;
import net.crystalixs.core.common.translation.HotReloadWatcher;
import net.crystalixs.core.common.translation.TranslationBundleMeta;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.command.*;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigLoader;
import net.crystalixs.core.velocity.config.jackson.JacksonVelocity;
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
import tools.jackson.databind.ObjectMapper;

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

    private TranslationProvider provider;
    private HotReloadWatcher watcher;
    private VelocityConfigLoader loader;
    private VelocityConfig config;

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
        if (watcher != null) {
            watcher.stop();
        }
        scheduler.shutdownNow();
        loader.save();

        logger.info("Velocity core plugin has been disabled!");
    }

    private void registerListener(ProxyServer server) {
        server.getEventManager().register(this, new MotdListener(config));
        server.getEventManager().register(this, new PlayerConnectionListener(config));
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
        new CoreCommand(this, loader, provider).registerTo(commandManager);
        new MaintenanceCommand(this, config, server).registerTo(commandManager);
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
        JacksonVelocity jacksonVelocity = JacksonVelocity.builder().withMiniMessage(miniMessage).build();
        ObjectMapper mapper = ObjectMapperFactory.create(builder -> builder.addModule(jacksonVelocity));

        loader = new VelocityConfigLoader(mapper, logger, dataDirectory.resolve("config.json"));
        loader.reload();
        config = loader.get();
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

        if (!config.isHotReloadEnabled()) return;

        watcher = new HotReloadWatcher(scheduler, dataDirectory.resolve("lang"), 1000L, provider::reload);
        watcher.start();
    }
}
