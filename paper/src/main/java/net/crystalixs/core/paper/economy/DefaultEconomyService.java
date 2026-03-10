package net.crystalixs.core.paper.economy;

import net.crystalixs.core.persistence.model.Currency;
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

    @Override
    public void addCurrency(UUID playerId, Currency currency, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        PlayerModel model = getOrCreatePlayer(playerId);
        long coins = model.coins();
        long gems = model.gems();

        switch (currency) {
            case COINS -> coins = safeAdd(coins, amount);
            case GEMS -> gems = safeAdd(gems, amount);
        }
        store.update(new PlayerModel(playerId, model.playtime(), coins, gems));
    }

    @Override
    public void setCurrency(UUID playerId, Currency currency, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }

        PlayerModel model = getOrCreatePlayer(playerId);
        long coins = model.coins();
        long gems = model.gems();

        switch (currency) {
            case COINS -> coins = amount;
            case GEMS -> gems = amount;
        }
        store.update(new PlayerModel(playerId, model.playtime(), coins, gems));
    }

    @Override
    public void takeCurrency(UUID playerId, Currency currency, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        PlayerModel model = getOrCreatePlayer(playerId);
        long coins = model.coins();
        long gems = model.gems();

        switch (currency) {
            case COINS -> {
                if (coins < amount) throw new IllegalStateException("Insufficient coins");
                coins -= amount;
            }
            case GEMS -> {
                if (gems < amount) throw new IllegalStateException("Insufficient gems");
                gems -= amount;
            }
        }
        store.update(new PlayerModel(playerId, model.playtime(), coins, gems));
    }

    @Override
    public void transferCoins(UUID fromPlayerId, UUID toPlayerId, long amount) {
        if (fromPlayerId.equals(toPlayerId)) {
            throw new IllegalArgumentException("Cannot transfer coins to yourself");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        PlayerModel from = getOrCreatePlayer(fromPlayerId);
        PlayerModel to = getOrCreatePlayer(fromPlayerId);

        if (from.coins() < amount) {
            throw new IllegalStateException("Insufficient coins");
        }
        long updatedFromCoins = from.coins() - amount;
        long updatedToCoins = safeAdd(to.coins(), amount);

        store.update(new PlayerModel(from.uuid(), from.playtime(), updatedFromCoins, from.gems()));
        store.update(new PlayerModel(to.uuid(), to.playtime(), updatedToCoins, to.gems()));
    }

    private long safeAdd(long current, long delta) {
        if (Long.MAX_VALUE - current < delta) {
            throw new IllegalStateException("Amount overflow");
        }
        return current + delta;
    }

    private PlayerModel getOrCreatePlayer(UUID playerId) {
        return store.findById(playerId).orElseGet(() -> {
            store.create(playerId);
            return store.findById(playerId).orElseThrow(() -> new IllegalStateException("Could not load player after creation: " + playerId));
        });
    }
}
