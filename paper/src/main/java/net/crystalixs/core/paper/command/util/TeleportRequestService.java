package net.crystalixs.core.paper.command.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportRequestService {

    public enum RequestType {
        TPA, TPA_HERE
    }

    public record TeleportRequest(UUID requester, UUID target, RequestType type, long expiresAtMillis) {
    }

    private static final long EXPIRE_MILLIS = 60_000L;
    private static final long EXPIRE_TICKS = 1_200L;

    private final Map<UUID, TeleportRequest> openRequests = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;

    public TeleportRequestService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public TeleportRequest create(Player requester, Player target, RequestType type) {
        TeleportRequest request = new TeleportRequest(requester.getUniqueId(), target.getUniqueId(), type, System.currentTimeMillis() + EXPIRE_MILLIS);
        openRequests.put(target.getUniqueId(), request);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            TeleportRequest current = openRequests.get(target.getUniqueId());
            if (current != null && current.equals(request) && isExpired(current)) {
                openRequests.remove(target.getUniqueId());
            }
        }, EXPIRE_TICKS);

        return request;
    }

    public TeleportRequest consumeTarget(Player target) {
        TeleportRequest request = openRequests.remove(target.getUniqueId());
        if (request == null) return null;
        return isExpired(request) ? null : request;
    }

    public void shutdown() {
        openRequests.clear();
    }

    private boolean isExpired(TeleportRequest request) {
        return System.currentTimeMillis() > request.expiresAtMillis();
    }
}
