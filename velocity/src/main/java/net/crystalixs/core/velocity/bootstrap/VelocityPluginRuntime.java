package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.LogManager;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.kyori.adventure.text.Component.translatable;

public final class VelocityPluginRuntime implements AutoCloseable {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .editTags(builder -> builder.tag("prefix", Tag.inserting(translatable("prefix"))))
            .build();

    private final PluginContainer pluginContainer;
    private final ProxyServer server;
    private final Path dataDirectory;
    private final ScheduledExecutorService scheduler;
    private final MiniMessage miniMessage;
    private final LogFactory logging;
    private final StructuredLogger logger;

    private VelocityPluginRuntime(PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, ScheduledExecutorService scheduler, MiniMessage miniMessage, LogFactory logging, StructuredLogger logger) {
        this.pluginContainer = pluginContainer;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.scheduler = scheduler;
        this.miniMessage = miniMessage;
        this.logging = logging;
        this.logger = logger;
    }

    public static VelocityPluginRuntime create(PluginContainer pluginContainer, ProxyServer server, Path dataDirectory, Logger platformLogger) {
        LogFactory logging = LogManager.createForSlf4j(platformLogger, dataDirectory.resolve("logs"));
        return new VelocityPluginRuntime(pluginContainer, server, dataDirectory,
                Executors.newSingleThreadScheduledExecutor(),
                MINI_MESSAGE,
                logging, logging.logger("core"));
    }

    public PluginContainer pluginContainer() {
        return pluginContainer;
    }

    public ProxyServer server() {
        return server;
    }

    public Path dataDirectory() {
        return dataDirectory;
    }

    public ScheduledExecutorService scheduler() {
        return scheduler;
    }

    public MiniMessage miniMessage() {
        return miniMessage;
    }

    public StructuredLogger logger() {
        return logger;
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
        logging.close();
    }
}
