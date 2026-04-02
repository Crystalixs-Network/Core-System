package net.crystalixs.core.paper.scoreboard;

import org.bukkit.entity.Player;

public interface ScoreboardPlaceholderResolver<T> {

    T resolve(Player player);

}
