package net.crystalixs.core.velocity.translation;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.translation.AbstractTranslationBundleLoader;

import java.io.InputStream;
import java.nio.file.Path;

public final class VelocityTranslationBundleLoader extends AbstractTranslationBundleLoader {

    private final Path dataDirectory;
    private final StructuredLogger logger;

    public VelocityTranslationBundleLoader(Path dataDirectory, StructuredLogger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Override
    protected Path resolveDataDirectory() {
        return dataDirectory;
    }

    @Override
    protected InputStream openResource(String fileName) {
        return getClass().getClassLoader().getResourceAsStream("lang/" + fileName);
    }

    @Override
    protected StructuredLogger logger() {
        return logger;
    }
}
