package net.crystalixs.core.paper.listener;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.store.PlayerStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public final class PlayerRegistrationListener implements Listener {

    private final StructuredLogger logger;
    private final PlayerStore store;

    public PlayerRegistrationListener(StructuredLogger logger, PlayerStore store) {
        this.logger = logger;
        this.store = store;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        if (store.exists(playerId)) {
            return;
        }

        store.create(playerId);
        logger.info("player registered", LogMetadata
                .event("player.registered")
                .and(LogMetadata.Key.SUBJECT, playerId.toString()));
    }
}
