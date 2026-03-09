package net.crystalixs.core.paper.economy;

import java.util.UUID;

public interface EconomyService {

    Balance getBalance(UUID playerId);

    long getCoins(UUID playerId);

    long getGems(UUID playerId);

}
