package net.crystalixs.core.paper;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.LogManager;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.translation.HotReloadWatcher;
import net.crystalixs.core.common.translation.TranslationBundleMeta;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.paper.command.PaperCommandSource;
import net.crystalixs.core.paper.command.PaperPlayerCommandSource;
import net.crystalixs.core.paper.translation.PaperTranslationBundleLoader;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CorePlugin extends JavaPlugin {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final MiniMessage miniMessage = MiniMessage.builder()
            .build();
    private LogFactory logging;
    private StructuredLogger logger;

    private HotReloadWatcher watcher;

    @Override
    public void onEnable() {
        this.logging = LogManager.createForJavaUtil(getLogger(), getDataPath().resolve("logs"), "paper-core");
        this.logger = logging.logger("core");
        registerTranslations();
        registerCommands();

        logger.info("plugin enabled");
    }

    @Override
    public void onDisable() {
        scheduler.shutdownNow();
        if (watcher != null) {
            watcher.stop();
        }
        if (logger != null) {
            logger.info("plugin disabled");
        }
        if (logging != null) {
            logging.close();
        }
    }

    public StructuredLogger logger() {
        return logger;
    }

    private void registerCommands() {
        PaperCommandManager<PaperCommandSource> commandManager = PaperCommandManager.builder(senderMapper())
                .executionCoordinator(ExecutionCoordinator.<PaperCommandSource>builder().build())
                .buildOnEnable(this);

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

    private void registerTranslations() {
        PaperTranslationBundleLoader translationLoader = new PaperTranslationBundleLoader(this, logger.child("translations"));
        TranslationProvider provider = TranslationProvider.builder()
                .logger(logger)
                .withMiniMessage(miniMessage)
                .withLoader(translationLoader)
                .bundle(TranslationBundleMeta.builder()
                        .bundleName("messages")
                        .defaultLocale(Locale.GERMANY)
                        .build()
                )
                .language(Locale.GERMANY)
                .build();

        // Hier fehlt noch der Config check
        watcher = new HotReloadWatcher(logger, scheduler, getDataPath().resolve("lang"), 1000L, provider::reload);
        watcher.start();
    }
}
