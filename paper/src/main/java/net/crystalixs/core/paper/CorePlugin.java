package net.crystalixs.core.paper;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.paper.command.PaperCommandSource;
import net.crystalixs.core.paper.command.PaperPlayerCommandSource;
import net.crystalixs.core.paper.translation.PaperTranslationBundleLoader;
import net.crystalixs.core.paper.translation.PaperTranslationProvider;
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

public class CorePlugin extends JavaPlugin {

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
            PaperTranslationProvider provider = new PaperTranslationProvider(miniMessage, translationLoader);
            provider.load("messages", Locale.GERMAN);

        } catch (IOException exception) {
            getLogger().warning("Unable to load ressource bundle: " + exception.getMessage());
        }
    }

}
