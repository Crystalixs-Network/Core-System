package net.crystalixs.core.paper.command.util;

import net.crystalixs.core.paper.CorePlugin;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SitService {

    private final Map<UUID, UUID> activeSeats = new ConcurrentHashMap<>();
    private final CorePlugin plugin;

    public SitService(CorePlugin plugin) {
        this.plugin = plugin;
    }

    public void toggle(Player player) {
        if (activeSeats.containsKey(player.getUniqueId()) || player.isInsideVehicle()) {
            unsit(player);
            return;
        }
        sit(player);
    }

    public void sit(Player player) {
        if (activeSeats.containsKey(player.getUniqueId())) return;

        Location location = player.getLocation().clone().subtract(0d, 1.2d, 0d);
        player.getWorld().spawn(location, ArmorStand.class, seat -> {
            seat.setMarker(true);
            seat.setInvisible(true);
            seat.setInvulnerable(true);
            seat.setGravity(false);
            seat.setPersistent(true);
            seat.setSilent(true);
            seat.setCustomNameVisible(false);

            seat.addPassenger(player);
            activeSeats.put(player.getUniqueId(), seat.getUniqueId());
        });
    }

    public void unsit(Player player) {
        UUID seatId = activeSeats.remove(player.getUniqueId());
        if (seatId == null) {
            if (player.isInsideVehicle()) player.leaveVehicle();
            return;
        }
        if (player.isInsideVehicle()) player.leaveVehicle();

        Entity entity = findEntity(player, seatId);
        if (entity != null && !entity.isDead()) entity.remove();
    }

    public void unsitBySeat(Entity seat) {
        UUID seatId = seat.getUniqueId();
        activeSeats.entrySet().removeIf(entry -> {
            boolean match = entry.getValue().equals(seatId);
            if (match && !seat.isDead()) {
                seat.remove();
            }
            return match;
        });
    }

    public void shutdown() {
        plugin.getServer().getOnlinePlayers().forEach(this::unsit);
        activeSeats.clear();
    }

    private Entity findEntity(Player player, UUID seatId) {
        for (Entity entity : player.getWorld().getEntities()) {
            if (!entity.getUniqueId().equals(seatId)) continue;
            return entity;
        }
        return null;
    }
}
