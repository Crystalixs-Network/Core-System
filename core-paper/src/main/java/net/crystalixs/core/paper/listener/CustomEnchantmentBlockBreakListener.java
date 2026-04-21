package net.crystalixs.core.paper.listener;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentHandler;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
public class CustomEnchantmentBlockBreakListener implements Listener {

    private final CustomEnchantmentRegistry registry;

    public CustomEnchantmentBlockBreakListener(JavaPlugin plugin, CustomEnchantmentRegistry registry) {
        this.registry = registry;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        ItemMeta itemMeta = tool.getItemMeta();
        if (itemMeta == null) return;

        BreakingBlocksEnchantmentContext context = new BreakingBlocksEnchantmentContext(player, event.getBlock(), tool);
        for (CustomEnchantmentHandler handler : registry.handlers()) {
            Enchantment enchantment = resolveEnchantment(handler.enchantment());
            if (enchantment == null) continue;

            int level = itemMeta.getEnchantLevel(enchantment);
            if (level < 1) continue;

            handler.handle(context, level);
        }
    }

    private Enchantment resolveEnchantment(@NotNull @KeyPattern String enchantment) {
        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(EnchantmentKeys.create(Key.key("core:" + enchantment)));
    }
}

