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
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import static net.kyori.adventure.text.Component.translatable;

public final class RepairCommand extends PaperCommand {

    public RepairCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("repair")
                .commandDescription(RichDescription.translatable("command.repair.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.repair"))
                .handler(context -> {
                    Player player = context.sender().player();
                    ItemStack itemStack = player.getInventory().getItemInMainHand();

                    if (itemStack.getType().isAir() || itemStack.getType().getMaxDurability() <= 0) {
                        player.sendMessage(translatable("command.repair.error.invalid-item"));
                        return;
                    }

                    ItemStack repaired = new ItemBuilder(itemStack.clone())
                            .setDamage(0)
                            .get();

                    player.getInventory().setItemInMainHand(repaired);
                    player.sendMessage(translatable("command.repair.success"));
                }));
    }
}
