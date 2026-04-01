package net.crystalixs.core.paper.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public record Scoreboard(String title, List<String> lines) {
}
