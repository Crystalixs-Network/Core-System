package net.crystalixs.core.common.config;

import java.nio.file.Path;
import java.util.logging.Logger;

public record Config<T>(
        Path file,
        String defaultResource,
        Class<T> type,
        Logger logger,
        ConfigurateExtensionProvider extensionProvider,
        ClassLoader resourceClassLoader
) {

    public Config {
        if (resourceClassLoader == null) {
            resourceClassLoader = Thread.currentThread().getContextClassLoader();
        }
    }

}
