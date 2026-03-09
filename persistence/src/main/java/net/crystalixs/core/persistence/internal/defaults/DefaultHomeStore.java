package net.crystalixs.core.persistence.internal.defaults;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import net.crystalixs.core.persistence.model.HomeModel;
import net.crystalixs.core.persistence.store.HomeStore;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;

public final class DefaultHomeStore implements HomeStore {

    private final QueryConfiguration config;

    public DefaultHomeStore(DataSource dataSource) {
        this.config = QueryConfiguration.builder(dataSource)
                .setThrowExceptions(true)
                .build();
    }

    @Override
    public Optional<HomeModel> findById(long homeId) {
        return config.query("SELECT * FROM homes WHERE id = ?;")
                .single(call().bind(homeId))
                .map(HomeModel.map())
                .first();
    }

    @Override
    public Optional<HomeModel> findByPlayerAndName(UUID playerId, String name) {
        return config.query("SELECT * FROM homes WHERE player_id = ? AND name = ?;")
                .single(call()
                        .bind(playerId.toString())
                        .bind(name))
                .map(HomeModel.map())
                .first();
    }

    @Override
    public List<HomeModel> findByPlayerId(UUID playerId) {
        return config.query("SELECT * FROM homes WHERE player_id = ? ORDER BY name;")
                .single(call().bind(playerId.toString()))
                .map(HomeModel.map())
                .all();
    }

    @Override
    public HomeModel create(HomeModel model) {
        return findById(insertHome(model)).orElseThrow();
    }

    @Override
    public void update(HomeModel model) {
        config.query("UPDATE homes SET player_id = ?, name = ?, world_name = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE id = ?;")
                .single(call()
                        .bind(model.playerId().toString())
                        .bind(model.name())
                        .bind(model.position().worldName())
                        .bind(model.position().x())
                        .bind(model.position().y())
                        .bind(model.position().z())
                        .bind(model.position().yaw())
                        .bind(model.position().pitch())
                        .bind(model.id()))
                .update();
    }

    @Override
    public boolean deleteById(long homeId) {
        return config.query("DELETE FROM homes WHERE id = ?;")
                .single(call().bind(homeId))
                .delete()
                .changed();
    }

    @Override
    public boolean deleteByPlayerAndName(UUID playerId, String name) {
        return config.query("DELETE FROM homes WHERE player_id = ? AND name = ?;")
                .single(call()
                        .bind(playerId.toString())
                        .bind(name))
                .delete()
                .changed();
    }

    private long insertHome(HomeModel model) {
        return config.query("INSERT INTO homes (player_id, name, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?);")
                .single(call()
                        .bind(model.playerId().toString())
                        .bind(model.name())
                        .bind(model.position().worldName())
                        .bind(model.position().x())
                        .bind(model.position().y())
                        .bind(model.position().z())
                        .bind(model.position().yaw())
                        .bind(model.position().pitch()))
                .insertAndGetKeys()
                .keys()
                .stream()
                .findFirst().orElseThrow();
    }
}
