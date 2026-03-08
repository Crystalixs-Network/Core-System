package net.crystalixs.core.velocity.bootstrap;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.listener.MotdListener;
import net.crystalixs.core.velocity.listener.PlayerConnectionListener;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;

public final class VelocityListenerBootstrap {

    public void register(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater) {
        runtime.server().getEventManager().register(plugin, new MotdListener(configUpdater, runtime.miniMessage()));
        runtime.server().getEventManager().register(plugin, new PlayerConnectionListener(runtime.logger(), runtime.miniMessage(), configUpdater));
    }
}
