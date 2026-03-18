package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.command.util.SitService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SitListener implements Listener {

    private final SitService service;

    public SitListener(SitService service) {
        this.service = service;
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.unsit(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        service.unsit(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        service.unsit(event.getPlayer());
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        Entity dismounted = event.getDismounted();
        if (!(event.getEntity() instanceof Player)) return;

        service.unsitBySeat(dismounted);
    }
}
