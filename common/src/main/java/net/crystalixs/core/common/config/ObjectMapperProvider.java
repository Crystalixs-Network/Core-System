package net.crystalixs.core.common.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.module.kotlin.KotlinFeature;
import tools.jackson.module.kotlin.KotlinModule;

public final class ObjectMapperProvider {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new KotlinModule.Builder()
                    .enable(KotlinFeature.KotlinPropertyNameAsImplicitName)
                    .build())
            .enable(SerializationFeature.INDENT_OUTPUT) // Pretty printing
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();

    private ObjectMapperProvider() {
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
