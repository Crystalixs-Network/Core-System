package net.crystalixs.core.paper.enchantment;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public sealed interface EnchantmentContext {

    record BreakingBlocksContext(Player player, Block block, ItemStack tool) implements EnchantmentContext {
    }
}
