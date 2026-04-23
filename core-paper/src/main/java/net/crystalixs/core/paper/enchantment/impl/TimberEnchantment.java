package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.util.BlockFloodFill;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.Set;

public final class TimberEnchantment implements CustomEnchantment, BlockFloodFill {

    private static final int MAX_TREE_BLOCKS = 512;

    @Override
    public String id() {
        return "timber";
    }

    @Override
    public void onBlockBreak(BreakingBlocksContext context, int level) {
        Block origin = context.block();

        if (!Tag.ITEMS_AXES.isTagged(context.tool().getType())) return;
        if (!matches(origin)) return;

        Set<Block> connectedLogs = fill(origin, MAX_TREE_BLOCKS);
        int requiredLevel = requiredLevel(origin, connectedLogs);
        if (level < requiredLevel) return;

        for (Block block : connectedLogs) {
            if (block.equals(origin)) continue;
            block.breakNaturally(context.tool(), true);
        }
    }

    @Override
    public boolean matches(Block block) {
        return Tag.LOGS.isTagged(block.getType());
    }

    private int requiredLevel(Block origin, Set<Block> logs) {
        if (isJungleTree(origin) || isMegaTrunk(origin)) return 3;
        if (isLargeTree(logs)) return 2;
        return 1;
    }

    private boolean isMegaTrunk(Block origin) {
        Material type = origin.getType();
        if (!matches(origin)) return false;

        Block east = origin.getRelative(1, 0, 0);
        Block south = origin.getRelative(0, 0, 1);
        Block southEast = origin.getRelative(1, 0, 1);
        return east.getType() == type && south.getType() == type && southEast.getType() == type;
    }

    private boolean isJungleTree(Block origin) {
        return Tag.JUNGLE_LOGS.isTagged(origin.getType());
    }

    private boolean isLargeTree(Set<Block> logs) {
        return logs.size() > 24;
    }
}
