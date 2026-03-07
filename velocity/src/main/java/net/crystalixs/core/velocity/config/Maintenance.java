package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    public record Screen(String header, String body, String footer, String url) {

        public Component construct(MiniMessage miniMessage) {
            return join(JoinConfiguration.separator(newline()),
                    deserialize(miniMessage, header()), empty(), // Ein empty Component impliziert eine Leerzeile
                    deserialize(miniMessage, body()), empty(),
                    deserialize(miniMessage, footer()),
                    deserialize(miniMessage, url()));
        }

        private static Component deserialize(MiniMessage miniMessage, String input) {
            return input == null ? empty() : miniMessage.deserialize(input);
        }
    }
}
