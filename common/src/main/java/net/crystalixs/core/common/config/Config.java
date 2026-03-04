package net.crystalixs.core.common.config;

import java.io.IOException;

public interface Config<T> {

    T get();

    void save() throws IOException;

    void reload() throws IOException;

}
