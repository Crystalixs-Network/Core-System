package net.crystalixs.core.velocity.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record Database(String host, int port, String username, String password, String database) {
}
