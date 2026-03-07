package net.crystalixs.core.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.config.Maintenance;
import net.crystalixs.core.velocity.config.VelocityConfigUpdater;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.permission.Permission;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

import static net.kyori.adventure.text.Component.translatable;
import static org.incendo.cloud.parser.standard.BooleanParser.booleanParser;

public class MaintenanceCommand extends VelocityCommand {

    private final VelocityConfigUpdater updater;
    private final ProxyServer proxy;

    public MaintenanceCommand(CorePlugin plugin, VelocityConfigUpdater updater, ProxyServer proxy) {
        super(plugin);
        this.updater = updater;
        this.proxy = proxy;
    }

    @Override
    public void registerTo(@NonNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("maintenance")
                .commandDescription(RichDescription.translatable("command.maintenance.description.main"))
                .senderType(VelocityCommandSource.class)
                .permission(Permission.of("core.command.maintenance"))
                .required("state", booleanParser(), RichDescription.translatable("command.maintenance.description.state"))
                .handler(context -> {
                    final CommandSource source = context.sender().plattformSender();
                    final Maintenance maintenance = updater.current().maintenance();

                    boolean state = context.get("state");

                    if (state && maintenance.isEnabled()) {
                        source.sendMessage(translatable("command.maintenance.error.already_enabled"));
                        return;
                    }
                    if (!state && !maintenance.isEnabled()) {
                        source.sendMessage(translatable("command.maintenance.error.already_disabled"));
                        return;
                    }

                    if (!toggleMaintenance(source, state)) {
                        return;
                    }
                    kickUnauthorized();
                })
        );
    }

    private boolean toggleMaintenance(CommandSource source, boolean state) {
        try {
            updater.setMaintenance(state);
        } catch (IOException exception) {
            source.sendMessage(translatable("command.core.reload.error.io-exception"));
            return false;
        }
        source.sendMessage(translatable(state ? "command.maintenance.enabled" : "command.maintenance.disabled"));
        return true;
    }

    private void kickUnauthorized() {
        for (Player player : proxy.getAllPlayers()) {
            if (player.hasPermission("core.bypass.maintenance"))
                continue;

            player.disconnect(updater.current().maintenance().screen().construct());
        }
    }
}
