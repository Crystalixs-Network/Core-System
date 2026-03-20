package net.crystalixs.core.velocity.bootstrap;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;
import net.crystalixs.core.velocity.listener.BackendHelpCatalogRedisSubscriber;
import net.crystalixs.core.velocity.listener.MotdListener;
import net.crystalixs.core.velocity.listener.PlayerConnectionListener;

public final class VelocityListenerBootstrap {

    private BackendHelpCatalogRedisSubscriber redisSubscriber;

    public void register(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater, BackendHelpCatalogCache cache) {
        String redisUri = configUpdater.current().redisSync() == null ? null : configUpdater.current().redisSync().uri();
        this.redisSubscriber = new BackendHelpCatalogRedisSubscriber(cache, runtime.componentLogger("help-sync"), redisUri);
        this.redisSubscriber.start();

        runtime.server().getEventManager().register(plugin, new MotdListener(configUpdater, runtime.miniMessage()));
        runtime.server().getEventManager().register(plugin, new PlayerConnectionListener(plugin.listenerLogger("player-connection"), runtime.miniMessage(), configUpdater));
    }

    public void close() {
        if (redisSubscriber != null) {
            redisSubscriber.close();
            redisSubscriber = null;
        }
    }
}
