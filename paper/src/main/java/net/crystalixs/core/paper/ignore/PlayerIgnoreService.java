package net.crystalixs.core.paper.ignore;

import org.bukkit.entity.Player;

public interface PlayerIgnoreService {

    void ignorePlayer(Player actor, Player target);

    boolean isIgnoredByPlayer(Player actor, Player target);

}
