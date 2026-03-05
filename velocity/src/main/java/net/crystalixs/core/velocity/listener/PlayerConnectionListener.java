package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.velocity.config.VelocityConfig;

public class PlayerConnectionListener {

    private final VelocityConfig config;

    public PlayerConnectionListener(VelocityConfig config) {
        this.config = config;
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        Player player = event.getPlayer();

        if (!config.maintenance().isEnabled()) return;
        if (player.hasPermission("core.bypass.maintenance")) return;

        player.disconnect(config.maintenance().screen().construct());
    }

}
