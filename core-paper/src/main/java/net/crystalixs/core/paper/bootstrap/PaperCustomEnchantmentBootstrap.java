package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.enchantment.CustomEnchantmentRegistry;
import net.crystalixs.core.paper.enchantment.impl.LeafMiningEnchantmentHandler;
import net.crystalixs.core.paper.enchantment.impl.TimberEnchantmentHandler;
import net.crystalixs.core.paper.enchantment.impl.WoodWhisperEnchantmentHandler;

import java.util.List;

public final class PaperCustomEnchantmentBootstrap {

    public CustomEnchantmentRegistry createRegistry() {
        return new CustomEnchantmentRegistry(List.of(
                new TimberEnchantmentHandler(),
                new WoodWhisperEnchantmentHandler(),
                new LeafMiningEnchantmentHandler()
        ));
    }
}
