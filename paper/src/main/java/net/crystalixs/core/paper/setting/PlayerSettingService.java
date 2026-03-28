package net.crystalixs.core.paper.setting;

import java.util.UUID;

public interface PlayerSettingService {

    boolean isVanished(UUID playerId);

    void updateVanishSetting(UUID playerId, boolean isVanished);

}
