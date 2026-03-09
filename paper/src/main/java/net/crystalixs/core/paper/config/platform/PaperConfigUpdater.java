package net.crystalixs.core.paper.config.platform;

import net.crystalixs.core.common.config.ConfigFacade;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.paper.config.PaperConfig;

import java.io.IOException;

public final class PaperConfigUpdater {

    private final ConfigFacade<PaperConfig> facade;

    public PaperConfigUpdater(ConfigService<PaperConfig> service) throws IOException {
        this.facade = new ConfigFacade<>(service);
    }

    public PaperConfig current() {
        return facade.current();
    }
}
