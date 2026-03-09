package net.crystalixs.core.persistence.model;

public record HomePositionModel(
        String worldName,
        double x, double y, double z,
        float yaw, float pitch
) {
}
