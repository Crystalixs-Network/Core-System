package net.crystalixs.core.paper;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.common.translation.HotReloadWatcher;
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

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CorePlugin extends JavaPlugin {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final MiniMessage miniMessage = MiniMessage.builder()
            .build();

    @Override
    public void onEnable() {
        registerTranslations();
        registerCommands();

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
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
        try {
            PaperTranslationBundleLoader translationLoader = new PaperTranslationBundleLoader(this);
            TranslationProvider provider = new TranslationProvider(miniMessage, translationLoader, Locale.GERMANY);
            provider.load("messages", Locale.GERMANY);

            // Hier fehlt noch der Config check
            enableHotReloading(provider);

        } catch (IOException exception) {
            getLogger().warning("Unable to load ressource bundle: " + exception.getMessage());
        }
    }

    private void enableHotReloading(TranslationProvider provider) {
        new HotReloadWatcher(scheduler, getDataPath().resolve("lang"), 1000L, () -> {
            try {
                provider.reload();
            } catch (IOException exception) {
                getLogger().warning("Failed to reload translations: " + exception.getMessage());
            }
        }).start();

    }

}
