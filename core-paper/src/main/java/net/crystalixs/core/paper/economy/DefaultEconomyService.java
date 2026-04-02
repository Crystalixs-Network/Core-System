package net.crystalixs.core.paper.economy;

import net.crystalixs.core.persistence.model.*;
import net.crystalixs.core.persistence.store.AuditStore;
import net.crystalixs.core.persistence.store.PlayerStore;
import net.crystalixs.core.persistence.store.TransactionStore;
import org.bukkit.Bukkit;

import java.util.Set;
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
            throw new EconomyException(EconomyError.INVALID_AMOUNT, "Amount must be > 0");
        }

        getOrCreatePlayer(playerId);
        store.addCurrency(playerId, currency, amount);

        publishMutation(EconomyMutationType.ADD, currency, playerId);
        transaction(TransactionType.ADMIN_GIVE, currency, amount, null, playerId, null, "admin_give");
        audit("economy.addCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_give");
    }

    @Override
    public void setCurrency(UUID playerId, Currency currency, long amount) {
        if (amount < 0) {
            throw new EconomyException(EconomyError.INVALID_AMOUNT, "Amount must be >= 0");
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

        publishMutation(EconomyMutationType.SET, currency, playerId);
        transaction(type, currency, delta, fromPlayerId, toPlayerId, null, "admin_set");
        audit("economy.setCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_set");
    }

    @Override
    public void takeCurrency(UUID playerId, Currency currency, long amount) {
        if (amount <= 0) {
            throw new EconomyException(EconomyError.INVALID_AMOUNT, "Amount must be > 0");
        }

        getOrCreatePlayer(playerId);
        boolean success = store.takeCurrency(playerId, currency, amount);
        if (!success) {
            throw new EconomyException(EconomyError.INSUFFICIENT_FUNDS, "Insufficient " + currency.name().toLowerCase());
        }

        publishMutation(EconomyMutationType.TAKE, currency, playerId);
        transaction(TransactionType.ADMIN_TAKE, currency, amount, playerId, null, null, "admin_take");
        audit("economy.takeCurrency", "playerId", playerId, "currency", currency, "amount", amount, "reason", "admin_take");
    }

    @Override
    public void transferCoins(UUID fromPlayerId, UUID toPlayerId, long amount) {
        if (fromPlayerId.equals(toPlayerId)) {
            throw new EconomyException(EconomyError.SELF_TRANSFER, "Cannot transfer to yourself");
        }
        if (amount <= 0) {
            throw new EconomyException(EconomyError.INVALID_AMOUNT, "Amount must be > 0");
        }

        boolean success = store.transferCoins(fromPlayerId, toPlayerId, amount);
        if (!success) {
            throw new EconomyException(EconomyError.INSUFFICIENT_FUNDS, "Insufficient coins");
        }

        publishMutation(EconomyMutationType.TRANSFER, Currency.COINS, fromPlayerId, toPlayerId);
        transaction(TransactionType.PAY, Currency.COINS, amount, fromPlayerId, toPlayerId, fromPlayerId, "player_transfer");
        audit("economy.transferCoins", "fromPlayerId", fromPlayerId, "toPlayerId", toPlayerId, "amount", amount, "reason", "player_transfer");
    }

    private void publishMutation(EconomyMutationType type, Currency currency, UUID... players) {
        Bukkit.getPluginManager().callEvent(new EconomyMutationEvent(type, currency, Set.of(players)));
    }

    private void audit(String action, Object... payloadPairs) {
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

    private PlayerModel getOrCreatePlayer(UUID playerId) {
        return store.findById(playerId).orElseGet(() -> {
            store.create(playerId);
            return store.findById(playerId).orElseThrow(() -> new EconomyException(EconomyError.PLAYER_CREATION_FAILED, "Could not create player " + playerId));
        });
    }
}
