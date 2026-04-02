package net.crystalixs.core.paper.scoreboard;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.config.PaperConfig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ScoreboardService {

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
    }

    public void remove(Player player) {
    }

    public void shutdown() {
    }
}
