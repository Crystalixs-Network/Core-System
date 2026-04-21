package net.crystalixs.core.paper.enchantment;

import java.util.List;

public final class CustomEnchantmentDispatcher {

    private final CustomEnchantmentCatalog catalog;

    public CustomEnchantmentDispatcher(CustomEnchantmentCatalog catalog) {
        this.catalog = catalog;
    }

    public void dispatchBlockBreak(BreakingBlocksEnchantmentContext context, List<ActiveCustomEnchantment> enchantments) {
        for (ActiveCustomEnchantment entry : enchantments) {
            CustomEnchantment enchantment = catalog.find(entry.enchantmentId());
            if (enchantment == null) continue;

            enchantment.onBlockBreak(context, entry.level());
        }
    }
}
