package net.crystalixs.core.paper.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface FloodFill<T> extends Matcher<T> {

    default Set<T> fill(T origin, int cap) {
        Set<T> visited = new HashSet<>();
        if (origin == null || cap <= 0) return visited;

        ArrayDeque<T> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && visited.size() < cap) {
            T current = queue.poll();

            for (T neighbor : neighbors(current)) {
                if (!matches(neighbor)) continue;
                if (visited.add(neighbor)) queue.add(neighbor);
                if (visited.size() >= cap) break;
            }
        }

        return visited;
    }

    Collection<T> neighbors(T current);

}
