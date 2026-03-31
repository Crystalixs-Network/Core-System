package net.crystalixs.core.paper.translation;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.translation.AbstractTranslationBundleLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.nio.file.Path;

public final class PaperTranslationBundleLoader extends AbstractTranslationBundleLoader {

    private final JavaPlugin plugin;
    private final StructuredLogger logger;

    public PaperTranslationBundleLoader(JavaPlugin plugin, StructuredLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    protected Path resolveDataDirectory() {
        return plugin.getDataPath();
    }

    @Override
    protected InputStream openResource(String fileName) {
        return plugin.getResource("lang/" + fileName);
    }

    @Override
    protected StructuredLogger logger() {
        return logger;
    }
}
