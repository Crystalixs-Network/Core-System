package net.crystalixs.core.common.config;

import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigLoader<T> implements Config<T> {

    private final ObjectMapper mapper = ObjectMapperProvider.mapper();
    private final AtomicReference<T> config = new AtomicReference<>();
    private final Path file;
    private final String defaultResource;
    private final Class<T> type;
    private final ClassLoader resourceClassLoader;

    public ConfigLoader(@NotNull Path file, @NotNull String defaultResource, @NotNull Class<T> type) {
        this(file, defaultResource, type, ConfigLoader.class.getClassLoader());
    }

    public ConfigLoader(@NotNull Path file, @NotNull String defaultResource, @NotNull Class<T> type, @NotNull ClassLoader resourceClassLoader) {
        this.file = file;
        this.defaultResource = defaultResource;
        this.type = type;
        this.resourceClassLoader = resourceClassLoader;
    }

    @Override
    public T get() {
        return config.get();
    }

    @Override
    public synchronized void reload() throws IOException {
        // Defaults aus der Resource laden
        JsonNode defaultNode;
        try (var stream = resourceClassLoader.getResourceAsStream(defaultResource)) {
            if (stream == null) {
                throw new IOException("Resource not found: " + defaultResource);
            }
            defaultNode = mapper.readTree(stream);
        }

        // Config erstellen, wenn nicht vorhanden
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), defaultNode);
        }

        // User-Config laden, mergen und zurückschreiben
        JsonNode userNode = mapper.readTree(file.toFile());
        JsonNode merged = merge(defaultNode, userNode);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), merged);

        // Config (plattformspezifisch) aktualisieren
        T newConfig = mapper.treeToValue(merged, type);
        config.set(newConfig);
    }

    /**
     * Mergen von defaultNode und userNode unterliegen den folgenden Regeln:
     * <ol>
     *     <li>Defaults, die in der userNode fehlen, werden dort hinzugefügt</li>
     *     <li>Bestehende Felder in userNode werden beibehalten</li>
     *     <li>Felder in der userNode, die in defaultNode nicht existieren, werden entfernt</li>
     * </ol>
     */
    private JsonNode merge(@NotNull JsonNode defaultNode, JsonNode userNode) {
        if (!defaultNode.isObject()) {
            return userNode != null ? userNode : defaultNode;
        }

        ObjectNode defaultObject = defaultNode.asObject();
        ObjectNode userObject = (userNode != null && userNode.isObject())
                ? userNode.asObject()
                : mapper.createObjectNode();

        ObjectNode merged = mapper.createObjectNode();

        // 1. Default Felder mergen / hinzufügen
        for (String field : defaultObject.propertyNames()) {
            JsonNode defaultChild = defaultObject.get(field);
            JsonNode userChild = userObject.has(field)
                    ? userObject.get(field)
                    : null;

            if (defaultChild.isObject()) {
                merged.set(field, merge(defaultChild, userChild));
            } else {
                merged.set(field, userChild != null ? userChild : defaultChild);
            }
        }

        // 2. User-Felder, die nicht in den Defaults existieren, werden gelöscht
        return merged;
    }
}
