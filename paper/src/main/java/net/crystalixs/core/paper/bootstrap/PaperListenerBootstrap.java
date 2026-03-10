package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.paper.listener.PlayerRegistrationListener;
import net.crystalixs.core.persistence.api.PersistenceContext;
import org.bukkit.plugin.PluginManager;

public final class PaperListenerBootstrap {

    public void register(PaperPluginRuntime runtime, PersistenceContext persistenceContext) {
        final PluginManager pluginManager = runtime.plugin().getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerRegistrationListener(
                        runtime.componentLogger("listener").child("player-registration"),
                        persistenceContext.players()),
                runtime.plugin());
    }
}
