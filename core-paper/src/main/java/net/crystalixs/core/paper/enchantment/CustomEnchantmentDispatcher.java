package net.crystalixs.core.paper.enchantment;

import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.CombatContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.PlacingBlocksContext;

import java.util.List;

public final class CustomEnchantmentDispatcher {

    private final CustomEnchantmentCatalog catalog;

    public CustomEnchantmentDispatcher(CustomEnchantmentCatalog catalog) {
        this.catalog = catalog;
    }

    public void dispatch(EnchantmentContext enchantmentContext, List<ActiveCustomEnchantment> enchantments) {
        for (ActiveCustomEnchantment entry : enchantments) {
            CustomEnchantment enchantment = catalog.find(entry.enchantmentId());
            if (enchantment == null) continue;

            switch (enchantmentContext) {
                case BreakingBlocksContext context -> enchantment.onBlockBreak(context, entry.level());
                case PlacingBlocksContext context -> enchantment.onBlockPlace(context, entry.level());
                case InteractContext context -> enchantment.onInteract(context, entry.level());
                case CombatContext context -> enchantment.onCombat(context, entry.level());
            }
        }
    }
}
