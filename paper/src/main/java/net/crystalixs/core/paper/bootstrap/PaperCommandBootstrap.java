package net.crystalixs.core.paper.bootstrap;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.*;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.PrivateMessageService;
import net.crystalixs.core.paper.command.util.SitService;
import net.crystalixs.core.paper.command.util.TeleportRequestService;
import net.crystalixs.core.paper.command.util.TrashService;
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
    private final TrashService trashService;
    private final SitService sitService;
    private final PrivateMessageService messageService;
    private final TeleportRequestService teleportService;

    public PaperCommandBootstrap(PaperPluginRuntime runtime) {
        this.runtime = runtime;
        this.trashService = new TrashService(runtime.plugin());
        this.sitService = new SitService(runtime.plugin());
        this.messageService = new PrivateMessageService();
        this.teleportService = new TeleportRequestService(runtime.plugin());
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
        var economyService = new DefaultEconomyService(persistence.players(), persistence.transactions(), persistence.audits());

        new CoinsCommand(plugin, economyService).registerTo(commandManager);
        new BalanceCommand(plugin, economyService).registerTo(commandManager);
        new PayCommand(plugin, economyService).registerTo(commandManager);
        new EconomyCommand(plugin, economyService).registerTo(commandManager);
        new HatCommand(plugin).registerTo(commandManager);
        new EnderchestCommand(plugin).registerTo(commandManager);
        new WorkbenchCommand(plugin).registerTo(commandManager);
        new AnvilCommand(plugin).registerTo(commandManager);
        new RepairCommand(plugin).registerTo(commandManager);
        new SkullCommand(plugin).registerTo(commandManager);
        new TrashCommand(plugin, trashService).registerTo(commandManager);
        new SitCommand(plugin, sitService).registerTo(commandManager);
        new SignCommand(plugin).registerTo(commandManager);
        new MessageCommand(plugin, messageService).registerTo(commandManager);
        new ReplyCommand(plugin, messageService).registerTo(commandManager);
        new TpaCommand(plugin, teleportService).registerTo(commandManager);
    }

    public void shutdown() {
        trashService.shutdown();
        sitService.shutdown();
        messageService.shutdown();
        teleportService.shutdown();
    }

    public SitService sitService() {
        return sitService;
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
