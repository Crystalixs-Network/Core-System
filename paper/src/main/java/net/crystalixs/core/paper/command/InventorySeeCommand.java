package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.InventorySeeService;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;

public final class InventorySeeCommand extends PaperCommand {

    private final InventorySeeService service;

    public InventorySeeCommand(CorePlugin plugin, InventorySeeService service) {
        super(plugin);
        this.service = service;
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("invsee")
                .commandDescription(RichDescription.translatable("command.invsee.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.invsee"))
                .required("player", playerParser(), RichDescription.translatable("command.invsee.description.player"))
                .handler(context -> {
                    Player viewer = context.sender().player();
                    Player target = context.get("player");
                    boolean canModify = viewer.hasPermission("core.command.invsee.modify");

                    service.open(viewer, target, canModify);
                }));
    }
}
