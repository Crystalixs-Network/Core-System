package net.crystalixs.core.velocity.config.translation;

import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static net.kyori.adventure.key.Key.key;

public final class VelocityTranslationRegistry {

    private final Path langDirectory;
    private final MiniMessage miniMessage;
    private final ClassLoader resourceClassLoader;
    private final Locale defaultLocale;

    private MiniMessageTranslationStore activeStore;

    public VelocityTranslationRegistry(Path langDirectory, ClassLoader resourceClassLoader, Locale defaultLocale, MiniMessage miniMessage) {
        this.langDirectory = langDirectory;
        this.resourceClassLoader = resourceClassLoader;
        this.defaultLocale = defaultLocale;
        this.miniMessage = miniMessage;
    }

    public synchronized void registerBundle(@NotNull @KeyPattern.Value String bundleName, @NotNull List<Locale> locales) throws IOException {
        if (activeStore != null) {
            GlobalTranslator.translator().removeSource(activeStore);
        }

        MiniMessageTranslationStore store = MiniMessageTranslationStore.create(key("crystalixs", bundleName), miniMessage);
        store.defaultLocale(defaultLocale);

        for (Locale locale : locales) {
            String tag = normalize(locale);
            String fileName = bundleName + "_" + tag + ".properties";
            String resourcePath = "lang/" + fileName;
            Path target = langDirectory.resolve(fileName);

            Properties defaults = loadDefaults(resourcePath);
            validateAndMerge(target, defaults);

            Properties merged = load(target);
            for (String key : merged.stringPropertyNames()) {
                String value = merged.getProperty(key);
                store.register(key, locale, value);
            }
        }

        GlobalTranslator.translator().addSource(store);
        activeStore = store;
    }

    private Properties loadDefaults(String resourcePath) throws IOException {
        try (var stream = resourceClassLoader.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("Missing translation resource: " + resourcePath);
            }

            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                Properties properties = new Properties();
                properties.load(reader);

                return properties;
            }
        }
    }

    private void validateAndMerge(Path file, Properties defaults) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.notExists(file)) {
            store(file, defaults, "Generated from defaults");
            return;
        }

        Properties user = load(file);
        Properties merged = merge(defaults, user);
        store(file, merged, "This config was updated based on the defaults. More information can be found in the changelog.");
    }

    private Properties load(Path file) throws IOException {
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    /**
     * Mergen von der Standard-Config und der User-Config unterliegen den folgenden Regeln:
     * <ol>
     *     <li>Defaults, die in der userNode fehlen, werden dort hinzugefügt</li>
     *     <li>Bestehende Felder in userNode werden beibehalten</li>
     *     <li>Felder in der userNode, die in defaultNode nicht existieren, werden entfernt</li>
     * </ol>
     */
    public static Properties merge(Properties defaults, Properties user) {
        Properties merged = new OrderedProperties();

        for (String key : defaults.stringPropertyNames()) {
            String value = user.getProperty(key, defaults.getProperty(key));
            merged.setProperty(key, value);
        }
        return merged;
    }

    private void store(Path file, Properties properties, String comment) throws IOException {
        Properties ordered = new OrderedProperties();
        Set<String> keys = new TreeSet<>(properties.stringPropertyNames());

        for (String key : keys) {
            ordered.setProperty(key, properties.getProperty(key));
        }

        try (var writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
            ordered.store(writer, comment);
        }
    }

    private @NonNull String normalize(Locale locale) {
        return locale.toLanguageTag()
                .replace('-', '_')
                .toLowerCase(Locale.ROOT);
    }

    private static final class OrderedProperties extends Properties {
        @Override
        public Enumeration<Object> keys() {
            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }
    }
}
