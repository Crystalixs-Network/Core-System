package net.crystalixs.core.paper.ignore;

import net.crystalixs.core.persistence.store.PlayerIgnoreStore;
import org.bukkit.entity.Player;

public final class DefaultPlayerIgnoreService implements PlayerIgnoreService {

    private final PlayerIgnoreStore store;

    public DefaultPlayerIgnoreService(PlayerIgnoreStore store) {
        this.store = store;
    }

    @Override
    public void ignorePlayer(Player actor, Player target) {
        store.create(actor.getUniqueId(), target.getUniqueId());
    }

    @Override
    public boolean isIgnoredByPlayer(Player actor, Player target) {
        return store.exists(actor.getUniqueId(), target.getUniqueId());
    }
}
