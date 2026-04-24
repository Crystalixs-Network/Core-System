package net.crystalixs.core.paper.listener;

import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentCatalog;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentDispatcher;
import net.crystalixs.core.paper.enchantment.CustomEnchantmentResolver;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.BreakingBlocksContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.CombatContext;
import net.crystalixs.core.paper.enchantment.EnchantmentContext.InteractContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class CustomEnchantmentListener implements Listener {

    private final CustomEnchantmentResolver resolver;
    private final CustomEnchantmentDispatcher dispatcher;

    public CustomEnchantmentListener(Collection<CustomEnchantment> enchantments) {
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

        BreakingBlocksContext context = new BreakingBlocksContext(event, player, event.getBlock(), tool);
        dispatcher.dispatch(context, activeEnchantments);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        var activeEnchantments = resolver.resolve(tool);
        if (activeEnchantments.isEmpty()) return;

        InteractContext context = new InteractContext(event, player, tool, event.getAction(), event.getClickedBlock());
        dispatcher.dispatch(context, activeEnchantments);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombatDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        ItemStack tool = killer.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        var activeEnchantments = resolver.resolve(tool);
        if (activeEnchantments.isEmpty()) return;

        CombatContext context = new CombatContext(event, killer, tool, event.getEntity());
        dispatcher.dispatch(context, activeEnchantments);
    }
}
