package net.crystalixs.core.persistence.model;

import java.util.UUID;

public record PlayerModel(
        UUID uuid,
        long playtime,
        long coins, long gems
) {
}
