package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class ResourceCollectorEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "resource_collector";
    }

    @Override
    public void onBlockBreak(EnchantmentContext.BreakingBlocksContext context, int level) {
        if (isNotSupportedTool(context.tool())) return;

        Collection<ItemStack> drops = context.block().getDrops(context.tool(), context.player());
        if (drops.isEmpty()) return;

        context.event().setDropItems(false);
        giveOrDropNaturally(context.player(), context.block().getLocation(), drops);
    }

    @Override
    public void onInteract(EnchantmentContext.InteractContext context, int level) {
        if (context.action() != Action.RIGHT_CLICK_BLOCK) return;
        if (context.block() == null) return;
        if (isNotSupportedTool(context.tool())) return;

        context.block().getWorld()
                .getNearbyEntities(context.block().getLocation().toCenterLocation(), 2.5D, 2.5D, 2.5D, entity -> entity instanceof Item)
                .stream()
                .map(entity -> (Item) entity)
                .forEach(item -> {
                    Map<Integer, ItemStack> leftovers = context.player().getInventory().addItem(item.getItemStack());
                    if (leftovers.isEmpty()) {
                        item.remove();
                        return;
                    }
                    item.setItemStack(leftovers.values().iterator().next());
                });
    }

    @Override
    public void onCombat(EnchantmentContext.CombatContext context, int level) {
        if (isNotSupportedTool(context.tool())) return;
        if (context.event().getDrops().isEmpty()) return;

        Collection<ItemStack> drops = new ArrayList<>(context.event().getDrops());
        context.event().getDrops().clear();
        giveOrDropNaturally(context.player(), context.target().getLocation(), drops);
    }

    private void giveOrDropNaturally(Player player, Location location, Collection<ItemStack> drops) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(drops.toArray(ItemStack[]::new));
        leftovers.values().forEach(itemStack -> {
            if (itemStack == null || itemStack.getType().isAir()) return;
            location.getWorld().dropItemNaturally(location, itemStack);
        });
    }

    private boolean isNotSupportedTool(ItemStack tool) {
        return tool.getType() != Material.SHEARS
               && !Tag.ITEMS_PICKAXES.isTagged(tool.getType())
               && !Tag.ITEMS_AXES.isTagged(tool.getType())
               && !Tag.ITEMS_SHOVELS.isTagged(tool.getType())
               && !Tag.ITEMS_HOES.isTagged(tool.getType())
               && !Tag.ITEMS_ENCHANTABLE_WEAPON.isTagged(tool.getType());
    }
}
