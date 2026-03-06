package net.crystalixs.core.velocity.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VelocityConfig(
        @JsonProperty("enable-hot-reloading") boolean isHotReloadEnabled,
        Motd motd,
        Maintenance maintenance
) {
}
