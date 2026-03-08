package net.crystalixs.core.velocity.command.cloud;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.common.command.AbstractCommand;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.CorePlugin;

public abstract class VelocityCommand extends AbstractCommand<VelocityCommandSource, CorePlugin> {

    public VelocityCommand(CorePlugin plugin) {
        super(plugin);
    }

    protected StructuredLogger commandLogger(String commandName) {
        return plugin.commandLogger(commandName);
    }

    protected String actorName(CommandSource source) {
        if (source instanceof Player player) {
            return player.getUsername();
        }
        return "console";
    }
}
