package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import static net.kyori.adventure.text.Component.*;

@ConfigSerializable
public record Maintenance(@Setting("enabled") boolean isEnabled, String version, Motd motd, Screen screen) {

    public Maintenance enable() {
        return new Maintenance(true, version, motd, screen);
    }

    public Maintenance disable() {
        return new Maintenance(false, version, motd, screen);
    }

    @ConfigSerializable
    public record Screen(Component header, Component body, Component footer, Component url) {

        public Component construct() {
            return join(JoinConfiguration.separator(newline()),
                    header(), empty(), // Ein empty Component impliziert eine Leerzeile
                    body(), empty(),
                    footer(),
                    url());
        }
    }
}
