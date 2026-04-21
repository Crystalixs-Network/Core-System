package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.enchantment.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class CustomEnchantmentBlockBreakListener implements Listener {

    private final CustomEnchantmentResolver resolver;
    private final CustomEnchantmentDispatcher dispatcher;

    public CustomEnchantmentBlockBreakListener(Collection<CustomEnchantment> enchantments) {
        CustomEnchantmentCatalog catalog = new CustomEnchantmentCatalog(enchantments);
        this.resolver = new CustomEnchantmentResolver(catalog);
        this.dispatcher = new CustomEnchantmentDispatcher(catalog);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        var activeEnchantments = resolver.resolve(tool);
        if (activeEnchantments.isEmpty()) return;

        BreakingBlocksEnchantmentContext context = new BreakingBlocksEnchantmentContext(player, event.getBlock(), tool);
        dispatcher.dispatchBlockBreak(context, activeEnchantments);
    }
}
