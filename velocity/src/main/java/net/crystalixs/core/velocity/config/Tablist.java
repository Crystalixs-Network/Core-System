package net.crystalixs.core.velocity.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;

@ConfigSerializable
public record Tablist(List<String> header, List<String> footer) {

    public Component headerComponent(MiniMessage miniMessage) {
        return toComponent(header, miniMessage, null);
    }

    public Component footerComponent(MiniMessage miniMessage, String serverName) {
        return toComponent(footer, miniMessage, serverName);
    }

    private static Component toComponent(List<String> lines, MiniMessage miniMessage, String serverName) {
        return join(JoinConfiguration.newlines(), lines.stream()
                .map(line -> {
                    if (line == null || line.isBlank()) {
                        return empty();
                    }
                    return deserialize(miniMessage, line, serverName);
                })
                .toList());
    }

    private static Component deserialize(MiniMessage miniMessage, String input, String serverName) {
        if (input == null) {
            return empty();
        }
        return miniMessage.deserialize(input, serverResolver(serverName));
    }

    private static TagResolver serverResolver(String serverName) {
        return Placeholder.unparsed("server", serverName == null ? "" : serverName);
    }
}
