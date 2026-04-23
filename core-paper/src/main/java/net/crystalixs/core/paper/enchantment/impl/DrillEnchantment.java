package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import org.bukkit.Axis;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public final class DrillEnchantment implements CustomEnchantment {

    private static final String FARMWORLD_BACKEND_ID = "farmwelt";
    private final boolean isFarmworldServer;

    public DrillEnchantment(String backendId) {
        this.isFarmworldServer = isFarmworldServer(backendId);
    }

    @Override
    public String id() {
        return "drill";
    }

    @Override
    public void onBlockBreak(BreakingBlocksEnchantmentContext context, int level) {
        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (isNotMinable(context.block())) return;

        int effectiveLevel = isFarmworldServer ? 1 : level;
        int radius = Math.clamp(effectiveLevel, 1, 3);
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

    private boolean isFarmworldServer(String backendId) {
        if (backendId == null || backendId.isBlank()) return false;
        return backendId.equalsIgnoreCase(FARMWORLD_BACKEND_ID);
    }
}
