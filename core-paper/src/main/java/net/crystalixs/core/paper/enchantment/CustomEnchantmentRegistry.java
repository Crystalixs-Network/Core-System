package net.crystalixs.core.paper.enchantment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomEnchantmentRegistry {

    private final Map<String, CustomEnchantmentHandler> handlers = new HashMap<>();

    public CustomEnchantmentRegistry(Collection<CustomEnchantmentHandler> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.enchantment().toLowerCase(), handler));
    }

    public CustomEnchantmentHandler find(String enchantment) {
        if (enchantment == null) return null;
        return handlers.get(enchantment.toLowerCase());
    }

    public Collection<CustomEnchantmentHandler> handlers() {
        return List.copyOf(handlers.values());
    }
}
