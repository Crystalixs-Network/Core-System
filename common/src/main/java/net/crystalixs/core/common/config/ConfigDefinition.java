package net.crystalixs.core.common.config;

import java.nio.file.Path;
import java.util.logging.Logger;

public record ConfigDefinition<T>(
        Path file,
        String defaultResource,
        Class<T> type,
        Logger logger,
        ConfigurateExtensionProvider extensionProvider,
        ClassLoader resourceClassLoader
) {

    public ConfigDefinition {
        if (resourceClassLoader == null) {
            resourceClassLoader = Thread.currentThread().getContextClassLoader();
        }
    }

}
