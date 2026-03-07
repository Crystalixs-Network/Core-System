package net.crystalixs.core.velocity.config;

import net.crystalixs.core.common.config.ConfigFacade;
import net.crystalixs.core.common.config.ConfigService;

import java.io.IOException;

public final class VelocityConfigUpdater {

    private final ConfigFacade<VelocityConfig> facade;

    public VelocityConfigUpdater(ConfigService<VelocityConfig> service) {
        this.facade = new ConfigFacade<>(service);
    }

    public void setMaintenance(boolean enabled) throws IOException {
        facade.update(config -> config.withMaintenance(enabled
                ? config.maintenance().enable()
                : config.maintenance().disable()));
    }
}
