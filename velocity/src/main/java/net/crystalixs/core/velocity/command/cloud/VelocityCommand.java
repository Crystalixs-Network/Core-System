package net.crystalixs.core.velocity.command.cloud;

import com.velocitypowered.api.plugin.PluginContainer;
import net.crystalixs.core.common.command.AbstractCommand;

public abstract class VelocityCommand<I extends PluginContainer> extends AbstractCommand<VelocityCommandSource, I> {

    public VelocityCommand(I plugin) {
        super(plugin);
    }
}
