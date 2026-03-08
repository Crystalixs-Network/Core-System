package net.crystalixs.core.common.bootstrap;

import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.StructuredLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractPluginRuntime implements AutoCloseable {

    private final ScheduledExecutorService scheduler;
    private final LogFactory logFactory;
    private final StructuredLogger logger;

    protected AbstractPluginRuntime(LogFactory logFactory) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.logFactory = logFactory;
        this.logger = logFactory.logger("core");
    }

    public ScheduledExecutorService scheduler() {
        return scheduler;
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
