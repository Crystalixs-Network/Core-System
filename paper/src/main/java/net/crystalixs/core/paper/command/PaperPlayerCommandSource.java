package net.crystalixs.core.paper.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaperPlayerCommandSource extends PaperCommandSource {

    public PaperPlayerCommandSource(CommandSender plattformSender, CommandSourceStack commandSourceStack) {
        super(plattformSender, commandSourceStack);
    }

    public Player player() {
        return (Player) plattformSender;
    }
}
