package net.crystalixs.core.paper.display;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static net.kyori.adventure.text.Component.empty;

public final class TablistService {

    private final LuckPerms luckPerms;
    private final PlayerAdapter<Player> adapter;
    private final JavaPlugin plugin;
    private EventSubscription<UserDataRecalculateEvent> subscription;

    private TablistService(LuckPerms luckPerms, JavaPlugin plugin) {
        this.luckPerms = luckPerms;
        this.plugin = plugin;
        this.adapter = luckPerms.getPlayerAdapter(Player.class);
    }

    public static TablistService create(JavaPlugin plugin, StructuredLogger logger) {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            logger.warn("LuckPerms is required for rank based tablist sorting", LogMetadata.event("tablist.missing_dependency"));
            return null;
        }
        return new TablistService(LuckPermsProvider.get(), plugin);
    }

    public void refresh(Player player) {
        Component prefix = resolvePrefix(player);
        Component displayName = prefix.append(player.name().color(NamedTextColor.GRAY));
        QueryOptions options = adapter.getQueryOptions(player);
        int weight = resolveWeight(player, options);

        player.setPlayerListOrder(weight);
        player.playerListName(displayName);
        player.displayName(displayName);
        applyOverhead(player, prefix);
    }

    public void refreshAll() {
        Bukkit.getOnlinePlayers().forEach(this::refresh);
    }

    public void subscribe() {
        if (subscription != null) return;

        subscription = luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class, event -> Bukkit.getScheduler().runTask(plugin, () -> refresh(event.getUser())));
    }

    private void refresh(User user) {
        Player player = Bukkit.getPlayer(user.getUniqueId());
        if (player != null && player.isOnline()) {
            refresh(player);
        }
    }

    private void applyOverhead(Player player, Component prefix) {
        String teamName = "core-" + player.getUniqueId().toString().replace("-", "").substring(0, 12);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        team.color(NamedTextColor.GRAY);
        team.addEntry(player.getName());
        team.prefix(prefix);
    }

    private Component resolvePrefix(Player player) {
        String prefix = adapter.getMetaData(player).getPrefix();
        if (prefix == null || prefix.isBlank()) {
            return empty();
        }

        return MiniMessage.miniMessage().deserialize(prefix);
    }

    private int resolveWeight(Player player, QueryOptions options) {
        return adapter.getUser(player).getInheritedGroups(options).stream()
                .mapToInt(group -> group.getWeight().orElse(0))
                .max().orElse(0);
    }
}
