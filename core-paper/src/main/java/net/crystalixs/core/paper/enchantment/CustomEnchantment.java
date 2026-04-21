package net.crystalixs.core.paper.enchantment;

public interface CustomEnchantment {

    String id();

    default void onBlockBreak(BreakingBlocksEnchantmentContext context, int level) {
        // Hook for Custom Enchantments, welche das Abbauen von Blöcken beeinflussen
    }
}
