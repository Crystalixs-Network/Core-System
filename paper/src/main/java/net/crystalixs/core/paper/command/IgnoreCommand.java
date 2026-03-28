package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.ignore.PlayerIgnoreService;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public final class IgnoreCommand extends PaperCommand {

    private final PlayerIgnoreService service;

    public IgnoreCommand(CorePlugin plugin, PlayerIgnoreService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
    }
}
