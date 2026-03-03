package net.crystalixs.core.paper.command;

import net.crystalixs.core.common.command.AbstractCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PaperCommand<I extends JavaPlugin> extends AbstractCommand<PaperAbstractCommandSource, I> {

    public PaperCommand(I plugin) {
        super(plugin);
    }
}
