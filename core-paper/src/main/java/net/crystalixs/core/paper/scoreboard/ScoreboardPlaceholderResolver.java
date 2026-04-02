package net.crystalixs.core.paper.scoreboard;

import net.kyori.adventure.text.Component;

public interface ScoreboardPlaceholderResolver<T> {

    Component resolve(T t);

}
