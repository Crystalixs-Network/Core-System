package net.crystalixs.core.persistence.internal.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.persistence.model.PlayerModel;
import net.crystalixs.core.persistence.store.PlayerStore;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class DefaultPlayerStore implements PlayerStore {

    private final QueryConfiguration config;

    public DefaultPlayerStore(DataSource dataSource) {
        this.config = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
    }

    @Override
    public Optional<PlayerModel> findById(UUID playerId) {
        return config.query("SELECT * FROM player WHERE uuid = ?;")
                .single(call().bind(playerId.toString()))
                .map(PlayerModel.map())
                .first();
    }

    @Override
    public boolean exists(UUID playerId) {
        return findById(playerId).isPresent();
    }

    @Override
    public void create(PlayerModel model) {
        config.query("INSERT INTO player (uuid, playtime, coins, gems) VALUES (?, ?, ?, ?);")
                .single(call()
                        .bind(model.uuid().toString())
                        .bind(model.playtime())
                        .bind(model.coins())
                        .bind(model.gems()))
                .insert();
    }

    @Override
    public void update(PlayerModel model) {
        config.query("UPDATE player SET playtime = ?, coins = ?, gems = ? WHERE uuid = ?;")
                .single(call()
                        .bind(model.playtime())
                        .bind(model.coins())
                        .bind(model.gems())
                        .bind(model.uuid().toString()))
                .update();
    }

    @Override
    public boolean delete(UUID playerId) {
        return config.query("DELETE FROM player WHERE uuid = ?")
                .single(call().bind(playerId.toString()))
                .delete()
                .changed();
    }
}
