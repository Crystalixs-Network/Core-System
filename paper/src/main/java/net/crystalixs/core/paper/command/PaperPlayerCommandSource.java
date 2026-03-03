package net.crystalixs.core.paper.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class PaperPlayerCommandSource extends PaperAbstractCommandSource {

    public PaperPlayerCommandSource(CommandSender plattformSender, CommandSourceStack commandSourceStack) {
        super(plattformSender, commandSourceStack);
    }
}
