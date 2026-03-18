package net.crystalixs.core.velocity.command;

import net.crystalixs.core.velocity.CorePlugin;
import net.crystalixs.core.velocity.command.cloud.VelocityCommand;
import net.crystalixs.core.velocity.command.cloud.VelocityCommandSource;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class NetworkHelpCommand extends VelocityCommand {

    private final BackendHelpCatalogCache cache;

    public NetworkHelpCommand(CorePlugin plugin, BackendHelpCatalogCache cache) {
        super(plugin);
        this.cache = cache;
    }

    @Override
    public void registerTo(@NotNull CommandManager<VelocityCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("networkhelp")
                .handler(context -> {
                    context.sender().plattformSender().sendMessage(text(
                            "Backend-Kataloge: " + cache.all().size() + ", Entries: " + cache.totalEntries()
                    ));
                    cache.all().forEach(catalog -> context.sender().plattformSender().sendMessage(text(
                            "- " + catalog.sourceId() + " (" + catalog.entries().size() + ")"
                    )));
                }));
    }
}
