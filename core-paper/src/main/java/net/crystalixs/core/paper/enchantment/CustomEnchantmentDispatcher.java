package net.crystalixs.core.paper.enchantment;

import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.CombatContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;

import java.util.List;

public final class CustomEnchantmentDispatcher {

    private final CustomEnchantmentCatalog catalog;

    public CustomEnchantmentDispatcher(CustomEnchantmentCatalog catalog) {
        this.catalog = catalog;
    }

    public void dispatchBlockBreak(BreakingBlocksContext context, List<ActiveCustomEnchantment> enchantments) {
        for (ActiveCustomEnchantment entry : enchantments) {
            CustomEnchantment enchantment = catalog.find(entry.enchantmentId());
            if (enchantment == null) continue;

            enchantment.onBlockBreak(context, entry.level());
        }
    }

    public void dispatchInteract(InteractContext context, List<ActiveCustomEnchantment> enchantments) {
        for (ActiveCustomEnchantment entry : enchantments) {
            CustomEnchantment enchantment = catalog.find(entry.enchantmentId());
            if (enchantment == null) continue;

            enchantment.onInteract(context, entry.level());
        }
    }

    public void dispatchCombat(CombatContext context, List<ActiveCustomEnchantment> enchantments) {
        for (ActiveCustomEnchantment entry : enchantments) {
            CustomEnchantment enchantment = catalog.find(entry.enchantmentId());
            if (enchantment == null) continue;

            enchantment.onCombat(context, entry.level());
        }
    }
}
