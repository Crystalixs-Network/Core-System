package net.crystalixs.core.velocity.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record VelocityConfig(
        @Setting("enable-hot-reloading") boolean isHotReloadingEnabled,
        Motd motd,
        Maintenance maintenance,
        @Setting("redis-sync") RedisSync redisSync
) {

    public VelocityConfig withMaintenance(Maintenance maintenance) {
        return new VelocityConfig(isHotReloadingEnabled, motd, maintenance, redisSync);
    }
}
