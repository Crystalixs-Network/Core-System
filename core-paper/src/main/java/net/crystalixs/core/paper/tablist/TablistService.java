package net.crystalixs.core.paper.tablist;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.kyori.adventure.text.Component;
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

import static net.kyori.adventure.text.Component.empty;

public final class TablistService {

    private final LuckPerms luckPerms;
    private final PlayerAdapter<Player> adapter;
    private final StructuredLogger logger;
    private final JavaPlugin plugin;
    private EventSubscription<UserDataRecalculateEvent> subscription;

    private TablistService(LuckPerms luckPerms, StructuredLogger logger, JavaPlugin plugin) {
        this.luckPerms = luckPerms;
        this.logger = logger;
        this.plugin = plugin;
        this.adapter = luckPerms.getPlayerAdapter(Player.class);
    }

    public static TablistService create(JavaPlugin plugin, StructuredLogger logger) {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            logger.warn("LuckPerms is required for rank based tablist sorting", LogMetadata.event("tablist.missing_dependency"));
            return null;
        }
        return new TablistService(LuckPermsProvider.get(), logger, plugin);
    }

    public void refresh(Player player) {
        Component displayName = resolvePrefix(player).append(player.name());
        QueryOptions options = adapter.getQueryOptions(player);
        int weight = resolveWeight(player, options);

        player.setPlayerListOrder(weight);
        player.playerListName(displayName);
        player.displayName(displayName);
    }

    public void refreshAll() {
        Bukkit.getOnlinePlayers().forEach(this::refresh);
    }

    public void subscribe() {
        if (subscription != null) return;

        subscription = luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class, event -> {
            Bukkit.getScheduler().runTask(plugin, () -> refresh(event.getUser()));
            logger.info("Subscribed to LuckPerms UserDataRecalculateEvent", LogMetadata.event("tablist.luckperms_event_subscription"));
        });
    }

    private void refresh(User user) {
        Player player = Bukkit.getPlayer(user.getUniqueId());
        if (player != null && player.isOnline()) {
            refresh(player);
        }
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
