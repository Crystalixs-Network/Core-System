package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.logging.LogMetadata;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperPluginBootstrap {

    private final PaperPluginRuntime runtime;
    private final PaperTranslationBootstrap translations;
    private final PaperCommandBootstrap commands;

    private PaperPluginBootstrap(PaperPluginRuntime runtime, PaperTranslationBootstrap translations, PaperCommandBootstrap commands) {
        this.runtime = runtime;
        this.translations = translations;
        this.commands = commands;
    }

    public static PaperPluginBootstrap create(JavaPlugin plugin) {
        PaperPluginRuntime runtime = PaperPluginRuntime.create(plugin);
        PaperTranslationBootstrap translations = PaperTranslationBootstrap.create(runtime);
        PaperCommandBootstrap commands = new PaperCommandBootstrap(runtime);

        return new PaperPluginBootstrap(runtime, translations, commands);
    }

    public void enable() {
        commands.registerCommands();
        runtime.logger().info("plugin enabled", LogMetadata.event("plugin.enabled"));
    }

    public void disable() {
        try {
            translations.close();
        } finally {
            try {
                runtime.logger().info("plugin disabled", LogMetadata.event("plugin.disabled"));
            } finally {
                runtime.close();
            }
        }
    }

    public PaperPluginRuntime runtime() {
        return runtime;
    }
}
