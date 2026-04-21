package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentHandler;
import net.crystalixs.core.paper.util.BlockFloodFill;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.Set;

public final class TimberEnchantmentHandler implements CustomEnchantmentHandler, BlockFloodFill {

    private static final int MAX_TREE_BLOCKS = 512;

    @Override
    public String enchantment() {
        return "timber";
    }

    @Override
    public void handle(BreakingBlocksEnchantmentContext context, int level) {
        Block origin = context.block();

        if (!Tag.ITEMS_AXES.isTagged(context.tool().getType())) return;
        if (!Tag.LOGS.isTagged(origin.getType())) return;

        Set<Block> connectedLogs = connectedLogs(origin);
        int requiredLevel = requiredLevel(origin, connectedLogs);
        if (level < requiredLevel) return;

        // Natürlichen Drop ignorieren.
        // Mehr Informationen dazu sind in WoodWhisperEnchantmentHandler
        // beim Multiplikator zu finden.
        context.block().getDrops().clear();

        for (Block block : connectedLogs) {
            if (block.equals(origin)) continue;
            block.breakNaturally(context.tool(), true);
        }
    }

    private Set<Block> connectedLogs(Block origin) {
        return fill(origin, MAX_TREE_BLOCKS);
    }

    private int requiredLevel(Block origin, Set<Block> logs) {
        if (isJungleTree(origin) || isMegaTrunk(origin)) return 3;
        if (isLargeTree(logs)) return 2;
        return 1;
    }

    private boolean isMegaTrunk(Block origin) {
        Material type = origin.getType();
        if (!Tag.LOGS.isTagged(type)) return false;

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

    @Override
    public boolean matches(Block block) {
        return Tag.LOGS.isTagged(block.getType());
    }
}
