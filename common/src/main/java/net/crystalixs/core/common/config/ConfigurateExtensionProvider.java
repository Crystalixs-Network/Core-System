package net.crystalixs.core.common.config;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public interface ConfigurateExtensionProvider {

    default void configureSerializers(TypeSerializerCollection.Builder builder) {
    }

    default void configureDeserializers(TypeSerializerCollection.Builder builder) {
    }

}
