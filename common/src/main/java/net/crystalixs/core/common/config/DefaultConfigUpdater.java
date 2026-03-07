package net.crystalixs.core.common.config;

import java.io.IOException;
import java.util.function.UnaryOperator;

final class DefaultConfigUpdater<T> implements ConfigUpdater<T> {

    private final ConfigService<T> configService;

    DefaultConfigUpdater(ConfigService<T> configService) {
        this.configService = configService;
    }

    @Override
    public T current() {
        return configService.get();
    }

    @Override
    public void update(UnaryOperator<T> updater) throws IOException {
        configService.update(updater);
    }
}
