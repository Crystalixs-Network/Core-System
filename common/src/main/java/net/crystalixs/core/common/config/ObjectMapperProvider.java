package net.crystalixs.core.common.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public final class ObjectMapperProvider {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT) // Pretty printing
            .build();


    private ObjectMapperProvider() {
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
