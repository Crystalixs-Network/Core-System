package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentKeys;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEnchantmentBlockBreakListener implements Listener {

    private final JavaPlugin plugin;
    private final CustomEnchantmentRegistry registry;
    private final CustomEnchantmentKeys keys;

    public CustomEnchantmentBlockBreakListener(JavaPlugin plugin, CustomEnchantmentRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
        this.keys = new CustomEnchantmentKeys(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        ItemMeta itemMeta = tool.getItemMeta();
        if (itemMeta == null) return;

        registry.handlers().forEach(handler -> {
            NamespacedKey key = keys.levelKey(handler.enchantment());
            Integer level = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            if (level == null || level < 1) return;

            BreakingBlocksEnchantmentContext context = new BreakingBlocksEnchantmentContext(player, event.getBlock(), tool);
            handler.handle(context, level);
        });
    }
}
