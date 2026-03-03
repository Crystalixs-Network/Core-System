package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;

public class MaintenanceCommand extends VelocityCommand {

    public MaintenanceCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("maintenance")
                .senderType(VelocityPlayerCommandSource.class)
                .permission("core.command.maintenance")
                .required("state", booleanParser())
                .handler(context -> {
                    final Player player = context.sender().player();
                    player.sendMessage(text("Hello world!", NamedTextColor.GREEN));
                })
        );
    }
}
