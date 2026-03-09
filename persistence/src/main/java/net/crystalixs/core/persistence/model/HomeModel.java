package net.crystalixs.core.persistence.model;

import java.time.Instant;
import java.util.UUID;

public record HomeModel(
        long id,
        UUID playerId,
        String name,
        HomePositionModel position,
        Instant createdAt
) {
}
