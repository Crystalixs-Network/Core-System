package net.crystalixs.core.paper.tablist;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TablistService {

    private final Map<UUID, String> assignedTeams = new HashMap<>();

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

    public void refresh(Player player) {
        QueryOptions options = adapter.getQueryOptions(player);
        int weight = resolveWeight(player, options);
        assignTeam(player, weight);
    }

    public void remove(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String previousTeamName = assignedTeams.remove(player.getUniqueId());
        if (previousTeamName == null) {
            return;
        }

        Team previousTeam = scoreboard.getTeam(previousTeamName);
        if (previousTeam == null) {
            return;
        }

        previousTeam.removeEntry(player.getName());
        if (previousTeam.getEntries().isEmpty()) {
            previousTeam.unregister();
        }
    }

    private void assignTeam(Player player, int weight) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String entry = player.getName();
        String targetTeamName = teamName(player, weight);
        String previousTeamName = assignedTeams.get(player.getUniqueId());

        if (previousTeamName != null && !previousTeamName.equals(targetTeamName)) {
            Team previousTeam = scoreboard.getTeam(previousTeamName);

            if (previousTeam != null) {
                previousTeam.removeEntry(entry);

                if (previousTeam.getEntries().isEmpty()) {
                    previousTeam.unregister();
                }
            }
        }

        Team targetTeam = scoreboard.getTeam(targetTeamName);
        if (targetTeam == null) {
            targetTeam = scoreboard.registerNewTeam(targetTeamName);
        }
        if (!targetTeam.hasEntry(entry)) {
            targetTeam.addEntry(entry);
        }

        assignedTeams.put(player.getUniqueId(), targetTeamName);
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
