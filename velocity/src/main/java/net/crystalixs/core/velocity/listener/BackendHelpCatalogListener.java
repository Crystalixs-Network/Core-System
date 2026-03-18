package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog;
import net.crystalixs.core.common.command.help.NetworkHelpCatalogCodec;
import net.crystalixs.core.common.command.help.NetworkHelpSyncProtocol;
import net.crystalixs.core.velocity.help.BackendHelpCatalogCache;

public class BackendHelpCatalogListener {

    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from(NetworkHelpSyncProtocol.CHANNEL);

    private final BackendHelpCatalogCache cache;

    public BackendHelpCatalogListener(BackendHelpCatalogCache cache) {
        this.cache = cache;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) return;

        NetworkHelpCatalog catalog = NetworkHelpCatalogCodec.decode(event.getData());
        cache.upsert(catalog);
        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}
