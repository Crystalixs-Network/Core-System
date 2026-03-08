package net.crystalixs.core.paper.bootstrap;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.paper.command.PaperCommandSource;
import net.crystalixs.core.paper.command.PaperPlayerCommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

public final class PaperCommandBootstrap {

    private final PaperPluginRuntime runtime;

    public PaperCommandBootstrap(PaperPluginRuntime runtime) {
        this.runtime = runtime;
    }

    public void registerCommands() {
        PaperCommandManager<PaperCommandSource> commandManager = PaperCommandManager.builder(senderMapper())
                .executionCoordinator(ExecutionCoordinator.<PaperCommandSource>builder().build())
                .buildOnEnable(runtime.plugin());

        // Hier Commands registrieren
    }

    private @NotNull SenderMapper<CommandSourceStack, PaperCommandSource> senderMapper() {
        return SenderMapper.create(commandSourceStack -> {
            CommandSender sender = commandSourceStack.getSender();
            return sender instanceof Player player
                    ? new PaperPlayerCommandSource(player, commandSourceStack)
                    : new PaperCommandSource(sender, commandSourceStack);
        }, PaperCommandSource::commandSourceStack);
    }
}
