package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.command.util.VanishService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class VanishListener implements Listener {

    private final JavaPlugin plugin;
    private final VanishService service;

    public VanishListener(JavaPlugin plugin, VanishService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (UUID uuid : service.getVanishedPlayers()) {
            Player vanished = Bukkit.getPlayer(uuid);
            if (vanished == null) continue;

            if (!player.hasPermission("core.vanish.see")) {
                player.hidePlayer(plugin, vanished);
            }
        }
    }
}
