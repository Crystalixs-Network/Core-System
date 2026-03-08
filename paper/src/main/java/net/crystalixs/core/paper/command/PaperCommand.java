package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.command.AbstractCommand;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PaperCommand<I extends JavaPlugin> extends AbstractCommand<PaperCommandSource, I> {

    public PaperCommand(I plugin) {
        super(plugin);
    }

    protected StructuredLogger commandLogger(String commandName) {
        if (plugin instanceof CorePlugin corePlugin) {
            return corePlugin.commandLogger(commandName);
        }
        throw new IllegalStateException("Paper command logger is only available for CorePlugin");
    }
}
