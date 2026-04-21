package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.impl.LeafMiningEnchantment;
import net.crystalixs.core.paper.enchantment.impl.TimberEnchantment;
import net.crystalixs.core.paper.enchantment.impl.WoodWhisperEnchantment;

import java.util.List;

public final class PaperCustomEnchantmentBootstrap {

    public List<CustomEnchantment> createHandlers() {
        return List.of(
                new TimberEnchantment(),
                new WoodWhisperEnchantment(),
                new LeafMiningEnchantment()
        );
    }
}
