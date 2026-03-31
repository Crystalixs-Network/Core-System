package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.rowmapper.RowMapping;

import java.util.UUID;

import static net.crystalixs.core.persistence.model.UuidReader.uuidReader;

public record PlayerSettingModel(
        long id,
        UUID playerUuid,
        boolean isVanished
) {

    public static RowMapping<PlayerSettingModel> map() {
        return row -> new PlayerSettingModel(
                row.getLong("id"),
                row.get("player_uuid", uuidReader()),
                row.getBoolean("is_vanished"));
    }
}
