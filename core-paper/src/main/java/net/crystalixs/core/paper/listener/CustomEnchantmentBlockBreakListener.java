package net.crystalixs.core.paper.listener;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.model.CustomEnchantment;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomEnchantmentBlockBreakListener implements Listener {

    private final List<CustomEnchantment> enchantments;

    public CustomEnchantmentBlockBreakListener(List<CustomEnchantment> enchantments) {
        this.enchantments = List.copyOf(enchantments);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        BreakingBlocksEnchantmentContext context = new BreakingBlocksEnchantmentContext(player, event.getBlock(), tool);
        for (CustomEnchantment customEnchantment : enchantments) {
            Enchantment enchantment = resolveEnchantment(customEnchantment.id());
            if (enchantment == null) continue;

            int level = tool.getEnchantmentLevel(enchantment);
            if (level < 1) continue;

            customEnchantment.onBlockBreak(context, level);
        }
    }

    private Enchantment resolveEnchantment(@NotNull @KeyPattern String enchantment) {
        return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(EnchantmentKeys.create(Key.key("core:" + enchantment)));
    }
}
