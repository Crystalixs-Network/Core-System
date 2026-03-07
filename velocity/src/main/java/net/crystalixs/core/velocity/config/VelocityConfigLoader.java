package net.crystalixs.core.velocity.config;

import net.crystalixs.core.common.config.legacy.ConfigLoader;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class VelocityConfigLoader extends ConfigLoader<VelocityConfig> {

    private final Logger logger;

    public VelocityConfigLoader(ObjectMapper mapper, Logger logger, @NonNull Path file) {
        super(mapper, file, "config.json", VelocityConfig.class);
        this.logger = logger;
    }

    public void saveAndReload() {
        save();
        reload();
    }

    @Override
    public synchronized void save() {
        try {
            super.save();
        } catch (IOException exception) {
            logger.severe("There was an error while saving the config: " + exception.getMessage());
        }
    }

    @Override
    public synchronized void reload() {
        try {
            super.reload();
        } catch (IOException exception) {
            logger.severe("There was an error while reloading config: " + exception.getMessage());
        }
    }
}
