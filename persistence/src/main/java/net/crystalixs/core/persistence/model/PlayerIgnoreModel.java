package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.rowmapper.RowMapping;

import java.time.Instant;
import java.util.UUID;

import static net.crystalixs.core.persistence.model.UuidReader.uuidReader;

public record PlayerIgnoreModel(
        long id,
        UUID playerUuid,
        UUID ignoredUuid,
        Instant createdAt
) {

    public static RowMapping<PlayerIgnoreModel> map() {
        return row -> new PlayerIgnoreModel(
                row.getLong("id"),
                row.get("player_uuid", uuidReader()),
                row.get("ignored_uuid", uuidReader()),
                row.getTimestamp("created_at").toInstant());
    }

}
