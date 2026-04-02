package net.crystalixs.core.paper.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record PaperConfig(
        @Setting("enable-hot-reloading") boolean isHotReloadingEnabled,
        Database database,
        RedisSync redisSync,
        Scoreboard scoreboard
) {
}
