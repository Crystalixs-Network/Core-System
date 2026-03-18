package net.crystalixs.core.velocity.bootstrap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.*;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.command.cloud.VelocityPlayerCommandSource;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;
import net.crystalixs.core.velocity.help.UnifiedHelpService;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class VelocityCommandBootstrap {

    private final BackendHelpCatalogCache backendHelpCache = new BackendHelpCatalogCache();

    public void register(CorePlugin plugin, VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater, TranslationProvider provider) {
        backendHelpCache.loadPreviewSample(); // POC seed, bis Transport steht

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

        final UnifiedHelpService helpService = new UnifiedHelpService(commandManager, backendHelpCache);

        new ProxyStopCommand(plugin, runtime.server()).registerTo(commandManager);
        new CoreCommand(plugin, configUpdater, provider).registerTo(commandManager);
        new MaintenanceCommand(plugin, configUpdater, runtime.server(), runtime.miniMessage()).registerTo(commandManager);
        new HelpCommand(plugin, helpService).registerTo(commandManager);
        new GlobalFindCommand(plugin).registerTo(commandManager);
        new GlobalTeleportCommand(plugin).registerTo(commandManager);
        new OnlineCommand(plugin).registerTo(commandManager);

        new NetworkHelpCommand(plugin, backendHelpCache).registerTo(commandManager);
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
