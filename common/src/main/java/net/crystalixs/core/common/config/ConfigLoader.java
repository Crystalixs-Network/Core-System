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

    private final AtomicReference<T> config = new AtomicReference<>();
    private final ObjectMapper mapper;
    private final Path file;
    private final String defaultResource;
    private final Class<T> type;
    private final ClassLoader resourceClassLoader;

    public ConfigLoader(@NotNull ObjectMapper mapper, @NotNull Path file, @NotNull String defaultResource, @NotNull Class<T> type) {
        this(mapper, file, defaultResource, type, ConfigLoader.class.getClassLoader());
    }

    public ConfigLoader(@NotNull ObjectMapper mapper, @NotNull Path file, @NotNull String defaultResource, @NotNull Class<T> type, @NotNull ClassLoader resourceClassLoader) {
        this.mapper = mapper;
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
    public synchronized void save() throws IOException {
        T current = get();
        if (current == null) {
            throw new IllegalStateException("Config is not loaded, cannot save.");
        }

        Files.createDirectories(file.getParent());

        JsonNode node = mapper.valueToTree(current);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), node);
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
        JsonNode merged = JsonMerger.merge(mapper, defaultNode, userNode);
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
    public static final class JsonMerger {
        public static JsonNode merge(@NotNull ObjectMapper mapper, @NotNull JsonNode defaultNode, JsonNode userNode) {

            // Wenn Default kein Objekt ist: User gewinnt, sonst Default
            if (!defaultNode.isObject()) {
                if (userNode != null && !userNode.isMissingNode() && !userNode.isNull()) {
                    return userNode.deepCopy();
                }
                return defaultNode.deepCopy();
            }

            ObjectNode merged = mapper.createObjectNode();
            ObjectNode defaultObject = defaultNode.asObject();
            ObjectNode userObject = (userNode != null && userNode.isObject())
                    ? userNode.asObject()
                    : null;

            // Nur Default-Keys übernehmen -> Extra-User-Keys werden ausgeschlossen
            for (String field : defaultObject.propertyNames()) {
                JsonNode defaultChild = defaultObject.get(field);
                JsonNode userChild = (userObject != null) ? userObject.get(field) : null;

                JsonNode mergedChild;
                if (userChild == null || userChild.isNull() || userChild.isMissingNode())
                    mergedChild = defaultChild.deepCopy(); // Fehlender/null User-Wert -> Default
                else if (defaultChild.isObject() && userChild.isObject())
                    mergedChild = merge(mapper, defaultChild, userChild); // Rekursiver Merge nur bei Objekt/Objekt
                else
                    mergedChild = userChild.deepCopy(); // User-Wert gewinnt

                merged.set(field, mergedChild);
            }

            return merged;
        }
    }
}
