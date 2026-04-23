package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public final class GlassBreakerEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "glass_breaker";
    }

    @Override
    public void onBlockBreak(EnchantmentContext.BreakingBlocksContext context, int level) {
        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (!isGlass(context.block())) return;

        context.block().getWorld().dropItemNaturally(
                context.block().getLocation(),
                new ItemStack(context.block().getType()));
    }

    private boolean isGlass(Block block) {
        return block.getType().name().contains("GLASS")
               && block.getType() != Material.SPYGLASS
               && block.getType() != Material.GLASS_BOTTLE;
    }
}
