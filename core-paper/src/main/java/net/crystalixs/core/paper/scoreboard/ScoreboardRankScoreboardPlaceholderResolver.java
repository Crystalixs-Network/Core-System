package net.crystalixs.core.paper.scoreboard;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.empty;

public final class ScoreboardRankScoreboardPlaceholderResolver implements ScoreboardPlaceholderResolver {

    private final LuckPerms luckPerms;

    public ScoreboardRankScoreboardPlaceholderResolver(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public ComponentLike resolve(Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        QueryOptions options = user.getQueryOptions();
        CachedMetaData metaData = user.getCachedData().getMetaData(options);

        String group = metaData.getPrimaryGroup();
        String display = metaData.getMetaValue("displayname");
        String value = (display == null || display.isBlank()) ? group : display;

        if (value == null || value.isBlank()) {
            return empty();
        }
        return Argument.component("rang", MiniMessage.miniMessage().deserialize(value));
    }
}
