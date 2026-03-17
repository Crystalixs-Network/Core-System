package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.util.PrivateMessageService;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand extends PaperCommand {

    private final PrivateMessageService service;

    public ReplyCommand(CorePlugin plugin, PrivateMessageService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {

    }
}
