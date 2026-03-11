package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.rowmapper.RowMapping;

import java.time.Instant;
import java.util.UUID;

import static net.crystalixs.core.persistence.model.UuidReader.uuidReader;

public record TransactionModel(
        long id,
        TransactionType type,
        Currency currency,
        long amount,
        UUID fromPlayerId,
        UUID toPlayerId,
        UUID actorPlayerId,
        String reason,
        Instant createdAt) {

    public static RowMapping<TransactionModel> map() {
        return row -> new TransactionModel(
                row.getLong("id"),
                TransactionType.valueOf(row.getString("type")),
                Currency.valueOf(row.getString("currency")),
                row.getLong("amount"),
                row.get("from_player_id", uuidReader()),
                row.get("to_player_id", uuidReader()),
                row.get("actor_player_id", uuidReader()),
                row.getString("reason"),
                row.getTimestamp("created_at").toInstant());
    }

}
