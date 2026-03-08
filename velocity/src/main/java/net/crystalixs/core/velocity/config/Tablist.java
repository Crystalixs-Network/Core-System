package net.crystalixs.core.velocity.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public record Tablist(List<String> header, List<String> footer) {
}
