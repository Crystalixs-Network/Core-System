package net.crystalixs.core.common.config;

import java.io.IOException;
import java.util.function.UnaryOperator;

public final class ConfigFacade<T> {

    private final ConfigService<T> service;

    public ConfigFacade(ConfigService<T> service) {
        this.service = service;
    }

    public T current() {
        return service.get();
    }

    public void update(UnaryOperator<T> updater) throws IOException {
        service.update(updater);
    }

    public void reload() throws IOException {
        service.reload();
    }

    public void save() throws IOException {
        service.save();
    }
}
