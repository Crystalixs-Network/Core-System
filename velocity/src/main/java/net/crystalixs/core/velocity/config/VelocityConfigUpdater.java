package net.crystalixs.core.velocity.config;

import net.crystalixs.core.common.config.ConfigFacade;
import net.crystalixs.core.common.config.ConfigService;
import java.util.logging.Logger;

public final class VelocityConfigUpdater extends ConfigFacade<VelocityConfig> {

    public VelocityConfigUpdater(ConfigService<VelocityConfig> service, Logger logger) {
        super(service, logger);
    }

    public void enableMaintenance(boolean enabled) {
        update(config -> config.withMaintenance(enabled
                ? config.maintenance().enable()
                : config.maintenance().disable()));
    }
}
