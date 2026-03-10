package net.crystalixs.core.paper.economy;

import net.crystalixs.core.persistence.model.*;
import net.crystalixs.core.persistence.store.AuditStore;
import net.crystalixs.core.persistence.store.PlayerStore;
import net.crystalixs.core.persistence.store.TransactionStore;

import java.util.UUID;

public final class DefaultEconomyService implements EconomyService {

    private final PlayerStore store;
    private final TransactionStore transactions;
    private final AuditStore audits;

    public DefaultEconomyService(PlayerStore store, TransactionStore transactions, AuditStore audits) {
        this.store = store;
        this.transactions = transactions;
        this.audits = audits;
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

        transaction(TransactionType.ADMIN_GIVE, currency, amount, null, playerId, null, "admin_give");
        auditSuccess("economy.addCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_give");
    }

    @Override
    public void setCurrency(UUID playerId, Currency currency, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }

        PlayerModel model = getOrCreatePlayer(playerId);
        long previous = switch (currency) {
            case COINS -> model.coins();
            case GEMS -> model.gems();
        };
        long coins = model.coins();
        long gems = model.gems();

        switch (currency) {
            case COINS -> coins = amount;
            case GEMS -> gems = amount;
        }
        store.update(new PlayerModel(playerId, model.playtime(), coins, gems));

        long delta = Math.abs(previous - amount);
        TransactionType type = amount >= previous ? TransactionType.ADMIN_GIVE : TransactionType.ADMIN_TAKE;
        UUID fromPlayerId = amount >= previous ? null : playerId;
        UUID toPlayerId = amount >= previous ? playerId : null;

        transaction(type, currency, delta, fromPlayerId, toPlayerId, null, "admin_set");
        auditSuccess("economy.setCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_set");
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

        transaction(TransactionType.ADMIN_TAKE, currency, amount, playerId, null, null, "admin_take");
        auditSuccess("economy.takeCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_take");
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
        PlayerModel to = getOrCreatePlayer(toPlayerId);

        if (from.coins() < amount) {
            throw new IllegalStateException("Insufficient coins");
        }
        long updatedFromCoins = from.coins() - amount;
        long updatedToCoins = safeAdd(to.coins(), amount);

        store.update(new PlayerModel(from.uuid(), from.playtime(), updatedFromCoins, from.gems()));
        store.update(new PlayerModel(to.uuid(), to.playtime(), updatedToCoins, to.gems()));

        transaction(TransactionType.PAY, Currency.COINS, amount, fromPlayerId, toPlayerId, fromPlayerId, "player_transfer");
        auditSuccess("economy.transferCoins", "fromPlayerId", fromPlayerId, "toPlayerId", toPlayerId, "amount", amount, "reason", "player_transfer");
    }

    private void auditSuccess(String action, Object... payloadPairs) {
        audits.create(new AuditModel(0L, action, payload(payloadPairs), "SUCCESS", null));
    }

    private void transaction(TransactionType type, Currency currency, long amount, UUID fromPlayerId, UUID toPlayerId, UUID actorPlayerId, String reason) {
        transactions.create(new TransactionModel(0L, type, currency, amount, fromPlayerId, toPlayerId, actorPlayerId, reason, null));
    }

    private String payload(Object... pairs) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) builder.append(",");
            builder.append(pairs[i]).append("=").append(pairs[i + 1]);
        }
        return builder.toString();
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
