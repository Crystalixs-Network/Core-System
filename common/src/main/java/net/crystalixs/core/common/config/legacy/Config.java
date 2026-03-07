package net.crystalixs.core.common.config.legacy;

import java.io.IOException;

@Deprecated(forRemoval = true)
public interface Config<T> {

    T get();

    void save() throws IOException;

    void reload() throws IOException;

}
