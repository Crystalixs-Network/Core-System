package net.crystalixs.core.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import jakarta.inject.Inject;

import java.util.logging.Logger;

public class CorePlugin {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Plugin loaded!");
    }

}

