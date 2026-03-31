package net.crystalixs.core.velocity.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record RedisSync(String uri) {
}
