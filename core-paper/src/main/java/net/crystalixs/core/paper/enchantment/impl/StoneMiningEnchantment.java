package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.util.BlockFloodFill;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.Set;

public final class StoneMiningEnchantment implements CustomEnchantment, BlockFloodFill {

    @Override
    public String id() {
        return "stone_mining";
    }

    @Override
    public void onBlockBreak(BreakingBlocksContext context, int level) {
        Block origin = context.block();
        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (!matches(origin)) return;

        Set<Block> connectedBlocks = fill(origin, cap(level));
        for (Block block : connectedBlocks) {
            if (block.equals(origin)) continue;
            if (block.getType() != origin.getType()) continue;
            block.breakNaturally(context.tool(), true);
        }
    }

    private int cap(int level) {
        return switch (Math.clamp(level, 1, 3)) {
            case 1 -> 10;
            case 2 -> 15;
            default -> 24;
        };
    }

    @Override
    public boolean matches(Block block) {
        return Tag.BASE_STONE_OVERWORLD.isTagged(block.getType());
    }
}
