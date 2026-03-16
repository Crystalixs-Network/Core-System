package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.TrashService;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

public class TrashCommand extends PaperCommand {

    private final TrashService service;

    public TrashCommand(CorePlugin plugin) {
        super(plugin);
        this.service = new TrashService(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("trash")
                .commandDescription(RichDescription.translatable("command.trash.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.trash"))
                .handler(context -> service.open(context.sender().player())));
    }
}
