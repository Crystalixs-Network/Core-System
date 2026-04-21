package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentHandler;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public final class LeafMiningEnchantmentHandler implements CustomEnchantmentHandler {

    @Override
    public String enchantment() {
        return "leaf_mining";
    }

    @Override
    public void handle(BreakingBlocksEnchantmentContext context, int level) {
        if (!isShears(context.tool())) return;
        if (!Tag.LEAVES.isTagged(context.block().getType())) return;

        int cap = cap(level);
        Set<Block> leafs = connectedLeafs(context.block(), cap);

        // Natürlichen Drop ignorieren.
        // Mehr Informationen dazu sind in WoodWhisperEnchantmentHandler
        // beim Multiplikator zu finden.
        context.block().getDrops().clear();

        leafs.forEach(block -> {
            if (block.equals(context.block())) return;
            block.breakNaturally(context.tool(), true);
        });
    }

    private int cap(int level) {
        return switch (Math.clamp(level, 1, 3)) {
            case 1 -> 32;
            case 2 -> 64;
            default -> 128;
        };
    }

    private Set<Block> connectedLeafs(Block origin, int cap) {
        Set<Block> visited = new HashSet<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();

        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && visited.size() < cap) {
            Block current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        Block neighbor = current.getRelative(dx, dy, dz);
                        if (!Tag.LEAVES.isTagged(neighbor.getType())) continue;
                        if (visited.add(neighbor)) queue.add(neighbor);
                    }
                }
            }
        }
        return visited;
    }

    private boolean isShears(ItemStack tool) {
        return tool != null && tool.getType() == Material.SHEARS;
    }
}
