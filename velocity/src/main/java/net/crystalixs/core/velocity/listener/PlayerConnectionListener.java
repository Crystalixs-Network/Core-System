package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.VelocityConfigUpdater;

public class PlayerConnectionListener {

    private final VelocityConfigUpdater updater;

    public PlayerConnectionListener(VelocityConfigUpdater updater) {
        this.updater = updater;
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        VelocityConfig config = updater.current();

        if (!config.maintenance().isEnabled()) return;
        if (player.hasPermission("core.bypass.maintenance")) return;

        player.disconnect(config.maintenance().screen().construct());
    }

}
