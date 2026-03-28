package net.crystalixs.core.paper.ignore;

import org.bukkit.entity.Player;

public interface PlayerIgnoreService {

    void ignorePlayer(Player actor, Player target);

    void unignorePlayer(Player actor, Player target);

    boolean isIgnoring(Player actor, Player target);

}
