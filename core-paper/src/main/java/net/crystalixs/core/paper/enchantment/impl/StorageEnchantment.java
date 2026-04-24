package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.PlacingBlocksContext;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.Listener;
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
}
