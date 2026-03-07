package net.crystalixs.core.common.config;

import net.crystalixs.core.common.logging.ChangeSetLogger;
import net.crystalixs.core.common.logging.ChangeSet;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.logging.LogMetadata;
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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public final class JacksonConfigurateConfigService<T> implements ConfigService<T> {

    private final AtomicReference<T> current = new AtomicReference<>();

    private final ConfigDefinition<T> definition;
    private final ConfigMergeService mergeService;
    private final ChangeSetLogger changeSetLogger;
    private final StructuredLogger logger;

    private final ObjectMapper<T> mapper;
    private final ConfigurationOptions options;
    private final JacksonConfigurationLoader loader;

    JacksonConfigurateConfigService(ConfigDefinition<T> definition, ConfigMergeService mergeService, ChangeSetLogger changeSetLogger) throws SerializationException {
        this.definition = definition;
        this.mergeService = mergeService;
        this.changeSetLogger = changeSetLogger;
        this.logger = definition.logger().child("config");

        TypeSerializerCollection.Builder serializerBuilder = TypeSerializerCollection.defaults().childBuilder();
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
    public synchronized void update(UnaryOperator<T> updater) throws IOException {
        T next = updater.apply(get());
        if (next == null) {
            throw new IllegalStateException("Could not update config, returned null.");
        }
        write(next);
        current.set(next);
    }

    @Override
    public synchronized void reload() throws IOException {
        BasicConfigurationNode defaults = defaults();
        BasicConfigurationNode effective;

        if (Files.notExists(file())) {
            createParentDirectories();
            effective = defaults.copy();
            saveAtomically(effective);
            logger.info("default config created", LogMetadata.event("config.created").and(LogMetadata.Key.FILE, file()));

        } else {
            effective = loader.load();
            ChangeSet changeSet = mergeService.merge(defaults, effective);

            if (changeSet.hasChanges()) {
                saveAtomically(effective);
                changeSetLogger.log(logger, "config", file().toString(), changeSet);
            }
        }
        try {
            current.set(mapper.load(effective));
        } catch (SerializationException exception) {
            throw new IOException("Failed to deserialize config " + file(), exception);
        }
    }

    @Override
    public synchronized void save() throws IOException {
        write(get());
    }

    private void write(T value) throws IOException {
        createParentDirectories();
        BasicConfigurationNode target = BasicConfigurationNode.root(options);
        try {
            mapper.save(value, target);
        } catch (SerializationException exception) {
            throw new IOException("Failed to serialize config " + file(), exception);
        }

        BasicConfigurationNode defaults = defaults();
        ChangeSet changeSet = mergeService.merge(defaults, target);
        saveAtomically(target);

        if (changeSet.hasChanges()) {
            changeSetLogger.log(logger, "config", file().toString(), changeSet);
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
        InputStream stream = definition.resourceClassLoader().getResourceAsStream(definition.defaultResource());
        if (stream == null) {
            throw new IOException("Cannot find default resource: " + definition.defaultResource());
        }
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private void createParentDirectories() throws IOException {
        if (file().getParent() != null) {
            Files.createDirectories(file().getParent());
        }
    }

    private void saveAtomically(BasicConfigurationNode target) throws IOException {
        Path file = file();
        Path parent = file.getParent();
        Path tempFile = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");

        try {
            JacksonConfigurationLoader.builder()
                    .path(tempFile)
                    .defaultOptions(options)
                    .build()
                    .save(target);

            try {
                Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                // Some filesystems do not support atomic replacement; preserve correctness with a normal replace.
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
