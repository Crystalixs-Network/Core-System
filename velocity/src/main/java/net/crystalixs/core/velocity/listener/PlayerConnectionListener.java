package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.config.Tablist;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerConnectionListener {

    private final StructuredLogger logger;
    private final MiniMessage miniMessage;
    private final VelocityConfigUpdater updater;

    public PlayerConnectionListener(StructuredLogger logger, MiniMessage miniMessage, VelocityConfigUpdater updater) {
        this.logger = logger;
        this.miniMessage = miniMessage;
        this.updater = updater;
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        VelocityConfig config = updater.current();

        if (!config.maintenance().isEnabled()) return;
        if (player.hasPermission("core.bypass.maintenance")) return;

        player.disconnect(config.maintenance().screen().construct(miniMessage));
    }

    @Subscribe
    public void onBackendConnect(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        Tablist tablist = updater.current().tablist();

        String serverName = player.getCurrentServer()
                .map(connection -> connection.getServerInfo().getName())
                .orElseGet(() -> {
                    logger.warn("failed to resolve server name for tablist", LogMetadata
                            .event("tablist.server_name_unresolved")
                            .and(LogMetadata.Key.ACTOR, player.getUsername()));
                    return "fehler";
                });

        player.sendPlayerListHeaderAndFooter(
                tablist.headerComponent(miniMessage),
                tablist.footerComponent(miniMessage, serverName));
    }

}
