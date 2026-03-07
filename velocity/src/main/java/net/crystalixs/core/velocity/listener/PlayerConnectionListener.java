package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.velocity.config.VelocityConfig;

public class PlayerConnectionListener {

    private final ConfigService<VelocityConfig> configService;

    public PlayerConnectionListener(ConfigService<VelocityConfig> configService) {
        this.configService = configService;
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        VelocityConfig config = configService.get();

        if (!config.maintenance().isEnabled()) return;
        if (player.hasPermission("core.bypass.maintenance")) return;

        player.disconnect(config.maintenance().screen().construct());
    }

}
