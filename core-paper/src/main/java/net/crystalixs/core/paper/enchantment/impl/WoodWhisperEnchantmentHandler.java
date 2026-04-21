package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public final class WoodWhisperEnchantmentHandler implements CustomEnchantment {

    @Override
    public String id() {
        return "wood_whisper";
    }

    @Override
    public void onBlockBreak(BreakingBlocksEnchantmentContext context, int level) {
        Block block = context.block();

        if (!Tag.ITEMS_AXES.isTagged(context.tool().getType())) return;
        if (!Tag.LOGS.isTagged(block.getType())) return;
        if (block.getType().name().startsWith("STRIPPED_")) return;

        int multiplier = multiplier(level);
        Collection<ItemStack> drops = block.getDrops(context.tool(), context.player());

        // Der Multiplikator wird durch die Funktion f(x) ↦ 2x+1 berechnet.
        // Damit die Anzahl an finalen Drops korrekt ist, muss ein Drop entfernt
        // werden, da der natürlich auftretende Drop berücksichtigt werden muss.
        for (int i = 0; i < multiplier - 1; i++) {
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
            }
        }
    }

    private int multiplier(int level) {
        return switch (level) {
            case 1 -> 3;
            case 2 -> 5;
            default -> 7;
        };
    }
}
