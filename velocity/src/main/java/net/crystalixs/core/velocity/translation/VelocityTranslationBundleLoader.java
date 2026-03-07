package net.crystalixs.core.velocity.translation;

import net.crystalixs.core.common.translation.TranslationConfigMergeService;
import net.crystalixs.core.common.translation.TranslationBundle;
import net.crystalixs.core.common.translation.TranslationBundleLoader;
import net.crystalixs.core.common.translation.TranslationFlattener;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public final class VelocityTranslationBundleLoader implements TranslationBundleLoader {

    private final TranslationConfigMergeService service;
    private final TranslationFlattener flattener;
    private final Path dataDirectory;

    private VelocityTranslationBundleLoader(Path dataDirectory, TranslationConfigMergeService service, TranslationFlattener flattener) {
        this.dataDirectory = dataDirectory;
        this.service = service;
        this.flattener = flattener;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TranslationBundle load(String bundleName, Locale locale) throws IOException {
        String tag = locale.toLanguageTag().replace('-', '_').toLowerCase();
        String fileName = bundleName + "_" + tag + ".conf";

        Path langDirectory = dataDirectory.resolve("lang");
        Path userFile = langDirectory.resolve(fileName);
        Files.createDirectories(userFile.getParent());

        return loadFromResources(locale, fileName, userFile);
    }

    private TranslationBundle loadFromResources(Locale locale, String fileName, Path userFile) throws IOException {
        try (var stream = getClass().getClassLoader().getResourceAsStream("lang/" + fileName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing translation resource: " + fileName);
            }

            HoconConfigurationLoader defaultLoader = HoconConfigurationLoader.builder()
                    .source(() -> new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
                    .build();
            CommentedConfigurationNode defaultNode = defaultLoader.load();

            HoconConfigurationLoader fileLoader = HoconConfigurationLoader.builder()
                    .prettyPrinting(true)
                    .emitComments(true)
                    .path(userFile)
                    .build();
            CommentedConfigurationNode userNode = Files.notExists(userFile) ? defaultNode.copy() : fileLoader.load();

            service.merge(defaultNode, userNode);
            fileLoader.save(userNode);

            Map<String, String> flattened = flattener.flattern(userNode);
            return new TranslationBundle(locale, flattened);

        } catch (Exception exception) {
            throw new IOException("Failed to load bundle " + fileName, exception);
        }
    }

    public static final class Builder {
        private Path dataDirectory;

        public Builder dataDirectory(Path dataDirectory) {
            this.dataDirectory = dataDirectory;
            return this;
        }

        public VelocityTranslationBundleLoader build() {
            return new VelocityTranslationBundleLoader(dataDirectory, new TranslationConfigMergeService(), new TranslationFlattener());
        }
    }
}
