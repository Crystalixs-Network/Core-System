package net.crystalixs.core.persistence.model;

import java.time.Instant;
import java.util.UUID;

public record PlayerIgnoreModel(
        long id,
        UUID playerUuid,
        UUID ignoredUuid,
        Instant createdAt
) {
}
