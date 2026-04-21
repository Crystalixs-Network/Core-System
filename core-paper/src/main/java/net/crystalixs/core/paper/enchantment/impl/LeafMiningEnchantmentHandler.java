package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentHandler;
import net.crystalixs.core.paper.util.BlockFloodFill;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public final class LeafMiningEnchantmentHandler implements CustomEnchantmentHandler, BlockFloodFill {


    @Override
    public String enchantment() {
        return "leaf_mining";
    }

    @Override
    public void handle(BreakingBlocksEnchantmentContext context, int level) {
        Block origin = context.block();
        if (!isShears(context.tool())) return;
        if (!matches(origin)) return;

        int cap = cap(level);
        Set<Block> leafs = fill(context.block(), cap);

        // Natürlichen Drop ignorieren.
        // Mehr Informationen dazu sind in WoodWhisperEnchantmentHandler
        // beim Multiplikator zu finden.
        context.block().getDrops().clear();

        leafs.forEach(block -> {
            if (block.equals(context.block())) return;
            block.breakNaturally(context.tool(), true);
        });
    }

    @Override
    public boolean matches(Block block) {
        return Tag.LEAVES.isTagged(block.getType());
    }

    private int cap(int level) {
        return switch (Math.clamp(level, 1, 3)) {
            case 1 -> 32;
            case 2 -> 64;
            default -> 128;
        };
    }

    private boolean isShears(ItemStack tool) {
        return tool != null && tool.getType() == Material.SHEARS;
    }
}
