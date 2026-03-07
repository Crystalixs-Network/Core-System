package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record Motd(String firstLine, String secondLine) {

    public Component firstLineComponent(MiniMessage miniMessage) {
        return deserialize(miniMessage, firstLine);
    }

    public Component secondLineComponent(MiniMessage miniMessage) {
        return deserialize(miniMessage, secondLine);
    }

    private static Component deserialize(MiniMessage miniMessage, String input) {
        return input == null ? Component.empty() : miniMessage.deserialize(input);
    }
}
