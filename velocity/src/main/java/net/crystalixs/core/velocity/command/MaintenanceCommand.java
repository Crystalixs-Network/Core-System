package net.crystalixs.core.velocity.command;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import org.incendo.cloud.CommandManager;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;

public class MaintenanceCommand extends VelocityCommand {

    public MaintenanceCommand(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("maintenance")
                .senderType(VelocityCommandSource.class)
                .permission("core.command.maintenance")
                .required("state", booleanParser())
                .handler(context -> {
                    VelocityCommandSource source = context.sender();
                    source.plattformSender().sendMessage(translatable("command.maintenance.enabled"));
                })
        );
    }
}
