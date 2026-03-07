package net.crystalixs.core.common.config;

import net.crystalixs.core.common.logging.ChangeLogger;
import org.spongepowered.configurate.serialize.SerializationException;

public final class ConfigServiceFactory {

    private ConfigServiceFactory() {
    }

    public static <T> ConfigService<T> create(ConfigDefinition<T> definition) throws SerializationException {
        return new JacksonConfigurateConfigService<>(definition, new ConfigMergeService(), new ChangeLogger());
    }

}
