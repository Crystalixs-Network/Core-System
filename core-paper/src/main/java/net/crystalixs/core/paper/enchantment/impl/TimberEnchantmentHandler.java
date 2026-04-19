package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentHandler;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public final class TimberEnchantmentHandler implements CustomEnchantmentHandler {

    private static final int MAX_TREE_BLOCKS = 512;

    @Override
    public String enchantment() {
        return "timber";
    }

    @Override
    public void handle(BreakingBlocksEnchantmentContext context, int level) {
        Block origin = context.block();
        if (!Tag.LOGS.isTagged(origin.getType())) return;

        Set<Block> connectedLogs = connectedLogs(origin);
        for (Block block : connectedLogs) {
            if (block.equals(origin)) continue;
            block.breakNaturally(context.tool(), true);
        }
    }

    private Set<Block> connectedLogs(Block origin) {
        Set<Block> visited = new HashSet<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && visited.size() < MAX_TREE_BLOCKS) {
            Block current = queue.poll();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        Block neighbor = current.getRelative(dx, dy, dz);
                        if (!Tag.LOGS.isTagged(neighbor.getType())) continue;
                        if (visited.add(neighbor)) queue.add(neighbor);
                    }
                }
            }
        }

        return visited;
    }
}
