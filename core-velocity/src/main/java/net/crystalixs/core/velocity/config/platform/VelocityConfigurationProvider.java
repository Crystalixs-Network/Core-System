package net.crystalixs.core.velocity.config.platform;

import net.crystalixs.core.common.config.ConfigurateExtensionProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.NamingSchemes;

public final class VelocityConfigurationProvider implements ConfigurateExtensionProvider {

    private final MiniMessage miniMessage;

    public VelocityConfigurationProvider(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public void configureSerializers(TypeSerializerCollection.Builder builder) {
        builder.register(Component.class, new ComponentSerializer(miniMessage));
    }

    @Override
    public void configureMapper(ObjectMapper.Factory.Builder builder) {
        builder.defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED);
    }
}
