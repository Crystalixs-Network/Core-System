package net.crystalixs.core.paper.enchantment;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomEnchantmentCatalog {

    private final Map<String, CustomEnchantment> enchantments = new HashMap<>();

    public CustomEnchantmentCatalog(Collection<CustomEnchantment> enchantments) {
        enchantments.forEach(enchantment -> this.enchantments.put(enchantment.id(), enchantment));
    }

    public CustomEnchantment find(@NotNull String id) {
        return enchantments.get(id);
    }

    public Collection<CustomEnchantment> all() {
        return List.copyOf(enchantments.values());
    }
}
