package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;

@ConfigSerializable
public record Tablist(List<String> header, List<String> footer) {

    public Component headerComponent(MiniMessage miniMessage) {
        return toComponent(header, miniMessage);
    }

    public Component footerComponent(MiniMessage miniMessage) {
        return toComponent(footer, miniMessage);
    }

    private static Component toComponent(List<String> lines, MiniMessage miniMessage) {
        return join(JoinConfiguration.newlines(), lines.stream()
                .map(line -> {
                    if (line == null || line.isBlank()) {
                        return empty();
                    }
                    return deserialize(miniMessage, line);
                })
                .toList());
    }

    private static Component deserialize(MiniMessage miniMessage, String input) {
        if (input == null) {
            return empty();
        }
        return miniMessage.deserialize(input);
    }
}
