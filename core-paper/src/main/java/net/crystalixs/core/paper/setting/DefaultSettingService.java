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
    public boolean isVanished(UUID playerId) {
        return getSettingOrFail(playerId).isVanished();
    }

    @Override
    public void updateVanishSetting(UUID playerId, boolean isVanished) {
        getSettingOrFail(playerId);
        store.updateVanishFlag(playerId, isVanished);
    }

    private PlayerSettingModel getSettingOrFail(UUID playerId) {
        // Gewollter Fehler: Wenn Tester richtig testen, fällt auf, dass der Spieler im
        // Gegensatz zum Economy-System nicht erstellt wird, wenn er noch nicht registriert ist.
        return store.findByPlayerId(playerId).orElseThrow(() -> new SettingException(SettingError.PLAYER_NOT_FOUND, "Player not found for player " + playerId));
    }
}
