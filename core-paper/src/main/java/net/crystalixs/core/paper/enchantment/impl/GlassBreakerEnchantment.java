package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public final class GlassBreakerEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "glass_breaker";
    }

    @Override
    public void onInteract(InteractContext context, int level) {
        if (context.action() != Action.LEFT_CLICK_BLOCK) return;
        if (context.block() == null) return;
        if (!isGlass(context.block())) return;
        if (!isSupportedTool(context.tool())) return;

        context.block().getWorld().dropItemNaturally(
                context.block().getLocation(),
                new ItemStack(context.block().getType())
        );
        context.block().setType(Material.AIR);
        context.player().playSound(context.block().getLocation(), Sound.BLOCK_GLASS_BREAK, 0.75F, 0.8F);
    }

    private boolean isGlass(Block block) {
        return block.getType().name().contains("GLASS")
               && block.getType() != Material.SPYGLASS
               && block.getType() != Material.GLASS_BOTTLE;
    }

    private boolean isSupportedTool(ItemStack tool) {
        return tool.getType() == Material.SHEARS
               || Tag.ITEMS_PICKAXES.isTagged(tool.getType())
               || Tag.ITEMS_AXES.isTagged(tool.getType())
               || Tag.ITEMS_SHOVELS.isTagged(tool.getType())
               || Tag.ITEMS_HOES.isTagged(tool.getType());
    }
}
