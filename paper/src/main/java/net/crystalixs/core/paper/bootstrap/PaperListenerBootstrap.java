package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.command.util.SitService;
import net.crystalixs.core.paper.listener.SitListener;
import net.crystalixs.core.persistence.api.PersistenceContext;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperListenerBootstrap {

    public void register(PaperPluginRuntime runtime, SitService sitService) {
        final PluginManager pluginManager = runtime.plugin().getServer().getPluginManager();
        final JavaPlugin plugin = runtime.plugin();

        pluginManager.registerEvents(new SitListener(sitService), plugin);
    }
}
