package net.crystalixs.core.common.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.module.kotlin.KotlinModule;

import java.util.function.Consumer;

import static tools.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

public final class ObjectMapperFactory {

    private ObjectMapperFactory() {
    }

    public static ObjectMapper create(Consumer<JsonMapper.Builder> customizer) {
        JsonMapper.Builder builder = JsonMapper.builder()
                .addModule(new KotlinModule.Builder().build())
                .propertyNamingStrategy(SNAKE_CASE)
                .enable(INDENT_OUTPUT);

        if (customizer != null) {
            customizer.accept(builder);
        }

        return builder.build();
    }

    public static ObjectMapper createDefault() {
        return create(null);
    }

}
