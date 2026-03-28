package net.crystalixs.core.persistence.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.persistence.PersistenceException;
import net.crystalixs.core.persistence.store.PlayerIgnoreStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;
import static net.crystalixs.core.persistence.model.UuidReader.uuidReader;

public final class DefaultPlayerIgnoreStore implements PlayerIgnoreStore {

    private final QueryConfiguration config;
    private final StructuredLogger logger;

    public DefaultPlayerIgnoreStore(@NotNull StructuredLogger logger, @NotNull DataSource dataSource) {
        this.logger = logger;
        this.config = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<UUID> findAllByPlayer(@NotNull UUID playerUuid) {
        try {
            return config.query("SELECT player_ignore.ignored_uuid FROM player_ignore WHERE player_uuid = ?;")
                    .single(call().bind(playerUuid.toString()))
                    .map(row -> row.get("ignored_uuid", uuidReader()))
                    .all();
        } catch (RuntimeException exception) {
            throw failure("persistence.player_ignore.find_all_failed", "Could not load ignored players", exception, playerUuid);
        }
    }

    @Override
    public boolean exists(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid) {
        try {
            return config.query("SELECT 1 FROM player_ignore WHERE player_uuid = ? AND ignored_uuid = ? LIMIT 1;")
                    .single(call()
                            .bind(playerUuid.toString())
                            .bind(ignoredUuid.toString()))
                    .map(row -> true)
                    .first().orElse(false);
        } catch (RuntimeException exception) {
            throw failure("persistence.player_ignore.exists_failed", "Could not check ignored player relation", exception, playerUuid);
        }
    }

    @Override
    public void create(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid) {
        try {
            config.query("INSERT INTO player_ignore (player_uuid, ignored_uuid) VALUES (?, ?);")
                    .single(call()
                            .bind(playerUuid.toString())
                            .bind(ignoredUuid.toString()))
                    .insert();
        } catch (RuntimeException exception) {
            throw failure("persistence.player_ignore.create_failed", "Could not create ignored player", exception, playerUuid);
        }
    }

    @Override
    public void delete(@NotNull UUID playerUuid, @NotNull UUID ignoredUuid) {
        try {
            config.query("DELETE FROM player_ignore WHERE player_uuid = ? AND ignored_uuid = ?;")
                    .single(call()
                            .bind(playerUuid.toString())
                            .bind(ignoredUuid.toString()))
                    .delete();
        } catch (RuntimeException exception) {
            throw failure("persistence.player_ignore.delete_failed", "Could not delete ignored player", exception, playerUuid);
        }
    }

    private PersistenceException failure(String event, String message, RuntimeException exception, UUID playerUuid) {
        logger.warn(event, LogMetadata.event(event).and(LogMetadata.Key.SUBJECT, "player:" + playerUuid));
        throw new PersistenceException(message, exception);
    }
}
