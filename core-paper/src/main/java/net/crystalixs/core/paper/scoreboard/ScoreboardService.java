package net.crystalixs.core.paper.scoreboard;

import net.crystalixs.celestial.api.Scoreboard;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.config.PaperConfig;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.translation.Argument.component;

public final class ScoreboardService {

    private final Map<UUID, Scoreboard> activeBoards = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> lastRefresh = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final StructuredLogger logger;
    private final PaperConfig config;
    private final LuckPerms luckPerms;
    private final ScoreboardPlaceholderResolver<Component> rankResolver;
    private EventSubscription<UserDataRecalculateEvent> subscription;

    private ScoreboardService(JavaPlugin plugin, StructuredLogger logger, PaperConfig config) {
        this.plugin = plugin;
        this.logger = logger;
        this.config = config;
        this.luckPerms = plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null
                ? null
                : LuckPermsProvider.get();
        this.rankResolver = luckPerms == null
                ? null
                : new ScoreboardRankScoreboardPlaceholderResolver(luckPerms);
    }

    public static ScoreboardService create(JavaPlugin plugin, StructuredLogger logger, PaperConfig config) {
        if (config == null || config.scoreboard() == null) {
            return null;
        }
        return new ScoreboardService(plugin, logger, config);
    }

    public void display(Player player) {
        if (!player.isOnline()) {
            return;
        }

        Component title = resolveTitle();
        List<Component> lines = resolveLines(player);

        Scoreboard scoreboard = activeBoards.get(player.getUniqueId());
        if (scoreboard != null) {
            updateScoreboard(scoreboard, title, lines);
            return;
        }
        createAndDisplay(player, title, lines);
    }

    public void remove(Player player) {
        Scoreboard scoreboard = activeBoards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.destroy();
        }
        lastRefresh.remove(player.getUniqueId());
    }

    public void shutdown() {
        if (subscription != null) {
            subscription.close();
        }
        activeBoards.values().forEach(Scoreboard::destroy);
        activeBoards.clear();
        lastRefresh.clear();
    }

    public void refreshIfActive(UUID uuid) {
        if (uuid == null || !activeBoards.containsKey(uuid)) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> refresh(uuid));
    }

    public void refreshAllActive() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            activeBoards.forEach((uuid, scoreboard) -> {
                Player player = Bukkit.getPlayer(uuid);

                if (player == null || !player.isOnline()) {
                    scoreboard.destroy();
                    activeBoards.remove(uuid);
                    lastRefresh.remove(uuid);
                    return;
                }

                Component title = resolveTitle();
                List<Component> lines = resolveLines(player);
                updateScoreboard(scoreboard, title, lines);
            });
        });
    }

    public void subscribe() {
        if (luckPerms == null || subscription != null) {
            return;
        }

        subscription = luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class,
                event -> {
                    UUID uuid = event.getUser().getUniqueId();
                    if (!activeBoards.containsKey(uuid)) {
                        return;
                    }

                    int currentTick = plugin.getServer().getCurrentTick();
                    Integer previousTick = lastRefresh.put(uuid, currentTick);
                    if (previousTick != null && previousTick == currentTick) {
                        return;
                    }

                    Bukkit.getScheduler().runTask(plugin, () -> refresh(uuid));
                }
        );
    }

    private void refresh(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return;
        }

        Scoreboard scoreboard = activeBoards.get(uuid);
        if (scoreboard == null) {
            return;
        }
        updateScoreboard(scoreboard, resolveTitle(), resolveLines(player));
    }

    private void createAndDisplay(Player player, Component title, List<Component> lines) {
        Scoreboard scoreboard = Scoreboard.sidebar()
                .withPlayer(player)
                .title(title)
                .lines(lines)
                .build();

        scoreboard.display();
        activeBoards.put(player.getUniqueId(), scoreboard);
    }

    private void updateScoreboard(Scoreboard scoreboard, Component title, List<Component> lines) {
        scoreboard.updateTitle(title);
        scoreboard.updateLines(lines);
    }

    private Component resolveTitle() {
        String translationKey = config.scoreboard().title();
        if (translationKey == null || translationKey.isBlank()) {
            return empty();
        }
        return translatable(translationKey);
    }

    private List<Component> resolveLines(Player player) {
        if (config.scoreboard().lines() == null) {
            return Collections.emptyList();
        }
        return config.scoreboard().lines().stream()
                .map(line -> line == null || line.isBlank()
                        ? empty()
                        : translatable(line).arguments(component("rang", resolveRankPlaceholder(player)))
                )
                .collect(Collectors.toList());
    }

    private Component resolveRankPlaceholder(Player player) {
        if (rankResolver == null) {
            return empty();
        }
        return rankResolver.resolve(player);
    }
}
