package net.crystalixs.core.paper.economy;

import net.crystalixs.core.persistence.model.Currency;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public final class EconomyMutationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final EconomyMutationType type;
    private final Currency currency;
    private final Set<UUID> affectedPlayers;

    public EconomyMutationEvent(EconomyMutationType type, Currency currency, Set<UUID> affectedPlayers) {
        this.type = type;
        this.currency = currency;
        this.affectedPlayers = Collections.unmodifiableSet(affectedPlayers);
    }

    public EconomyMutationType getType() {
        return type;
    }

    public Currency currency() {
        return currency;
    }

    public Set<UUID> affectedPlayers() {
        return affectedPlayers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
