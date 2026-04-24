package net.crystalixs.core.paper.enchantment.impl;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.PlacingBlocksContext;
import net.crystalixs.core.paper.enchantment.util.StorageSession;
import org.bukkit.block.ShulkerBox;

public final class StorageEnchantment implements CustomEnchantment {

    @Override
    public String id() {
        return "storage";
    }

    @Override
    public void onBlockPlace(PlacingBlocksContext context, int level) {
        if (!(context.block().getState() instanceof ShulkerBox)) return;
        context.event().setCancelled(true);

        StorageSession session = new StorageSession(context.player(), level);
        session.open();
    }
}
