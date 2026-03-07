package net.crystalixs.core.velocity.config;

import net.crystalixs.core.common.config.ConfigUpdater;

import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public final class VelocityConfigUpdater {

    private final ConfigUpdater<VelocityConfig> updater;
    private final Logger logger;

    public VelocityConfigUpdater(ConfigUpdater<VelocityConfig> updater, Logger logger) {
        this.updater = updater;
        this.logger = logger;
    }

    public VelocityConfig current() {
        return updater.current();
    }

    public void update(UnaryOperator<VelocityConfig> updater) {
        try {
            this.updater.update(updater);
        } catch (IOException exception) {
            logger.severe("Could not update config: " + exception.getMessage());
        }
    }

    public void enableMaintenance(boolean enabled) {
        update(config -> config.withMaintenance(enabled
                ? config.maintenance().enable()
                : config.maintenance().disable()));
    }
}
