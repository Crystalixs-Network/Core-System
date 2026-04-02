package net.crystalixs.core.paper.scoreboard;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.config.PaperConfig;
import net.crystalixy.celestial.api.Scoreboard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.translatable;

public final class ScoreboardService {

    private final Map<UUID, Scoreboard> activeBoards = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final StructuredLogger logger;
    private final PaperConfig config;

    private ScoreboardService(JavaPlugin plugin, StructuredLogger logger, PaperConfig config) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
    }

    public static ScoreboardService create(JavaPlugin plugin, StructuredLogger logger, PaperConfig config) {
        if (config == null || config.scoreboard() == null) {
            return null;
        }
        return new ScoreboardService(plugin, logger, config);
    }

    public void display(Player player) {
        remove(player); // Destroy previous scoreboard

        activeBoards.computeIfAbsent(player.getUniqueId(), ignored -> {
            Component title = resolveTitle();
            List<Component> lines = resolveLines();

            Scoreboard scoreboard = Scoreboard.sidebar()
                    .withPlayer(player)
                    .title(title)
                    .lines(lines)
                    .build();

            scoreboard.display();
            return scoreboard;
        });
    }

    public void remove(Player player) {
        Scoreboard scoreboard = activeBoards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.destroy();
        }
    }

    public void shutdown() {
        activeBoards.values().forEach(Scoreboard::destroy);
        activeBoards.clear();
    }

    private Component resolveTitle() {
        String translationKey = config.scoreboard().title();
        if (translationKey == null || translationKey.isBlank()) {
            return empty();
        }
        return translatable(translationKey);
    }

    private List<Component> resolveLines() {
        if (config.scoreboard().lines() == null) {
            return Collections.emptyList();
        }
        return config.scoreboard().lines().stream()
                .map(line -> line == null || line.isBlank()
                        ? empty()
                        : translatable(line)
                )
                .collect(Collectors.toList());
    }
}
