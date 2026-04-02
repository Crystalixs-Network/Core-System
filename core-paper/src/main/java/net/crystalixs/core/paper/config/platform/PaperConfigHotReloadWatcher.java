package net.crystalixs.core.paper.config.platform;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PaperConfigHotReloadWatcher implements Runnable, AutoCloseable {

    private final StructuredLogger logger;
    private final ScheduledExecutorService scheduler;
    private final Path directory;
    private final String fileName;
    private final long debounceMs;
    private final Runnable callback;

    private volatile boolean isRunning = true;
    private long lastReloaded = 0L;
    private WatchService watcher;

    public PaperConfigHotReloadWatcher(StructuredLogger logger, ScheduledExecutorService scheduler, Path file, long debounceMs, Runnable callback) {
        this.logger = logger.child("watch");
        this.scheduler = scheduler;
        this.directory = file.getParent();
        this.fileName = file.getFileName().toString();
        this.debounceMs = debounceMs;
        this.callback = callback;
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(this, 0L, 500L, TimeUnit.MILLISECONDS);
        logger.info("config watcher started", LogMetadata
                .event("config.watch.started")
                .and(LogMetadata.Key.DIRECTORY, directory)
                .and(LogMetadata.Key.FILE, fileName)
                .and(LogMetadata.Key.DEBOUNCE_MS, debounceMs));
    }

    @Override
    public void run() {
    }

    @Override
    public void close() throws Exception {
        isRunning = false;
        if (watcher == null) {
            return;
        }
        try {
            watcher.close();
        } catch (IOException ignored) {
        }
    }
}
