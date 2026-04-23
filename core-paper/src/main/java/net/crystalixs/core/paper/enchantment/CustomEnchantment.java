package net.crystalixs.core.paper.enchantment;

import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;

public interface CustomEnchantment {

    String id();

    default void onBlockBreak(BreakingBlocksContext context, int level) {
        // Hook for Custom Enchantments, welche das Abbauen von Blöcken beeinflussen
    }
}
