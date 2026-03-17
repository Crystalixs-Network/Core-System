package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.SitService;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

public final class SitCommand extends PaperCommand {

    private final SitService service;

    public SitCommand(CorePlugin plugin, SitService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("sit")
                .commandDescription(RichDescription.translatable("command.sit.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.sit"))
                .handler(context -> service.toggle(context.sender().player())));
    }
}
