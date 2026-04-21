package net.crystalixs.core.paper.enchantment;

import net.crystalixs.core.paper.enchantment.model.ActiveCustomEnchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CustomEnchantmentResolver {

    private final CustomEnchantmentCatalog catalog;

    public CustomEnchantmentResolver(CustomEnchantmentCatalog catalog) {
        this.catalog = catalog;
    }

    public List<ActiveCustomEnchantment> resolve(ItemStack tool) {
        List<ActiveCustomEnchantment> resolved = new ArrayList<>();

        if (tool == null || tool.getType().isAir()) {
            return resolved;
        }
        for (var entry : tool.getEnchantments().entrySet()) {
            String key = entry.getKey().getKey().getKey();
            if (catalog.find(key) == null) continue;

            int level = entry.getValue() == null ? 0 : entry.getValue();
            if (level <= 0) continue;

            resolved.add(new ActiveCustomEnchantment(key, level));
        }

        return resolved;
    }
}
