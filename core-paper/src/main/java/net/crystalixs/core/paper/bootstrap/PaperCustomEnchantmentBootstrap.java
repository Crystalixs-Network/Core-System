package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.enchantment.CustomEnchantmentRegistry;
import net.crystalixs.core.paper.enchantment.impl.TimberEnchantmentHandler;

import java.util.List;

public final class PaperCustomEnchantmentBootstrap {

    public CustomEnchantmentRegistry createRegistry() {
        return new CustomEnchantmentRegistry(List.of(
                new TimberEnchantmentHandler()
        ));
    }
}
