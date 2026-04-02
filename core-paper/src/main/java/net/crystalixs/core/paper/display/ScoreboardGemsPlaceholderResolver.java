package net.crystalixs.core.paper.display;

import net.crystalixs.core.paper.CorePlugin;
import net.crystalixs.core.paper.economy.EconomyService;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

public final class ScoreboardGemsPlaceholderResolver implements ScoreboardPlaceholderResolver {

    private final CorePlugin plugin;
    private final EconomyService service;

    public ScoreboardGemsPlaceholderResolver(CorePlugin plugin, EconomyService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @Override
    public ComponentLike resolve(Player player) {
        Locale locale = plugin.resolveTranslationLocale(player.locale());

        String formatted = NumberFormat.getIntegerInstance(locale).format(service.getGems(player.getUniqueId()));
        return Argument.component("gems", text(formatted));
    }
}
