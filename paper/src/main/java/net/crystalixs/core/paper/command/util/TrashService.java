package net.crystalixs.core.paper.command.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TrashService {

    private static final int SIZE = 27;
    private final Map<UUID, TrashSession> sessions = new HashMap<>();

    public void open(Player player) {
        sessions.computeIfAbsent(player.getUniqueId(), uuid -> new TrashSession(SIZE))
                .open(player);
    }

}
