package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public final class PlowEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "plow";
    }

    @Override
    public void onInteract(EnchantmentContext.InteractContext context, int level) {
        if (context.action() != Action.RIGHT_CLICK_BLOCK) return;
        if (context.block() == null) return;
        if (!isHoe(context.tool())) return;

        Block origin = context.block();
        int radius = Math.clamp(level, 1, 3);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block target = origin.getRelative(x, 0, z);
                if (!isTillable(target)) continue;
                if (!target.getRelative(BlockFace.UP).getType().isAir()) continue;

                target.setType(Material.FARMLAND);
            }
        }
    }

    private boolean isTillable(Block block) {
        return switch (block.getType()) {
            case GRASS_BLOCK, DIRT, DIRT_PATH -> true;
            default -> false;
        };
    }

    private boolean isHoe(ItemStack tool) {
        return Tag.ITEMS_HOES.isTagged(tool.getType());
    }
}
