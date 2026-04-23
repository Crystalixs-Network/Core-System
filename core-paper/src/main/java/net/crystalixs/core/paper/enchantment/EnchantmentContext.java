package net.crystalixs.core.paper.enchantment;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public sealed interface EnchantmentContext {

    record BreakingBlocksContext(Player player, Block block, ItemStack tool) implements EnchantmentContext {
    }

    record InteractContext(Player player, ItemStack tool, Action action, @Nullable Block block) implements EnchantmentContext {
    }
}
