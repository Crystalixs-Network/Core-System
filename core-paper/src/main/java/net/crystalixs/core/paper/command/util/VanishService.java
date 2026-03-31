package net.crystalixs.core.paper.command.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishService {

    private final Plugin plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishService(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public boolean toggleVanish(Player target) {
        if (isVanished(target)) {
            unvanish(target);
            return false;
        }
        vanish(target);
        return true;
    }

    public void vanish(Player target) {
        if (isVanished(target)) return;

        vanishedPlayers.add(target.getUniqueId());

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(target)) continue;

            // optional: vanish bypass permission
            if (online.hasPermission("core.bypass.vanish")) continue;

            online.hidePlayer(plugin, target);
        }

        target.setCollidable(false);
        target.setInvulnerable(true);
        target.setSilent(true);
    }

    public void unvanish(Player target) {
        if (!isVanished(target)) return;

        vanishedPlayers.remove(target.getUniqueId());

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(plugin, target);
        }

        target.setCollidable(true);
        target.setInvulnerable(false);
        target.setSilent(false);
    }

    public Set<UUID> getVanishedPlayers() {
        return Set.copyOf(vanishedPlayers);
    }
}