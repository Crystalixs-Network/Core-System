package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.command.AbstractCommand;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;

public abstract class PaperCommand extends AbstractCommand<PaperCommandSource, CorePlugin> {

    public PaperCommand(CorePlugin plugin) {
        super(plugin);
    }

    protected StructuredLogger commandLogger(String commandName) {
        return plugin.commandLogger(commandName);
    }
}
