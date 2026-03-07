package net.crystalixs.core.common.config;

import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public final class JacksonConfigurateConfigService<T> implements ConfigService<T> {

    private final AtomicReference<T> current = new AtomicReference<>();

    private final ConfigDefinition<T> definition;
    private final ConfigMergeService mergeService;
    private final ConfigChangeLogger changeLogger;

    private final ObjectMapper<T> mapper;
    private final ConfigurationOptions options;
    private final JacksonConfigurationLoader loader;

    JacksonConfigurateConfigService(ConfigDefinition<T> definition, ConfigMergeService mergeService, ConfigChangeLogger changeLogger) throws SerializationException {
        this.definition = definition;
        this.mergeService = mergeService;
        this.changeLogger = changeLogger;

        TypeSerializerCollection.Builder serializerBuilder = TypeSerializerCollection.builder();
        definition.extensionProvider().configureSerializers(serializerBuilder);

        ObjectMapper.Factory.Builder builder = ObjectMapper.factoryBuilder();
        definition.extensionProvider().configureMapper(builder);

        ObjectMapper.Factory factory = builder.build();
        serializerBuilder.registerAnnotatedObjects(factory);

        this.mapper = factory.get(definition.type());
        this.options = ConfigurationOptions.defaults().serializers(serializerBuilder.build());
        this.loader = JacksonConfigurationLoader.builder()
                .path(definition.file())
                .defaultOptions(options)
                .build();
    }

    @Override
    public T get() {
        T value = current.get();
        if (value == null) {
            throw new IllegalStateException("Config has not been loaded yet.");
        }
        return value;
    }

    @Override
    public synchronized void reload() throws IOException {
        BasicConfigurationNode defaults = defaults();
        BasicConfigurationNode effective;

        if (Files.notExists(file())) {
            createParentDirectories();
            effective = defaults.copy();
            loader.save(effective);
            definition.logger().info("Config file does not exist, creating new one. Wrote defaults to: " + file());
        } else {
            effective = loader.load();
            ConfigChangeSet changeSet = mergeService.merge(defaults, effective);

            if (changeSet.hasChanges()) {
                loader.save(effective);
                changeLogger.log(definition.logger(), file().toString(), changeSet);
            }
        }
        try {
            current.set(mapper.load(effective));
        } catch (SerializationException exception) {
            throw new IOException("Failed to deserialize config " + file(), exception);
        }
    }

    @Override
    public void save() throws IOException {
        T value = get();
        createParentDirectories();

        BasicConfigurationNode target = BasicConfigurationNode.root(options);
        try {
            mapper.save(value, target);
        } catch (SerializationException exception) {
            throw new IOException("Failed to serialize config " + file(), exception);
        }

        BasicConfigurationNode defaults = defaults();
        ConfigChangeSet changeSet = mergeService.merge(defaults, target);
        loader.save(target);

        if (changeSet.hasChanges()) {
            changeLogger.log(definition.logger(), file().toString(), changeSet);
        }
    }

    @Override
    public Path file() {
        return definition.file();
    }

    private BasicConfigurationNode defaults() throws IOException {
        return JacksonConfigurationLoader.builder()
                .source(this::defaultReader)
                .defaultOptions(options)
                .build().load();
    }

    private BufferedReader defaultReader() throws IOException {
        try (InputStream stream = definition.resourceClassLoader().getResourceAsStream(definition.defaultResource())) {
            if (stream == null) {
                throw new IOException("Cannot find default resource: " + definition.defaultResource());
            }
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }
    }

    private void createParentDirectories() throws IOException {
        if (file().getParent() != null) {
            Files.createDirectories(file().getParent());
        }
    }
}
