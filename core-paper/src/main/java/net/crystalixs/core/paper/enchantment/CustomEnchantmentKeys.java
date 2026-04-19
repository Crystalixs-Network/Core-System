package net.crystalixs.core.paper.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class CustomEnchantmentKeys {

    private final Map<String, NamespacedKey> keys = new HashMap<>();
    private final JavaPlugin plugin;

    public CustomEnchantmentKeys(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public NamespacedKey levelKey(String enchantment) {
        return keys.computeIfAbsent(enchantment.toLowerCase(), id -> new NamespacedKey(plugin, "enchantment_" + id + "_level"));
    }
}
