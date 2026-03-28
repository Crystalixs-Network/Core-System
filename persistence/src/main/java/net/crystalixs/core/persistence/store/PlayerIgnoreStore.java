package net.crystalixs.core.persistence.store;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerIgnoreStore {

    boolean exists(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

    void create(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

    void delete(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

}
