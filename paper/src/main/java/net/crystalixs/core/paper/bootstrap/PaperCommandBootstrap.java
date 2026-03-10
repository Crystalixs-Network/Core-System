package net.crystalixs.core.paper.bootstrap;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.BalanceCommand;
import net.crystalixs.core.paper.command.CoinsCommand;
import net.crystalixs.core.paper.command.PayCommand;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.economy.DefaultEconomyService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class PaperCommandBootstrap {

    private final PaperPluginRuntime runtime;

    public PaperCommandBootstrap(PaperPluginRuntime runtime) {
        this.runtime = runtime;
    }

    public void registerCommands() {
        final PaperCommandManager<PaperCommandSource> commandManager = PaperCommandManager.builder(senderMapper())
                .executionCoordinator(ExecutionCoordinator.<PaperCommandSource>builder().build())
                .buildOnEnable(runtime.plugin());

        MinecraftExceptionHandler.create(PaperCommandSource::plattformSender)
                .decorator(component -> text().append(translatable("prefix")).append(component).build())
                .defaultHandlers()
                .registerTo(commandManager);

        CorePlugin plugin = (CorePlugin) runtime.plugin();
        var persistence = plugin.persistence();
        var service = new DefaultEconomyService(persistence.players(), persistence.transactions(), persistence.audits());

        new CoinsCommand(plugin, service).registerTo(commandManager);
        new BalanceCommand(plugin, service).registerTo(commandManager);
        new PayCommand(plugin, service).registerTo(commandManager);
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
