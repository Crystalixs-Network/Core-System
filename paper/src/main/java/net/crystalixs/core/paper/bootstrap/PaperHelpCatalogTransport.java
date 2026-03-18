package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalogCodec;
import net.crystalixs.core.common.command.help.NetworkHelpSyncProtocol;
import net.crystalixs.core.paper.command.cloud.PaperCommandSource;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;

public final class PaperHelpCatalogTransport {

    private final JavaPlugin plugin;
    private final PaperHelpCatalogPublisher publisher;

    public PaperHelpCatalogTransport(JavaPlugin plugin, PaperHelpCatalogPublisher publisher) {
        this.plugin = plugin;
        this.publisher = publisher;
    }

    public void registerChannel() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, NetworkHelpSyncProtocol.CHANNEL);
    }

    public void publish(CommandManager<PaperCommandSource> commandManager) {
        NetworkHelpCatalog catalog = publisher.snapshot(commandManager);
        byte[] payload = NetworkHelpCatalogCodec.encode(catalog);

        Player target = plugin.getServer().getOnlinePlayers().stream().findFirst().orElse(null);
        if (target != null) {
            target.sendPluginMessage(plugin, NetworkHelpSyncProtocol.CHANNEL, payload);
        }
    }
}
