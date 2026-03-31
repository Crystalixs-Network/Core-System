package net.crystalixs.core.paper.ignore;

import net.crystalixs.core.persistence.store.PlayerIgnoreStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public final class DefaultPlayerIgnoreService implements PlayerIgnoreService {

    private final PlayerIgnoreStore store;

    public DefaultPlayerIgnoreService(PlayerIgnoreStore store) {
        this.store = store;
    }

    @Override
    public @UnmodifiableView @NotNull Collection<String> ignoredPlayerNames(Player actor) {
        return store.findAllByPlayer(actor.getUniqueId()).stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .filter(Objects::nonNull)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Override
    public void ignorePlayer(Player actor, Player target) {
        store.create(actor.getUniqueId(), target.getUniqueId());
    }

    @Override
    public void unignorePlayer(Player actor, Player target) {
        store.delete(actor.getUniqueId(), target.getUniqueId());
    }

    @Override
    public boolean unignorePlayer(Player actor, String targetName) {
        var match = store.findAllByPlayer(actor.getUniqueId()).stream()
                .map(uuid -> new IgnoredPlayer(uuid, Bukkit.getOfflinePlayer(uuid).getName()))
                .filter(player -> player.name() != null)
                .filter(player -> player.name().equalsIgnoreCase(targetName))
                .findFirst();

        if (match.isEmpty()) return false;

        store.delete(actor.getUniqueId(), match.get().uuid());
        return true;
    }

    @Override
    public boolean isIgnoring(Player actor, Player target) {
        return store.exists(actor.getUniqueId(), target.getUniqueId());
    }

    private record IgnoredPlayer(UUID uuid, String name) {
    }
}
