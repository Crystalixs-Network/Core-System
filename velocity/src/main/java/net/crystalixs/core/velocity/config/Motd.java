package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record Motd(Component firstLine, Component secondLine) {
}
