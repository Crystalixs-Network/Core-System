package net.crystalixs.core.common.translation;

import net.crystalixs.core.common.logging.ChangeSetLogger;
import net.crystalixs.core.common.logging.ChangeSet;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.sync.NodeOrderNormalizer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractTranslationBundleLoader implements TranslationBundleLoader {

    private final ChangeSetLogger changeSetLogger;
    private final TranslationConfigMergeService mergeService;
    private final TranslationFlattener flattener;
    private final NodeOrderNormalizer orderNormalizer;

    protected AbstractTranslationBundleLoader() {
        this(ChangeSetLogger.createDefault(), new TranslationConfigMergeService(), new TranslationFlattener(), new NodeOrderNormalizer());
    }

    protected AbstractTranslationBundleLoader(ChangeSetLogger changeSetLogger, TranslationConfigMergeService mergeService, TranslationFlattener flattener, NodeOrderNormalizer orderNormalizer) {
        this.changeSetLogger = changeSetLogger;
        this.mergeService = mergeService;
        this.flattener = flattener;
        this.orderNormalizer = orderNormalizer;
    }

    @Override
    public final TranslationBundle load(String bundleName, Locale locale) throws IOException {
        String fileName = resolveFileName(bundleName, locale);
        Path userFile = resolveDataDirectory().resolve("lang").resolve(fileName);
        Files.createDirectories(userFile.getParent());

        try (InputStream stream = openResource(fileName)) {
            if (stream == null) {
                throw new IllegalStateException("Missing translation resource: " + fileName);
            }

            CommentedConfigurationNode defaultNode = loadDefaults(stream);
            CommentedConfigurationNode userNode = loadUserNode(userFile, defaultNode);

            ChangeSet changeSet = mergeService.merge(defaultNode, userNode);
            boolean orderChanged = orderNormalizer.matchesNotDefaultOrder(defaultNode, userNode);
            if (orderChanged) {
                userNode = orderNormalizer.orderedLike(defaultNode, userNode);
            }
            changeSetLogger.log(logger(), "translations", fileName, changeSet);
            if (changeSet.hasChanges() || orderChanged || Files.notExists(userFile)) {
                saveUserNode(userFile, userNode);
            }

            Map<String, String> flattened = flattener.flattern(userNode);
            return new TranslationBundle(locale, flattened);
        } catch (Exception exception) {
            throw new IOException("Failed to load bundle " + fileName, exception);
        }
    }

    protected String resolveFileName(String bundleName, Locale locale) {
        String tag = locale.toLanguageTag().replace('-', '_').toLowerCase();
        return bundleName + "_" + tag + ".conf";
    }

    protected CommentedConfigurationNode loadDefaults(InputStream stream) throws IOException {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .source(() -> new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
                .build();
        return loader.load();
    }

    protected CommentedConfigurationNode loadUserNode(Path userFile, CommentedConfigurationNode defaultNode) throws IOException {
        if (Files.notExists(userFile)) {
            return defaultNode.copy();
        }
        return fileLoader(userFile).load();
    }

    protected void saveUserNode(Path userFile, CommentedConfigurationNode userNode) throws IOException {
        fileLoader(userFile).save(userNode);
    }

    protected HoconConfigurationLoader fileLoader(Path userFile) {
        return HoconConfigurationLoader.builder()
                .prettyPrinting(true)
                .emitComments(true)
                .path(userFile)
                .build();
    }

    protected abstract Path resolveDataDirectory();

    protected abstract InputStream openResource(String fileName);

    protected abstract StructuredLogger logger();
}
