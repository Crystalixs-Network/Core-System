package net.crystalixs.core.common.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

public interface ConfigService<T> {

    T get();

    void update(UnaryOperator<T> updater) throws IOException;

    void reload() throws IOException;

    void save() throws IOException;

    Path file();

}
