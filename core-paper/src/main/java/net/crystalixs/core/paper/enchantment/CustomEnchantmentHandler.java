package net.crystalixs.core.paper.enchantment;

public interface CustomEnchantmentHandler {

    String enchantment();

    void handle(BreakingBlocksEnchantmentContext context, int level);

}
