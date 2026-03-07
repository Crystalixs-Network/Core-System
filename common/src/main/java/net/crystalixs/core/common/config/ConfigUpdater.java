package net.crystalixs.core.common.config;

import java.io.IOException;
import java.util.function.UnaryOperator;

public interface ConfigUpdater<T> {

    static <T> ConfigUpdater<T> create(ConfigService<T> configService) {
        return new DefaultConfigUpdater<>(configService);
    }

    T current();

    void update(UnaryOperator<T> updater) throws IOException;

}
