package net.crystalixs.core.paper.scoreboard;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;

public interface ScoreboardPlaceholderResolver {

    ComponentLike resolve(Player player);

}
