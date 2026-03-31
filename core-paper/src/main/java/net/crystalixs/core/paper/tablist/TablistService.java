package net.crystalixs.core.paper.tablist;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;

public final class TablistService {

    private final StructuredLogger logger;
    private final PlayerAdapter<Player> adapter;

    private TablistService(StructuredLogger logger, LuckPerms luckPerms) {
        this.logger = logger;
        this.adapter = luckPerms.getPlayerAdapter(Player.class);
    }

    public static TablistService create(CorePlugin plugin, StructuredLogger logger) {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            logger.warn("LuckPerms is required for rank based tablist sorting", LogMetadata.event("tablist.missing_dependency"));
            return null;
        }
        return new TablistService(logger, LuckPermsProvider.get());
    }

    private int resolveWeight(Player player, QueryOptions options) {
        return adapter.getUser(player).getInheritedGroups(options).stream()
                .mapToInt(group -> group.getWeight().orElse(0))
                .max().orElse(0);
    }

    private String teamName(Player player, int weight) {
        int boundedWeight = Math.clamp(weight, 0, 9999);
        int sortKey = 9999 - boundedWeight;
        return "lp-" + sortKey;
    }
}
