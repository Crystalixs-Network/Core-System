package net.crystalixs.core.paper.setting;

import java.util.UUID;

public interface PlayerSettingService {

    boolean isIgnored(UUID playerId);

    boolean isVanished(UUID playerId);

    void updateIgnoreSetting(UUID playerId, boolean isIgnored);

    void updateVanishSetting(UUID playerId, boolean isVanished);

}
