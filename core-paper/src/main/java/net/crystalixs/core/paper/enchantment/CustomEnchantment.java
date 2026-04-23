package net.crystalixs.core.paper.enchantment;

import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;

public interface CustomEnchantment {

    String id();

    default void onBlockBreak(BreakingBlocksContext context, int level) {
        // Hook for Custom Enchantments, welche das Abbauen von Blöcken beeinflussen
    }

    default void onInteract(InteractContext context, int level) {
        // Hook für Custom Enchantments, welche Interaktionen (u.a. Links-/Rechtsklick) haben
    }
}
