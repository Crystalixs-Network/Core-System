package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.BreakingBlocksEnchantmentContext;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentHandler;

public final class WoodWhisperEnchantmentHandler implements CustomEnchantmentHandler {

    @Override
    public String enchantment() {
        return "wood_whisper";
    }

    @Override
    public void handle(BreakingBlocksEnchantmentContext context, int level) {
    }
}
