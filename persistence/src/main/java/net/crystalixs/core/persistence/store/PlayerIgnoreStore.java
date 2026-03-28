package net.crystalixs.core.persistence.store;

import net.crystalixs.core.persistence.model.PlayerIgnoreModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface PlayerIgnoreStore {

    @NotNull
    @UnmodifiableView
    Collection<PlayerIgnoreModel> findByPlayer(@NotNull UUID playerUuid);

    boolean exists(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

    void create(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid);

}
