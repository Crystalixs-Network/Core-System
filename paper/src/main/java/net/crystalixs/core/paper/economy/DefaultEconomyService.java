package net.crystalixs.core.paper.economy;

import net.crystalixs.core.persistence.model.PlayerModel;
import net.crystalixs.core.persistence.store.PlayerStore;

import java.util.UUID;

public final class DefaultEconomyService implements EconomyService {

    private final PlayerStore store;

    public DefaultEconomyService(PlayerStore store) {
        this.store = store;
    }

    @Override
    public Balance getBalance(UUID playerId) {
        PlayerModel player = getOrCreatePlayer(playerId);
        return new Balance(player.coins(), player.gems());
    }

    @Override
    public long getCoins(UUID playerId) {
        return getOrCreatePlayer(playerId).coins();
    }

    @Override
    public long getGems(UUID playerId) {
        return getOrCreatePlayer(playerId).gems();
    }

    private PlayerModel getOrCreatePlayer(UUID playerId) {
        return store.findById(playerId).orElseGet(() -> {
            PlayerModel model = new PlayerModel(playerId, 0L, 0L, 0L);
            store.create(model);
            return store.findById(playerId).orElse(model);
        });
    }
}
