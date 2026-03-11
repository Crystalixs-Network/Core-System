package net.crystalixs.core.paper.economy;

import net.crystalixs.core.persistence.model.Currency;

import java.util.UUID;

public interface EconomyService {

    Balance getBalance(UUID playerId);

    long getCoins(UUID playerId);

    long getGems(UUID playerId);

    void addCurrency(UUID playerId, Currency currency, long amount);

    void setCurrency(UUID playerId, Currency currency, long amount);

    void takeCurrency(UUID playerId, Currency currency, long amount);

    void transferCoins(UUID fromPlayerId, UUID toPlayerId, long amount);

}
