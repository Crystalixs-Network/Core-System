package net.crystalixs.core.paper.util;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;

public interface BlockFloodFill extends FloodFill<Block> {

    @Override
    default Collection<Block> neighbors(Block current) {
        Collection<Block> neighbors = new ArrayList<>(26);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    neighbors.add(current.getRelative(dx, dy, dz));
                }
            }
        }
        return neighbors;
    }
}
