package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.crystalixs.core.velocity.config.Maintenance;
import net.crystalixs.core.velocity.config.Motd;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.newline;

public class MotdListener {

    private final VelocityConfig config;

    public MotdListener(VelocityConfig config) {
        this.config = config;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        event.setPing(createPing(event.getPing().asBuilder()));
    }

    private ServerPing createPing(ServerPing.Builder builder) {
        Maintenance maintenance = config.maintenance();
        if (maintenance.enabled()) {
            builder.version(new ServerPing.Version(-1, maintenance.version()));
        }
        builder.description(descriptionComponent());

        return builder.build();
    }

    private Component descriptionComponent() {
        Motd motd = config.maintenance().enabled() ? config.maintenance().motd() : config.motd();
        return motd.firstLine()
                .append(newline())
                .append(motd.secondLine());
    }

}
