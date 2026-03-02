package net.crystalixs.core.common.config;

import java.io.IOException;

public interface Config<T> {

    String FILE_NAME = "config.json";

    T get();

    void reload() throws IOException;

}
