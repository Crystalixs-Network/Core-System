package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.velocity.config.Maintenance;
import net.crystalixs.core.velocity.config.Motd;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;

public class MotdListener {

    private final ConfigService<VelocityConfig> configService;

    public MotdListener(ConfigService<VelocityConfig> configService) {
        this.configService = configService;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        event.setPing(createPing(event.getPing().asBuilder()));
    }

    private ServerPing createPing(ServerPing.Builder builder) {
        VelocityConfig config = configService.get();
        Maintenance maintenance = config.maintenance();
        if (maintenance.isEnabled()) {
            builder.version(new ServerPing.Version(-1, maintenance.version()));
        }
        builder.description(descriptionComponent());

        return builder.build();
    }

    private Component descriptionComponent() {
        VelocityConfig config = configService.get();
        Motd motd = config.maintenance().isEnabled() ? config.maintenance().motd() : config.motd();
        return join(JoinConfiguration.separator(newline()),
                motd.firstLine(),
                motd.secondLine());
    }

}
