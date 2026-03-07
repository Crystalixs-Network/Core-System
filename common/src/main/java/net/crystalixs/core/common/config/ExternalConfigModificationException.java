package net.crystalixs.core.common.config;

import java.io.IOException;
import java.nio.file.Path;

public final class ExternalConfigModificationException extends IOException {

    public ExternalConfigModificationException(Path file) {
        super("Config file was modified externally: " + file);
    }
}
