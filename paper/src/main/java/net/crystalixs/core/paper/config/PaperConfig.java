package net.crystalixs.core.paper.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record PaperConfig(Database database) {
}
