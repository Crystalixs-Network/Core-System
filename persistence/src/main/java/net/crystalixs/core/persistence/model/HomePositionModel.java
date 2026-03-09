package net.crystalixs.core.persistence.model;

import de.chojo.sadu.mapper.wrapper.Row;

import java.sql.SQLException;

public record HomePositionModel(
        String worldName,
        double x, double y, double z,
        float yaw, float pitch
) {
    public static HomePositionModel fromRow(Row row) throws SQLException {
        return new HomePositionModel(
                row.getString("world_name"),
                row.getDouble("x"),
                row.getDouble("y"),
                row.getDouble("z"),
                row.getFloat("yaw"),
                row.getFloat("pitch")
        );
    }
}
