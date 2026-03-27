package net.crystalixs.core.paper.setting;

import net.crystalixs.core.persistence.model.PlayerSettingModel;
import net.crystalixs.core.persistence.store.PlayerSettingStore;

import java.util.UUID;

public final class DefaultSettingService implements PlayerSettingService {

    private final PlayerSettingStore store;

    public DefaultSettingService(PlayerSettingStore store) {
        this.store = store;
    }

    @Override
    public boolean isIgnored(UUID playerId) {
        return getSettingOrFail(playerId).isIgnored();
    }

    @Override
    public boolean isVanished(UUID playerId) {
        return getSettingOrFail(playerId).isVanished();
    }

    @Override
    public void updateIgnoreSetting(UUID playerId, boolean isIgnored) {
        getSettingOrFail(playerId);
        store.updateIgnoreFlag(playerId, isIgnored);
    }

    @Override
    public void updateVanishSetting(UUID playerId, boolean isVanished) {
        getSettingOrFail(playerId);
        store.updateVanishFlag(playerId, isVanished);
    }

    private PlayerSettingModel getSettingOrFail(UUID playerId) {
        return store.findByPlayerId(playerId).orElseThrow(() -> new SettingException(SettingError.PLAYER_NOT_FOUND, "Player not found for player " + playerId));
    }
}
