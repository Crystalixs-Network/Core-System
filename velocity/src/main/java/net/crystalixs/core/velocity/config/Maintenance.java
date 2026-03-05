package net.crystalixs.core.velocity.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.Component.empty;

public final class Maintenance {

    private final String version;
    private final Motd motd;
    private final Screen screen;
    private boolean isEnabled;

    @JsonCreator
    public Maintenance(
            @JsonProperty("enabled") boolean isEnabled,
            @JsonProperty("version") String version,
            @JsonProperty("motd") Motd motd,
            @JsonProperty("screen") Screen screen) {

        this.isEnabled = isEnabled;
        this.version = version;
        this.motd = motd;
        this.screen = screen;
    }

    public String version() {
        return version;
    }

    public Motd motd() {
        return motd;
    }

    public Screen screen() {
        return screen;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

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
