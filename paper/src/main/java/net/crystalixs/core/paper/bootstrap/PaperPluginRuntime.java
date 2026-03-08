package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.LogManager;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class PaperPluginRuntime implements AutoCloseable {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().build();

    private final JavaPlugin plugin;
    private final ScheduledExecutorService scheduler;
    private final LogFactory logFactory;
    private final StructuredLogger logger;

    private PaperPluginRuntime(JavaPlugin plugin,
                               ScheduledExecutorService scheduler,
                               LogFactory logFactory,
                               StructuredLogger logger) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.logFactory = logFactory;
        this.logger = logger;
    }

    public static PaperPluginRuntime create(JavaPlugin plugin) {
        LogFactory factory = LogManager.createForJavaUtil(plugin.getLogger(), plugin.getDataPath().resolve("logs"));
        return new PaperPluginRuntime(plugin,
                Executors.newSingleThreadScheduledExecutor(),
                factory, factory.logger("core"));
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public ScheduledExecutorService scheduler() {
        return scheduler;
    }

    public MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }

    public StructuredLogger logger() {
        return logger;
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
        logFactory.close();
    }
}
