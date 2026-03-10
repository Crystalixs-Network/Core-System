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

        getOrCreatePlayer(playerId);
        store.addCurrency(playerId, currency, amount);

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

        store.setCurrency(playerId, currency, amount);

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

        getOrCreatePlayer(playerId);
        boolean success = store.takeCurrency(playerId, currency, amount);
        if (!success) {
            throw new IllegalStateException("Insufficient " + currency.name().toLowerCase());
        }

        transaction(TransactionType.ADMIN_TAKE, currency, amount, playerId, null, null, "admin_take");
        auditSuccess("economy.takeCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_take");
    }

    @Override
    public void transferCoins(UUID fromPlayerId, UUID toPlayerId, long amount) {
        try {
            if (fromPlayerId.equals(toPlayerId)) {
                throw new IllegalArgumentException("Cannot transfer coins to yourself");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be > 0");
            }

            boolean success = store.transferCoins(fromPlayerId, toPlayerId, amount);
            if (!success) {
                throw new IllegalStateException("Insufficient coins");
            }
            transaction(TransactionType.PAY, Currency.COINS, amount, fromPlayerId, toPlayerId, fromPlayerId, "player_transfer");
            auditSuccess("economy.transferCoins", "fromPlayerId", fromPlayerId, "toPlayerId", toPlayerId, "amount", amount, "reason", "player_transfer");

        } catch (RuntimeException exception) {
            auditFailed("economy.transferCoins", exception.getMessage(), "fromPlayerId", fromPlayerId, "toPlayerId", toPlayerId, "amount", amount, "reason", "player_transfer");
            throw exception;
        }
    }

    private void auditSuccess(String action, Object... payloadPairs) {
        audits.create(new AuditModel(0L, action, payload(payloadPairs), "SUCCESS", null));
    }

    private void auditFailed(String action, String error, Object... payloadPairs) {
        audits.create(new AuditModel(0, action, payload(concat(payloadPairs, error)), "FAILED", null));
    }

    private void transaction(TransactionType type, Currency currency, long amount, UUID fromPlayerId, UUID toPlayerId, UUID actorPlayerId, String reason) {
        transactions.create(new TransactionModel(0L, type, currency, amount, fromPlayerId, toPlayerId, actorPlayerId, reason, null));
    }

    private Object[] concat(Object[] pairs, Object value) {
        Object[] merged = new Object[pairs.length + 2];
        System.arraycopy(pairs, 0, merged, 0, pairs.length);
        merged[pairs.length] = "error";
        merged[pairs.length + 1] = value;
        return merged;
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
