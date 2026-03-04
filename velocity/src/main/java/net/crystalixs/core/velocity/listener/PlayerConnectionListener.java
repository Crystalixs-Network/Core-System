package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.velocity.config.Maintenance.Screen;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import static net.kyori.adventure.text.Component.*;

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

        player.disconnect(constructDeniedComponent());
    }

    private Component constructDeniedComponent() {
        Screen screen = config.maintenance().screen();
        return join(JoinConfiguration.separator(newline()),
                screen.header(), empty(), // Ein empty Component impliziert eine Leerzeile
                screen.body(), empty(),
                screen.footer(),
                screen.url());
    }

}
