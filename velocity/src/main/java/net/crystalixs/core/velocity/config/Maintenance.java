package net.crystalixs.core.velocity.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.kyori.adventure.text.Component;

public final class Maintenance {

    private final String version;
    private final Motd motd;
    private final Screen screen;
    private boolean isEnabled;

    @JsonCreator
    public Maintenance(@JsonProperty("enabled") boolean isEnabled, String version, Motd motd, Screen screen) {
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

    public Maintenance enabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public record Screen(Component header, Component body, Component footer, Component url) {
    }
}
