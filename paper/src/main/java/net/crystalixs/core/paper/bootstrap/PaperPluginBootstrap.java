package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.bootstrap.AbstractPluginBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperPluginBootstrap extends AbstractPluginBootstrap<PaperPluginRuntime> {

    private final PaperTranslationBootstrap translations;
    private final PaperCommandBootstrap commands;

    private PaperPluginBootstrap(PaperPluginRuntime runtime, PaperTranslationBootstrap translations, PaperCommandBootstrap commands) {
        super(runtime);
        this.translations = translations;
        this.commands = commands;
    }

    public static PaperPluginBootstrap create(JavaPlugin plugin) {
        PaperPluginRuntime runtime = PaperPluginRuntime.create(plugin);
        PaperTranslationBootstrap translations = PaperTranslationBootstrap.create(runtime);
        PaperCommandBootstrap commands = new PaperCommandBootstrap(runtime);

        return new PaperPluginBootstrap(runtime, translations, commands);
    }

    @Override
    protected void enableInternal() {
        commands.registerCommands();
    }

    @Override
    protected void disableInternal() {
        translations.close();
    }
}
