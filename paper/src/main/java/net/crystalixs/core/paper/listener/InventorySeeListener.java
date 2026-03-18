package net.crystalixs.core.paper.listener;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.crystalixs.core.paper.command.util.InventorySeeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class InventorySeeListener implements Listener {

    private final InventorySeeService service;

    public InventorySeeListener(InventorySeeService service) {
        this.service = service;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSlotChange(PlayerInventorySlotChangeEvent event) {
        service.refresh(event.getPlayer().getUniqueId());
    }
}
