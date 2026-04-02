package net.crystalixs.core.paper.scoreboard;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.economy.EconomyService;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

public final class ScoreboardCoinsPlaceholderResolver implements ScoreboardPlaceholderResolver {

    private final CorePlugin plugin;
    private final EconomyService service;

    public ScoreboardCoinsPlaceholderResolver(CorePlugin plugin, EconomyService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @Override
    public ComponentLike resolve(Player player) {
        Locale locale = plugin.resolveTranslationLocale(player.locale());

        String formatted = NumberFormat.getIntegerInstance(locale).format(service.getCoins(player.getUniqueId()));
        return Argument.component("coins", text(formatted));
    }
}
