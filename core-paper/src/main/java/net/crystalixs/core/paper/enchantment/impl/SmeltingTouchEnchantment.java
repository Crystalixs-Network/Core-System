package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.*;

public final class SmeltingTouchEnchantment implements CustomEnchantment {

    private final Map<Material, ItemStack> cookingResults = new EnumMap<>(Material.class);
    private static volatile boolean isCacheInitialized = false;

    @Override
    public String id() {
        return "smelting_touch";
    }

    @Override
    public void onBlockBreak(EnchantmentContext.BreakingBlocksContext context, int level) {
        if (!Tag.ITEMS_PICKAXES.isTagged(context.tool().getType())) return;
        if (context.block().getDrops(context.tool(), context.player()).isEmpty()) return;

        Collection<ItemStack> drops = context.block().getDrops(context.tool(), context.player());
        if (drops.isEmpty()) return;

        ensureRecipeCache();

        List<ItemStack> convertedDrops = new ArrayList<>();
        boolean changed = false;

        for (ItemStack drop : drops) {
            ItemStack smelted = smelt(drop);
            if (smelted == null) {
                convertedDrops.add(drop);
                continue;
            }

            changed = true;
            appendStacked(convertedDrops, smelted, drop.getAmount());
        }

        if (!changed) return;

        context.event().setDropItems(false);
        convertedDrops.forEach(drop -> context.block().getWorld().dropItemNaturally(context.block().getLocation(), drop));
    }

    private ItemStack smelt(ItemStack itemStack) {
        ItemStack result = cookingResults.get(itemStack.getType());
        return result == null ? null : result.clone();
    }

    private void ensureRecipeCache() {
        if (isCacheInitialized) return;

        synchronized (cookingResults) {
            if (isCacheInitialized) return;

            Bukkit.recipeIterator().forEachRemaining(recipe -> {
                if (!(recipe instanceof CookingRecipe<?> cookingRecipe)) return;

                ItemStack result = cookingRecipe.getResult();
                if (result.getType().isAir()) return;

                RecipeChoice choice = cookingRecipe.getInputChoice();
                if (choice instanceof MaterialChoice materialChoice) {
                    for (Material material : materialChoice.getChoices()) {
                        cookingResults.putIfAbsent(material, result);
                    }
                    return;
                }
                if (choice instanceof ExactChoice exactChoice) {
                    for (ItemStack ingredient : exactChoice.getChoices()) {
                        cookingResults.putIfAbsent(ingredient.getType(), result);
                    }
                }
            });
            isCacheInitialized = true;
        }
    }

    private void appendStacked(Collection<ItemStack> drops, ItemStack template, int multiplier) {
        int totalAmount = template.getAmount() * Math.max(multiplier, 1);
        int maxSize = template.getMaxStackSize();

        while (totalAmount > 0) {
            int amount = Math.min(maxSize, totalAmount);
            ItemStack itemStack = template.clone();
            itemStack.setAmount(amount);
            drops.add(itemStack);
            totalAmount -= amount;
        }
    }
}
