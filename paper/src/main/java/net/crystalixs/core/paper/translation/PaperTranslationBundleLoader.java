package net.crystalixs.core.paper.translation;

import net.crystalixs.core.common.translation.AbstractTranslationBundleLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class PaperTranslationBundleLoader extends AbstractTranslationBundleLoader {

    private final JavaPlugin plugin;

    public PaperTranslationBundleLoader(JavaPlugin plugin) {
        this.plugin = plugin;
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
    protected Logger logger() {
        return plugin.getLogger();
    }
}
