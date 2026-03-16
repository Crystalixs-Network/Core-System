package net.crystalixs.core.paper.command;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.cloud.PaperCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

public class SitCommand extends PaperCommand {

    public SitCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NotNull CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("sit")
                .commandDescription(RichDescription.translatable("command.sit.description.main"))
                .senderType(PaperPlayerCommandSource.class)
                .permission(Permission.of("core.command.sit"))
                .handler(context -> {
                    Player player = context.sender().player();

                    if (player.isInsideVehicle()) {
                        player.leaveVehicle();
                        return;
                    }

                    Location location = player.getLocation().clone().subtract(0d, 1.2d, 0d);
                    player.getWorld().spawn(location, ArmorStand.class, seat -> {
                        seat.setInvisible(true);
                        seat.setMarker(true);
                        seat.setInvulnerable(true);
                        seat.setSilent(true);
                        seat.setGravity(false);
                        seat.addPassenger(player);
                    });
                }));
    }
}
