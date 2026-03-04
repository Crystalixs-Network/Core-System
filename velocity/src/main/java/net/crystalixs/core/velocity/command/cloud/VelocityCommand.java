package net.crystalixs.core.velocity.command.cloud;

import net.crystalixs.core.common.command.AbstractCommand;
import net.crystalixs.core.velocity.CorePlugin;

public abstract class VelocityCommand extends AbstractCommand<VelocityCommandSource, CorePlugin> {

    public VelocityCommand(CorePlugin plugin) {
        super(plugin);
    }
}
