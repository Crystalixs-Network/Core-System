package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.rowmapper.RowMapping;

import java.util.UUID;

import static net.crystalixs.core.persistence.model.UuidReader.uuidReader;

public record PlayerModel(
        UUID uuid,
        long playtime,
        long coins, long gems
) {

    public static RowMapping<PlayerModel> map() {
        return row -> new PlayerModel(
                row.get("uuid", uuidReader()),
                row.getLong("playtime"),
                row.getLong("coins"),
                row.getLong("gems"));
    }
}
