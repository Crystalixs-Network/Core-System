package net.crystalixs.core.paper.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record RedisSync(
        String uri,
        @Setting("backend-id") String backendId
) {
}
