package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.util.BlockFloodFill;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.Set;

public final class VeinMiningEnchantment implements CustomEnchantment, BlockFloodFill {

    @Override
    public String id() {
        return "vein_mining";
    }

    @Override
    public void onBlockBreak(BreakingBlocksEnchantmentContext context, int level) {
        Block origin = context.block();

        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (!matches(origin)) return;

        Material oreType = origin.getType();
        Set<Block> vein = fill(origin, cap(level));

        for (Block block : vein) {
            if (block.equals(origin)) continue;
            if (block.getType() != oreType) continue;
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
        return block.getType().name().endsWith("_ORE") || block.getType() == Material.ANCIENT_DEBRIS;
    }
}
