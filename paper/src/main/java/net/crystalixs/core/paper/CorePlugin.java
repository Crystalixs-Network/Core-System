package net.crystalixs.core.paper;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.bootstrap.PaperPluginBootstrap;
import net.crystalixs.core.persistence.api.PersistenceContext;
import org.bukkit.plugin.java.JavaPlugin;

public class CorePlugin extends JavaPlugin {

    private PaperPluginBootstrap bootstrap;

    @Override
    public void onEnable() {
        bootstrap = PaperPluginBootstrap.create(this);
        bootstrap.enable();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.disable();
        }
    }

    public StructuredLogger componentLogger(String component, String... nestedComponents) {
        if (bootstrap == null) {
            throw new IllegalStateException("Plugin bootstrap is not available");
        }
        return bootstrap.runtime().componentLogger(component, nestedComponents);
    }

    public StructuredLogger commandLogger(String commandName) {
        return componentLogger("commands", commandName);
    }

    public PersistenceContext persistence() {
        if (bootstrap == null) {
            throw new IllegalStateException("Plugin bootstrap is not available");
        }
        return bootstrap.persistence();
    }
}
