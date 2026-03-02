package net.crystalixs.core.common.config;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigLoader<T> implements Config<T> {

    private final ObjectMapper mapper = ObjectMapperProvider.mapper();
    private final AtomicReference<T> config = new AtomicReference<>();
    private final Path file;
    private final String resource;
    private final Class<T> type;

    public ConfigLoader(Path file, String resource, Class<T> type) {
        this.file = file;
        this.resource = resource;
        this.type = type;
    }

    @Override
    public T get() {
        return config.get();
    }

    @Override
    public synchronized void reload() throws IOException {
        JsonNode defaultNode;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resource);
            }
            defaultNode = mapper.readTree(inputStream);
        }

        // Config erstellen sofern nicht vorhanden
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), defaultNode);
        }

        JsonNode userNode = mapper.readTree(file.toFile());
        JsonNode merged = merge(defaultNode, userNode);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), merged);

        // Umwandlung in plattformspezifische Implementierung
        T newConfig = mapper.treeToValue(merged, type);
        config.set(newConfig);
    }

    private JsonNode merge(JsonNode defaultNode, JsonNode userNode) {
        if (!defaultNode.isObject()) {
            return userNode != null ? userNode : defaultNode;
        }

        ObjectNode defaultObj = defaultNode.asObject();
        ObjectNode node = mapper.createObjectNode();

        // Default Felder mergen
        defaultObj.propertyNames().forEach(field -> {
            JsonNode defaultChild = defaultObj.get(field);
            JsonNode child = (userNode != null && userNode.has(field)) ? userNode.get(field) : null;

            if (defaultChild.isObject())
                node.set(field, merge(defaultChild, child));
            else
                node.set(field, child != null ? child : defaultChild);
        });

        // Felder die nur in der verwendeten Config existieren hinzufügen
        if (userNode != null && userNode.isObject()) {
            ObjectNode obj = userNode.asObject();

            obj.propertyNames().forEach(field -> {
                if (!defaultNode.has(field)) node.set(field, obj.get(field));
            });
        }

        return node;
    }
}
