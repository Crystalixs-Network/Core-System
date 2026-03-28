package net.crystalixs.core.persistence.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface PlayerIgnoreStore {

    @UnmodifiableView
    @NotNull Collection<UUID> findAllByPlayer(@NotNull UUID playerUuid);

    boolean exists(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

    void create(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

    void delete(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

}
