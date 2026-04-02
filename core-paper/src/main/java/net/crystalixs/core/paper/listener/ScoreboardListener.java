package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.display.ScoreboardService;
import net.crystalixs.core.paper.economy.EconomyMutationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ScoreboardListener implements Listener {

    private final JavaPlugin plugin;
    private final ScoreboardService service;

    public ScoreboardListener(JavaPlugin plugin, ScoreboardService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            service.display(event.getPlayer());
            service.refreshAllActive();
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.remove(event.getPlayer());
        service.refreshAllActive();
    }

    @EventHandler
    public void onEconomyMutation(EconomyMutationEvent event) {
        event.affectedPlayers().forEach(service::refreshIfActive);
    }
}
