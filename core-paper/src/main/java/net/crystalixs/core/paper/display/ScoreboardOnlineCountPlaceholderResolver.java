package net.crystalixs.core.paper.display;

import net.crystalixs.core.paper.CorePlugin;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

public final class ScoreboardOnlineCountPlaceholderResolver implements ScoreboardPlaceholderResolver {

    private final CorePlugin plugin;

    public ScoreboardOnlineCountPlaceholderResolver(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ComponentLike resolve(Player player) {
        Locale locale = plugin.resolveTranslationLocale(player.locale());
        String formatted = NumberFormat.getIntegerInstance(locale).format(player.getServer().getOnlinePlayers().size());

        return Argument.component("online", text(formatted));
    }
}
