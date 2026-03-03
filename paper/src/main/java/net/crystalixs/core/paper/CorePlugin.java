package net.crystalixs.core.paper;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.crystalixs.core.paper.command.PaperAbstractCommandSource;
import net.crystalixs.core.paper.command.PaperPlayerAbstractCommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

public class CorePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    private void registerCommands() {
        PaperCommandManager<PaperAbstractCommandSource> commandManager = PaperCommandManager.builder(senderMapper())
                .executionCoordinator(ExecutionCoordinator.<PaperAbstractCommandSource>builder().build())
                .buildOnEnable(this);

        // Hier Commands registrieren
    }

    private @NotNull SenderMapper<CommandSourceStack, PaperAbstractCommandSource> senderMapper() {
        return SenderMapper.create(commandSourceStack -> {
            CommandSender sender = commandSourceStack.getSender();
            return sender instanceof Player player
                    ? new PaperPlayerAbstractCommandSource(player, commandSourceStack)
                    : new PaperAbstractCommandSource(sender, commandSourceStack);

        }, PaperAbstractCommandSource::commandSourceStack);
    }

}
