package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.crystalixs.core.common.command.help.NetworkHelpSyncProtocol;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;
import net.crystalixs.core.velocity.listener.BackendHelpCatalogListener;
import net.crystalixs.core.velocity.listener.MotdListener;
import net.crystalixs.core.velocity.listener.PlayerConnectionListener;

public final class VelocityListenerBootstrap {

    public void register(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater, BackendHelpCatalogCache cache) {
        runtime.server().getChannelRegistrar().register(MinecraftChannelIdentifier.from(NetworkHelpSyncProtocol.CHANNEL));
        runtime.server().getEventManager().register(plugin, new BackendHelpCatalogListener(cache));

        runtime.server().getEventManager().register(plugin, new MotdListener(configUpdater, runtime.miniMessage()));
        runtime.server().getEventManager().register(plugin, new PlayerConnectionListener(plugin.listenerLogger("player-connection"), runtime.miniMessage(), configUpdater));
    }
}
