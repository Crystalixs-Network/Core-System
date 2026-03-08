package net.crystalixs.core.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.crystalixs.core.velocity.config.Tablist;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerConnectionListener {

    private final VelocityConfigUpdater updater;
    private final MiniMessage miniMessage;

    public PlayerConnectionListener(VelocityConfigUpdater updater, MiniMessage miniMessage) {
        this.updater = updater;
        this.miniMessage = miniMessage;
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

        player.sendPlayerListHeaderAndFooter(
                tablist.headerComponent(miniMessage),
                tablist.footerComponent(miniMessage));
    }

}
