package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.CoreCommand;
import net.crystalixs.core.velocity.command.GlobalFindCommand;
import net.crystalixs.core.velocity.command.GlobalTeleportCommand;
import net.crystalixs.core.velocity.command.HelpCommand;
import net.crystalixs.core.velocity.command.MaintenanceCommand;
import net.crystalixs.core.velocity.command.OnlineCommand;
import net.crystalixs.core.velocity.command.ProxyStopCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class VelocityCommandBootstrap {

    public void register(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater, TranslationProvider provider) {
        final VelocityCommandManager<VelocityCommandSource> commandManager = new VelocityCommandManager<>(
                runtime.pluginContainer(),
                runtime.server(),
                ExecutionCoordinator.<VelocityCommandSource>builder().build(),
                senderMapper()
        );

        MinecraftExceptionHandler.create(VelocityCommandSource::plattformSender)
                .decorator(component -> text().append(translatable("prefix")).append(component).build())
                .defaultHandlers()
                .registerTo(commandManager);

        new ProxyStopCommand(plugin, runtime.server()).registerTo(commandManager);
        new CoreCommand(plugin, configUpdater, provider).registerTo(commandManager);
        new MaintenanceCommand(plugin, configUpdater, runtime.server(), runtime.miniMessage()).registerTo(commandManager);
        new HelpCommand(plugin).registerTo(commandManager);
        new GlobalFindCommand(plugin).registerTo(commandManager);
        new GlobalTeleportCommand(plugin).registerTo(commandManager);
        new OnlineCommand(plugin).registerTo(commandManager);
    }

    private @NotNull SenderMapper<CommandSource, VelocityCommandSource> senderMapper() {
        return SenderMapper.create(
                source -> source instanceof Player player
                        ? new VelocityPlayerCommandSource(player)
                        : new VelocityCommandSource(source),
                VelocityCommandSource::plattformSender
        );
    }
}
