package net.crystalixs.core.velocity.command.cloud;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class VelocityPlayerCommandSource extends VelocityCommandSource {

    public VelocityPlayerCommandSource(CommandSource plattformSender) {
        super(plattformSender);
    }

    public Player player() {
        return (Player) plattformSender;
    }
}
