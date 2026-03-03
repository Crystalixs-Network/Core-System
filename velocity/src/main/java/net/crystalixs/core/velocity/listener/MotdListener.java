package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.crystalixs.core.velocity.config.Motd;
import net.crystalixs.core.velocity.config.VelocityConfig;

import static net.kyori.adventure.text.Component.newline;

public class MotdListener {

    private final VelocityConfig config;

    public MotdListener(VelocityConfig config) {
        this.config = config;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        Motd motd = config.motd();
        ServerPing ping = event.getPing().asBuilder()
                .description(motd.firstLine().append(newline()).append(motd.secondLine()))
                .build();

        event.setPing(ping);
    }

}
