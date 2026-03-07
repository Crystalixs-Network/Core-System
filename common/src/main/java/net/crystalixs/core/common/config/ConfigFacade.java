package net.crystalixs.core.common.config;

import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class ConfigFacade<T> {

    private final ConfigService<T> service;
    private final Logger logger;

    public ConfigFacade(ConfigService<T> service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    public T current() {
        return service.get();
    }

    public void update(UnaryOperator<T> updater) {
        try {
            service.update(updater);
        } catch (IOException exception) {
            logger.severe("Could not update config: " + exception.getMessage());
        }
    }

    public void reload() {
        try {
            service.reload();
        } catch (IOException exception) {
            logger.severe("Could not reload config: " + exception.getMessage());
        }
    }

    public void save() {
        try {
            service.save();
        } catch (IOException exception) {
            logger.severe("Could not save config: " + exception.getMessage());
        }
    }
}
