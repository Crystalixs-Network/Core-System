package net.crystalixs.core.paper.command.cloud;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.common.command.AbstractCommandSource;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

public class PaperCommandSource extends AbstractCommandSource<CommandSender> implements Audience {

    private final CommandSourceStack commandSourceStack;

    public PaperCommandSource(CommandSender plattformSender, CommandSourceStack commandSourceStack) {
        super(plattformSender);
        this.commandSourceStack = commandSourceStack;
    }

    public CommandSourceStack commandSourceStack() {
        return commandSourceStack;
    }
}
