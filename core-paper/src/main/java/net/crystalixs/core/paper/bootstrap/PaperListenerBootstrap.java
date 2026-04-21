package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.command.util.InventorySeeService;
import net.crystalixs.core.paper.command.util.SitService;
import net.crystalixs.core.paper.command.util.VanishService;
import net.crystalixs.core.paper.display.ScoreboardService;
import net.crystalixs.core.paper.display.TablistService;
import net.crystalixs.core.paper.enchantment.CustomEnchantment;
import net.crystalixs.core.paper.listener.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PaperListenerBootstrap {

    public void register(PaperPluginRuntime runtime,
                         SitService sitService,
                         InventorySeeService inventorySeeService,
                         VanishService vanishService,
                         List<CustomEnchantment> customEnchantments,
                         TablistService tablistService,
                         ScoreboardService scoreboardService
    ) {
        final PluginManager pluginManager = runtime.plugin().getServer().getPluginManager();
        final JavaPlugin plugin = runtime.plugin();

        pluginManager.registerEvents(new SitListener(sitService), plugin);
        pluginManager.registerEvents(new HomeRenameAnvilListener(plugin), plugin);
        pluginManager.registerEvents(new InventorySeeListener(inventorySeeService), plugin);
        pluginManager.registerEvents(new VanishListener(plugin, vanishService), plugin);
        pluginManager.registerEvents(new CustomEnchantmentBlockBreakListener(customEnchantments), plugin);
        if (tablistService != null) pluginManager.registerEvents(new TablistListener(plugin, tablistService), plugin);
        if (scoreboardService != null) pluginManager.registerEvents(new ScoreboardListener(plugin, scoreboardService), plugin);
    }
}
