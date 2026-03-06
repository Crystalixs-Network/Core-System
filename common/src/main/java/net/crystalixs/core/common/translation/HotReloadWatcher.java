package net.crystalixs.core.common.translation;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class HotReloadWatcher implements Runnable {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final ScheduledExecutorService scheduler;
    private final Path directory;
    private final long debounce;
    private final Runnable callback;

    private volatile boolean isRunning = true;
    private long lastReload = 0L;
    private WatchService watchService;

    public HotReloadWatcher(ScheduledExecutorService scheduler, Path directory, long debounce, Runnable callback) {
        this.scheduler = scheduler;
        this.directory = directory;
        this.debounce = debounce;
        this.callback = callback;
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(this, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        isRunning = false;
        try {
            if (watchService != null) watchService.close();
        } catch (IOException exception) {
            logger.warning("Could not close WatchService: " + exception.getMessage());
        }
    }

    @Override
    public void run() {
        if (!isRunning) return;
        try {
            if (watchService == null) {
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
                        callback.run();
                        lastReload = now;
                    }
                }
                key.reset();
            }
        } catch (IOException exception) {
            logger.severe("There was an error while observing a directory: " + exception.getMessage());
        }
    }
}
