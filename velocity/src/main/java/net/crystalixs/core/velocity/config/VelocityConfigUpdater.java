package net.crystalixs.core.velocity.config;

import net.crystalixs.core.common.config.ConfigFacade;
import net.crystalixs.core.common.config.ConfigService;

import java.io.IOException;

public final class VelocityConfigUpdater extends ConfigFacade<VelocityConfig> {

    public VelocityConfigUpdater(ConfigService<VelocityConfig> service) {
        super(service);
    }

    public void setMaintenance(boolean enabled) throws IOException {
        update(config -> config.withMaintenance(enabled
                ? config.maintenance().enable()
                : config.maintenance().disable()));
    }
}
