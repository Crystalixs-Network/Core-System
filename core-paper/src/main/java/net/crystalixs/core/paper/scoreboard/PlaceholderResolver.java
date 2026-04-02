package net.crystalixs.core.paper.scoreboard;

import net.kyori.adventure.text.Component;

public interface PlaceholderResolver<T> {

    Component resolve(T t);

}
