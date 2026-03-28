package net.crystalixs.core.paper.ignore;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface PlayerIgnoreService {

    @UnmodifiableView
    @NotNull Collection<String> ignoredPlayerNames(Player actor);

    void ignorePlayer(Player actor, Player target);

    void unignorePlayer(Player actor, Player target);

    boolean unignorePlayer(Player actor, String targetName);

    boolean isIgnoring(Player actor, Player target);

}
