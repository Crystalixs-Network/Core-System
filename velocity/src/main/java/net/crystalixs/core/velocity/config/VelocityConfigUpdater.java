package net.crystalixs.core.velocity.config;

import net.crystalixs.core.common.config.ConfigFacade;
import net.crystalixs.core.common.config.ConfigService;

import java.io.IOException;
import java.util.function.UnaryOperator;

public final class VelocityConfigUpdater {

    private final ConfigFacade<VelocityConfig> facade;

    public VelocityConfigUpdater(ConfigService<VelocityConfig> service) throws IOException {
        this.facade = new ConfigFacade<>(service);
    }

    public VelocityConfig current() {
        return facade.current();
    }

    public void save() throws IOException {
        facade.save();
    }

    public void reload() throws IOException {
        facade.reload();
    }

    public void update(UnaryOperator<VelocityConfig> updater) throws IOException {
        facade.update(updater);
    }

    public void setMaintenance(boolean enabled) throws IOException {
        update(config -> config.withMaintenance(enabled
                ? config.maintenance().enable()
                : config.maintenance().disable()));
    }
}
