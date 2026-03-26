package net.crystalixs.core.paper.home;

import org.bukkit.entity.Player;

public final class HomeLimitResolver {

    public static final int MIN_LIMIT = 1;
    public static final int MAX_LIMIT = 20;

    private HomeLimitResolver() {
    }

    public static int resolve(Player player) {
        int resolved = MIN_LIMIT;
        for (int i = 0; i <= MAX_LIMIT; i++) {
            if (player.hasPermission("command.home.limit." + i)) {
                resolved = i;
            }
        }

        return resolved;
    }

}
