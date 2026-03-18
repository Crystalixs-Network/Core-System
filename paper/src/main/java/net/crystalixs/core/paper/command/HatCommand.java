package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

public final class HatCommand extends PaperCommand {

    public HatCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("hat")
                .commandDescription(RichDescription.translatable("command.hat.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.hat"))
                .handler(context -> {
                    Player player = context.sender().player();

                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack.getType().isAir()) {
                        player.sendMessage(translatable("command.hat.error.no-item"));
                        return;
                    }

                    ItemStack oldHelmet = player.getInventory().getHelmet();
                    player.getInventory().setHelmet(itemStack.clone());
                    player.getInventory().setItemInMainHand(oldHelmet);

                    player.sendMessage(translatable("command.hat.success"));
                }));
    }
}
