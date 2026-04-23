package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class PlowEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "plow";
    }

    @Override
    public void onInteract(InteractContext context, int level) {
        if (context.action() != Action.RIGHT_CLICK_BLOCK) return;
        if (context.block() == null) return;
        if (!isHoe(context.tool())) return;

        Block origin = context.block();
        int radius = Math.clamp(level, 1, 3);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block target = origin.getRelative(x, 0, z);
                harvestAndReplant(context, target);
            }
        }
    }

    private void harvestAndReplant(InteractContext context, Block block) {
        Material crop = harvest(context, block);
        plant(context, block, crop);
    }

    private Material harvest(InteractContext context, Block block) {
        if (!(block.getBlockData() instanceof Ageable ageable)) return null;
        if (ageable.getAge() < ageable.getMaximumAge()) return null;

        Material crop = block.getType();
        World world = block.getWorld();
        var drops = block.getDrops(context.tool(), context.player());

        block.setType(Material.AIR);
        drops.forEach(drop -> world.dropItemNaturally(block.getLocation(), drop));

        Location location = block.getLocation().add(0.5D, 0.5D, 0.5D);
        block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 8, 0.2D, 0.2D, 0.2D, 0D);

        return crop;
    }

    private void plant(InteractContext context, Block block, Material crop) {
        if (crop == null) return;

        Material seed = seed(crop);
        if (seed == null) return;

        PlayerInventory source = context.player().getInventory();
        if (consumeSeed(source, seed)) {
            block.setType(crop, false);
            if (block.getBlockData() instanceof Ageable ageable) {
                ageable.setAge(0);
                block.setBlockData(ageable, false);
            }
        }
    }

    private boolean consumeSeed(PlayerInventory source, Material seed) {
        for (int slot = 0; slot < source.getSize(); slot++) {
            ItemStack itemStack = source.getItem(slot);
            if (itemStack == null || itemStack.getType() != seed) continue;
            if (itemStack.getAmount() <= 0) continue;

            itemStack.setAmount(itemStack.getAmount() - 1);
            if (itemStack.getAmount() == 0) source.setItem(slot, null);
            else source.setItem(slot, itemStack);

            return true;
        }
        return false;
    }

    private Material seed(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case NETHER_WART -> Material.NETHER_WART;
            default -> null;
        };
    }

    private boolean isHoe(ItemStack tool) {
        return Tag.ITEMS_HOES.isTagged(tool.getType());
    }
}
