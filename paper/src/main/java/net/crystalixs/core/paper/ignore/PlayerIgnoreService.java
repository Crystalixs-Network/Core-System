package net.crystalixs.core.paper.ignore;

import java.util.UUID;

public interface PlayerIgnoreService {

    void ignorePlayer(UUID actor, UUID target);

    boolean isIgnoredByPlayer(UUID actor, UUID target);

}
