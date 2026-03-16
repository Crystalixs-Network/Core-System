package net.crystalixs.core.paper.command.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TrashService {

    private static final int SIZE = 27;
    private static final long DELETE_TICKS = 20L * 60L;

    private final Map<UUID, TrashSession> sessions = new HashMap<>();

    private final JavaPlugin plugin;

    public TrashService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        sessions.computeIfAbsent(player.getUniqueId(), uuid -> new TrashSession(plugin, SIZE, DELETE_TICKS))
                .open(player);
    }

    public void shutdown() {
        sessions.values().forEach(TrashSession::shutdown);
        sessions.clear();
    }
}
