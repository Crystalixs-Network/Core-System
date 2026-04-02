package net.crystalixs.core.paper.scoreboard;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.config.PaperConfig;
import net.crystalixy.celestial.api.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.empty;

public final class ScoreboardService {

    private final Map<UUID, Scoreboard> activeBoards = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final StructuredLogger logger;

    private ScoreboardService(JavaPlugin plugin, StructuredLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public static ScoreboardService create(JavaPlugin plugin, StructuredLogger logger, PaperConfig config) {
        if (config == null || config.scoreboard() == null) {
            return null;
        }
        return new ScoreboardService(plugin, logger);
    }

    public void display(Player player) {
        activeBoards.computeIfAbsent(player.getUniqueId(), ignored -> {
            Scoreboard scoreboard = Scoreboard.sidebar()
                    .withPlayer(player)
                    .title(empty())
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
}
