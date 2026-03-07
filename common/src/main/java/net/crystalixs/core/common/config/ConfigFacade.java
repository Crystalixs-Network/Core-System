package net.crystalixs.core.common.config;

import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.concurrent.atomic.AtomicReference;

public final class ConfigFacade<T> {

    private final ConfigService<T> service;
    private final AtomicReference<ConfigFileVersion> knownVersion;

    public ConfigFacade(ConfigService<T> service) throws IOException {
        this.service = service;
        this.knownVersion = new AtomicReference<>(ConfigFileVersion.read(service.file()));
    }

    public T current() {
        return service.get();
    }

    public void update(UnaryOperator<T> updater) throws IOException {
        ensureNoExternalModification();
        service.update(updater);
        refreshKnownVersion();
    }

    public void reload() throws IOException {
        service.reload();
        refreshKnownVersion();
    }

    public void save() throws IOException {
        ensureNoExternalModification();
        service.save();
        refreshKnownVersion();
    }

    private void ensureNoExternalModification() throws IOException {
        ConfigFileVersion currentVersion = ConfigFileVersion.read(service.file());
        ConfigFileVersion expectedVersion = knownVersion.get();

        if (!currentVersion.equals(expectedVersion)) {
            throw new ExternalConfigModificationException(service.file());
        }
    }

    private void refreshKnownVersion() throws IOException {
        knownVersion.set(ConfigFileVersion.read(service.file()));
    }
}
