package net.crystalixs.core.common.config;

import net.crystalixs.core.common.logging.StructuredLogger;

import java.nio.file.Path;

public record ConfigDefinition<T>(
        Path file,
        String defaultResource,
        Class<T> type,
        StructuredLogger logger,
        ConfigurateExtensionProvider extensionProvider,
        ClassLoader resourceClassLoader
) {

    public ConfigDefinition {
        if (resourceClassLoader == null) {
            resourceClassLoader = Thread.currentThread().getContextClassLoader();
        }
    }

}
