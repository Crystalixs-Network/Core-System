package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.home.gui.RenameHomeGui;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class HomeRenameAnvilListener implements Listener {

    private final JavaPlugin plugin;

    public HomeRenameAnvilListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player)) return;
        if (event.getInventory().getType() != InventoryType.ANVIL) return;

        NamespacedKey key = new NamespacedKey(plugin, RenameHomeGui.RENAME_ANVIL_PDC_KEY);
        if (!player.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN)) return;

        AnvilView anvilView = event.getView();
        anvilView.setRepairCost(0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (event.getInventory().getType() != InventoryType.ANVIL) return;

        NamespacedKey key = new NamespacedKey(plugin, RenameHomeGui.RENAME_ANVIL_PDC_KEY);
        player.getPersistentDataContainer().remove(key);
    }
}
