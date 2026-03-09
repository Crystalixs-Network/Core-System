package net.crystalixs.core.paper.config.platform;

import net.crystalixs.core.common.config.ConfigurateExtensionProvider;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.util.NamingSchemes;

public final class PaperConfigurationProvider implements ConfigurateExtensionProvider {

    @Override
    public void configureMapper(ObjectMapper.Factory.Builder builder) {
        builder.defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED);
    }
}
