package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import org.bukkit.Axis;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public final class DrillEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "drill";
    }

    @Override
    public void onBlockBreak(BreakingBlocksEnchantmentContext context, int level) {
        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (isNotMinable(context.block())) return;

        int radius = Math.clamp(level, 1, 3) - 1;
        if (radius <= 0) return;

        Block origin = context.block();
        Axis axis = axis(context.player().getEyeLocation().getDirection());

        for (int a = -radius; a <= radius; a++) {
            for (int b = -radius; b <= radius; b++) {
                if (a == 0 && b == 0) continue;

                Block target = switch (axis) {
                    case X -> origin.getRelative(0, a, b);
                    case Y -> origin.getRelative(a, 0, b);
                    case Z -> origin.getRelative(a, b, 0);
                };

                if (isNotMinable(target)) continue;
                target.breakNaturally(context.tool(), true);
            }
        }
    }

    private Axis axis(Vector direction) {
        double absX = Math.abs(direction.getX());
        double absY = Math.abs(direction.getY());
        double absZ = Math.abs(direction.getZ());

        if (absX >= absY && absX >= absZ) return Axis.X;
        if (absZ >= absY && absZ >= absX) return Axis.Z;
        return Axis.Y;
    }

    private boolean isNotMinable(Block block) {
        return !Tag.MINEABLE_PICKAXE.isTagged(block.getType());
    }
}
