package net.crystalixs.core.paper.bootstrap;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.command.*;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import net.crystalixs.core.paper.command.cloud.PaperPlayerCommandSource;
import net.crystalixs.core.paper.command.util.*;
import net.crystalixs.core.paper.config.platform.PaperConfigUpdater;
import net.crystalixs.core.paper.economy.DefaultEconomyService;
import net.crystalixs.core.paper.home.DefaultHomeService;
import net.crystalixs.core.paper.home.HomeGuiFactory;
import net.crystalixs.core.paper.setting.DefaultSettingService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
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
    private final InventorySeeService inventorySeeService;
    private final VanishService vanishService;
    private PaperHelpCatalogTransport helpCatalogTransport;
    private BukkitTask helpCatalogRepublishTask;

    public PaperCommandBootstrap(PaperPluginRuntime runtime) {
        this.runtime = runtime;
        this.trashService = new TrashService(runtime.plugin());
        this.sitService = new SitService(runtime.plugin());
        this.messageService = new PrivateMessageService();
        this.teleportService = new TeleportRequestService(runtime.plugin());
        this.inventorySeeService = new InventorySeeService(runtime.componentLogger("commands").child("invsee"));
        this.vanishService = new VanishService(runtime.plugin());
    }

    public void registerCommands(PaperConfigUpdater configUpdater) {
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
        var homeService = new DefaultHomeService(persistence.players(), persistence.homes());
        var homeGuiFactory = new HomeGuiFactory(plugin, homeService);
        var settingService = new DefaultSettingService(persistence.playerSettings());

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
        new TeleportRequestCommand(plugin, teleportService).registerTo(commandManager);
        new TeleportRequestHereCommand(plugin, teleportService).registerTo(commandManager);
        new TeleportRequestAcceptCommand(plugin, teleportService).registerTo(commandManager);
        new TeleportRequestDenyCommand(plugin, teleportService).registerTo(commandManager);
        new TeleportOverrideCommand(plugin).registerTo(commandManager);
        new TeleportOverrideHereCommand(plugin).registerTo(commandManager);
        new InventorySeeCommand(plugin, inventorySeeService).registerTo(commandManager);
        new VanishCommand(plugin, vanishService).registerTo(commandManager);
        new HomeCommand(plugin, homeService, homeGuiFactory).registerTo(commandManager);

        StructuredLogger helpSyncLogger = runtime.componentLogger("help-sync");
        String redisUri = configUpdater.current().redisSync() == null ? null : configUpdater.current().redisSync().uri();
        String backendId = configUpdater.current().redisSync() == null ? null : configUpdater.current().redisSync().backendId();

        if (backendId == null || backendId.isBlank()) {
            helpSyncLogger.warn("backendId is missing or blank", LogMetadata.event("backendId.missing"));
            return;
        }

        PaperHelpCatalogPublisher publisher = new PaperHelpCatalogPublisher(backendId);
        this.helpCatalogTransport = new PaperHelpCatalogTransport(helpSyncLogger, publisher, redisUri);
        this.helpCatalogTransport.connect();
        this.helpCatalogTransport.publish(commandManager);
        this.helpCatalogRepublishTask = runtime.plugin().getServer().getScheduler().runTaskTimerAsynchronously(
                runtime.plugin(),
                () -> this.helpCatalogTransport.publish(commandManager),
                20L * 30L,
                20L * 30L
        );
    }

    public void shutdown() {
        if (helpCatalogRepublishTask != null) {
            helpCatalogRepublishTask.cancel();
            helpCatalogRepublishTask = null;
        }
        if (helpCatalogTransport != null) {
            helpCatalogTransport.close();
            helpCatalogTransport = null;
        }
        trashService.shutdown();
        sitService.shutdown();
        messageService.shutdown();
        teleportService.shutdown();
    }

    public SitService sitService() {
        return sitService;
    }

    public InventorySeeService inventorySeeService() {
        return inventorySeeService;
    }

    public VanishService vanishService() {
        return vanishService;
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
