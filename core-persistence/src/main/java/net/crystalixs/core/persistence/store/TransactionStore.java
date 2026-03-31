package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.TransactionModel;

import java.util.List;
import java.util.UUID;

public interface TransactionStore {

    void create(TransactionModel model);

    List<TransactionModel> findByPlayer(UUID playerId, int limit);

}
