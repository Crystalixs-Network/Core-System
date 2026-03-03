package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;

public record Maintenance(boolean enabled, String version, Motd motd, Screen screen) {
    public record Screen(Component header, Component body, Component footer, Component url) {
    }
}
