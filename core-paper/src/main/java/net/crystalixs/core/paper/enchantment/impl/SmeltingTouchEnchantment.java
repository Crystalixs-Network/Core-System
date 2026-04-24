package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext;
import org.bukkit.Tag;

public final class SmeltingTouchEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "smelting_touch";
    }

    @Override
    public void onBlockBreak(EnchantmentContext.BreakingBlocksContext context, int level) {
        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (context.block().getDrops(context.tool(), context.player()).isEmpty()) return;
    }
}
