package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.command.util.InventorySeeService;
import net.crystalixs.core.paper.command.util.SitService;
import net.crystalixs.core.paper.command.util.VanishService;
import net.crystalixs.core.paper.listener.HomeRenameAnvilListener;
import net.crystalixs.core.paper.listener.InventorySeeListener;
import net.crystalixs.core.paper.listener.SitListener;
import net.crystalixs.core.paper.listener.VanishListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperListenerBootstrap {

    public void register(PaperPluginRuntime runtime,
                         SitService sitService,
                         InventorySeeService inventorySeeService,
                         VanishService vanishService
    ) {
        final PluginManager pluginManager = runtime.plugin().getServer().getPluginManager();
        final JavaPlugin plugin = runtime.plugin();

        pluginManager.registerEvents(new SitListener(sitService), plugin);
        pluginManager.registerEvents(new HomeRenameAnvilListener(plugin), plugin);
        pluginManager.registerEvents(new InventorySeeListener(inventorySeeService), plugin);
        pluginManager.registerEvents(new VanishListener(plugin, vanishService), plugin);
    }
}
