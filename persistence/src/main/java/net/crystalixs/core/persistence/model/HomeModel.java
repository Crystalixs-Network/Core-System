package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.rowmapper.RowMapping;

import java.time.Instant;
import java.util.UUID;

import static net.crystalixs.core.persistence.model.UuidReader.uuidReader;

public record HomeModel(
        long id,
        UUID playerUuid,
        String name,
        String icon,
        HomePositionModel position,
        Instant createdAt
) {

    public static RowMapping<HomeModel> map() {
        return row -> new HomeModel(
                row.getLong("id"),
                row.get("player_uuid", uuidReader()),
                row.getString("name"),
                row.getString("icon"),
                HomePositionModel.fromRow(row),
                row.getTimestamp("created_at").toInstant());
    }
}
