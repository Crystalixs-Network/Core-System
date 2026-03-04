package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.config.Maintenance;
import net.crystalixs.core.velocity.config.VelocityConfig;
import org.incendo.cloud.CommandManager;
import org.jspecify.annotations.NonNull;

import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;

public class MaintenanceCommand extends VelocityCommand {

    private final VelocityConfig config;

    public MaintenanceCommand(CorePlugin plugin, VelocityConfig config) {
        super(plugin);
        this.config = config;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("maintenance")
                .senderType(VelocityCommandSource.class)
                .permission("core.command.maintenance")
                .required("state", booleanParser())
                .handler(context -> {
                    final CommandSource source = context.sender().plattformSender();
                    final Maintenance maintenance = config.maintenance();

                    boolean state = context.get("state");

                    if (state && maintenance.isEnabled()) {
                        source.sendMessage(translatable("command.maintenance.error.already_enabled"));
                        return;
                    }
                    if (!state && !maintenance.isEnabled()) {
                        source.sendMessage(translatable("command.maintenance.error.already_disabled"));
                        return;
                    }

                    toggleMaintenance(source, state);
                })
        );
    }

    private void toggleMaintenance(CommandSource source, boolean newState) {
        if (newState) {
            config.maintenance().enable();
            source.sendMessage(translatable("command.maintenance.enabled"));
            return;
        }

        config.maintenance().disable();
        source.sendMessage(translatable("command.maintenance.disabled"));
    }
}
