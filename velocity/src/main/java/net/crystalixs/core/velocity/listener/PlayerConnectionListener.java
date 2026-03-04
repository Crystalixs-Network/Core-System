package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
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
    public void onLogin(PreLoginEvent event) {
        if (!config.maintenance().isEnabled()) return;

        Component deniedComponent = constructDeniedComponent();
        PreLoginComponentResult result = PreLoginComponentResult.denied(deniedComponent);
        event.setResult(result);
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
