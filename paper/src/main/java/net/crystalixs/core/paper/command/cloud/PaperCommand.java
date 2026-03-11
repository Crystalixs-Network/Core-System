package net.crystalixs.core.paper.command.cloud;

import net.crystalixs.core.common.command.AbstractCommand;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

public abstract class PaperCommand extends AbstractCommand<PaperCommandSource, CorePlugin> {

    public PaperCommand(CorePlugin plugin) {
        super(plugin);
    }

    protected StructuredLogger commandLogger(String commandName) {
        return plugin.commandLogger(commandName);
    }

    protected ComponentLike number(String key, long value, CommandSender sender) {
        Locale locale = sender instanceof Player player
                ? plugin.resolveTranslationLocale(player.locale())
                : plugin.defaultTranslationLocale();

        String formatted = NumberFormat.getIntegerInstance(locale).format(value);
        return Argument.component(key, text(formatted));
    }
}
