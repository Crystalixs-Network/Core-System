package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.PlacingBlocksContext;
import net.crystalixs.core.paper.enchantment.util.StorageSession;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class StorageEnchantment implements CustomEnchantment, Listener {

    public static final NamespacedKey STORAGE_LEVEL_KEY = new NamespacedKey("core", "storage_level");

    public StorageEnchantment(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String id() {
        return "storage";
    }

    @Override
    public void onBlockPlace(PlacingBlocksContext context, int level) {
        if (!(context.block().getState() instanceof ShulkerBox shulker)) return;

        shulker.getPersistentDataContainer().set(STORAGE_LEVEL_KEY, PersistentDataType.INTEGER, level);
        shulker.update(true, false);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getState() instanceof ShulkerBox shulker)) return;

        PersistentDataContainer container = shulker.getPersistentDataContainer();
        if (!container.has(STORAGE_LEVEL_KEY, PersistentDataType.INTEGER)) return;

        Integer level = container.get(STORAGE_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level == null || level <= 0) return;

        event.setCancelled(true);

        StorageSession session = new StorageSession(event.getPlayer(), shulker, level);
        session.open();
    }
}
