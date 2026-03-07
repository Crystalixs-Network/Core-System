package net.crystalixs.core.common.translation;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.logging.LogMetadata;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class HotReloadWatcher implements Runnable, AutoCloseable {

    private final StructuredLogger logger;
    private final ScheduledExecutorService scheduler;
    private final Path directory;
    private final long debounce;
    private final Runnable callback;

    private volatile boolean isRunning = true;
    private long lastReload = 0L;
    private WatchService watchService;

    public HotReloadWatcher(StructuredLogger logger, ScheduledExecutorService scheduler, Path directory, long debounce, Runnable callback) {
        this.logger = logger.child("hot-reload");
        this.scheduler = scheduler;
        this.directory = directory;
        this.debounce = debounce;
        this.callback = callback;
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(this, 0, 500, TimeUnit.MILLISECONDS);
        logger.info("Started translation hot reload watcher", LogMetadata.of("directory", directory).and("debounceMs", debounce));
    }

    public void stop() {
        isRunning = false;
        try {
            if (watchService != null) watchService.close();
        } catch (IOException exception) {
            logger.warn("Failed to close WatchService", LogMetadata.of("directory", directory), exception);
        }
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    public void run() {
        if (!isRunning) return;
        try {
            if (watchService == null) {
                Files.createDirectories(directory);
                watchService = FileSystems.getDefault().newWatchService();
                directory.register(watchService, ENTRY_MODIFY);
            }

            WatchKey key;
            while ((key = watchService.poll()) != null) {
                for (var event : key.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (!changed.toString().endsWith(".conf")) continue;

                    long now = System.currentTimeMillis();
                    if (now - lastReload > debounce) {
                        logger.info("Detected translation change", LogMetadata.of("file", changed).and("directory", directory));
                        callback.run();
                        lastReload = now;
                    }
                }
                key.reset();
            }
        } catch (IOException exception) {
            logger.error("Failed while observing translation directory", LogMetadata.of("directory", directory), exception);
        }
    }
}
