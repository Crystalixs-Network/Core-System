package net.crystalixs.core.common.config;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigService<T> {

    T get();

    void reload() throws IOException;

    void save() throws IOException;

    Path file();

}
