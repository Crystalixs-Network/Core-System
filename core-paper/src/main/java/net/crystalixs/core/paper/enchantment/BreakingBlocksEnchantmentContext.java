package net.crystalixs.core.paper.enchantment;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record BreakingBlocksEnchantmentContext(Player player, Block block, ItemStack tool) {
}
