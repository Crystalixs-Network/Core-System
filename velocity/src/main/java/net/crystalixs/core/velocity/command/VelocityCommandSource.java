package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import net.crystalixs.core.common.command.AbstractCommandSource;
import net.kyori.adventure.audience.Audience;

public class VelocityCommandSource extends AbstractCommandSource<CommandSource> implements Audience {

    public VelocityCommandSource(CommandSource plattformSender) {
        super(plattformSender);
    }
}
