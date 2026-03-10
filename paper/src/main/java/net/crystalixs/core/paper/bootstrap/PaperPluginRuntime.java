package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.bootstrap.AbstractPluginRuntime;
import net.crystalixs.core.common.logging.LogFactory;
import net.crystalixs.core.common.logging.LogManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.translatable;

public final class PaperPluginRuntime extends AbstractPluginRuntime {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .editTags(builder -> builder.tag("prefix", Tag.inserting(translatable("prefix"))))
            .build();

    private final JavaPlugin plugin;

    private PaperPluginRuntime(JavaPlugin plugin, LogFactory logFactory) {
        super(logFactory);
        this.plugin = plugin;
    }

    public static PaperPluginRuntime create(JavaPlugin plugin) {
        LogFactory factory = LogManager.createForJavaUtil(plugin.getLogger(), plugin.getDataPath().resolve("logs"));
        return new PaperPluginRuntime(plugin, factory);
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }
}
