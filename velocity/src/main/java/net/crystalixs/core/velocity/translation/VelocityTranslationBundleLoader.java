package net.crystalixs.core.velocity.translation;

import net.crystalixs.core.common.translation.AbstractTranslationBundleLoader;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class VelocityTranslationBundleLoader extends AbstractTranslationBundleLoader {

    private final Path dataDirectory;
    private final Logger logger;

    private VelocityTranslationBundleLoader(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    public static Builder builder() {
        return new Builder();
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
    protected Logger logger() {
        return logger;
    }

    public static final class Builder {
        private Path dataDirectory;
        private Logger logger;

        public Builder dataDirectory(Path dataDirectory) {
            this.dataDirectory = dataDirectory;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public VelocityTranslationBundleLoader build() {
            Logger effectiveLogger = logger == null ? Logger.getLogger(VelocityTranslationBundleLoader.class.getSimpleName()) : logger;
            return new VelocityTranslationBundleLoader(dataDirectory, effectiveLogger);
        }
    }
}
