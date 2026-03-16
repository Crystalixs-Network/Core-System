package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

public class WorkbenchCommand extends PaperCommand {

    public WorkbenchCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("workbench", "wb")
                .commandDescription(RichDescription.translatable("command.workbench.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.workbench"))
                .handler(context -> {
                    Player player = context.sender().player();
                    player.openInventory(MenuType.CRAFTING.create(player));
                }));
    }
}
