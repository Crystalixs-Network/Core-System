package net.crystalixs.core.common.translation;

import net.kyori.adventure.key.KeyPattern.Value;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.logging.LogMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class TranslationProvider {

    private final StructuredLogger logger;
    private final TranslationBundleLoader loader;
    private final TranslationRegistry registry;
    private final Locale defaultLocale;

    private @Value String bundleName;
    private Locale[] locales;

    public TranslationProvider(StructuredLogger logger, MiniMessage miniMessage, TranslationBundleLoader loader, Locale defaultLocale) {
        this.logger = logger.child("translations");
        this.loader = loader;
        this.defaultLocale = defaultLocale;
        this.registry = new TranslationRegistry(miniMessage, defaultLocale);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void load(@Value String bundleName, Locale... locales) {
        this.bundleName = bundleName;
        this.locales = locales;

        if (locales == null || locales.length == 0) {
            this.locales = new Locale[]{defaultLocale};
            logger.warn("default locale selected", LogMetadata
                    .event("translations.locales.defaulted")
                    .and(LogMetadata.Key.LOCALE, defaultLocale.toLanguageTag()));
        }
        if (registry.store() == null) {
            registry.registerBundle(bundleName);
        }
        reload();
    }

    public boolean reload() {
        MiniMessageTranslationStore oldStore = registry.store();
        if (oldStore != null) {
            GlobalTranslator.translator().removeSource(oldStore);
        }
        registry.registerBundle(bundleName);

        MiniMessageTranslationStore store = registry.store();
        try {
            for (Locale locale : locales) {
                TranslationBundle bundle = loader.load(bundleName, locale);
                bundle.entries().forEach((key, value) -> store.register(key, locale, value));
            }
            logger.info("translation bundles reloaded", LogMetadata
                    .event("translations.reloaded")
                    .and(LogMetadata.Key.BUNDLE, bundleName)
                    .and(LogMetadata.Key.LOCALE_COUNT, locales.length));
            return true;
        } catch (IOException exception) {
            logger.warn("translation reload failed", LogMetadata
                    .event("translations.reload_failed")
                    .and(LogMetadata.Key.BUNDLE, bundleName), exception);
            return false;
        }
    }

    public static final class Builder {
        private final List<Locale> languages = new ArrayList<>();

        private StructuredLogger logger;
        private MiniMessage miniMessage;
        private TranslationBundleLoader loader;
        private Locale defaultLocale;
        private String bundleName;

        public Builder logger(StructuredLogger logger) {
            this.logger = logger;
            return this;
        }

        public Builder withMiniMessage(MiniMessage miniMessage) {
            this.miniMessage = miniMessage;
            return this;
        }

        public Builder withLoader(TranslationBundleLoader loader) {
            this.loader = loader;
            return this;
        }

        public Builder bundle(TranslationBundleMeta bundleMeta) {
            this.bundleName = bundleMeta.bundleName();
            this.defaultLocale = bundleMeta.defaultLocale();
            return this;
        }

        public Builder language(Locale locale) {
            this.languages.add(locale);
            return this;
        }

        public TranslationProvider build() {
            Objects.requireNonNull(logger, "logger");
            Objects.requireNonNull(loader, "loader");
            if (bundleName == null) {
                throw new IllegalStateException("Bundle name is required");
            }
            if (defaultLocale == null) {
                throw new IllegalStateException("Default locale is required");
            }
            if (miniMessage == null) {
                logger.warn("default minimessage selected", LogMetadata.event("translations.minimessage.defaulted"));
                this.miniMessage = MiniMessage.miniMessage();
            }

            TranslationProvider provider = new TranslationProvider(logger, miniMessage, loader, defaultLocale);
            @Value String bundleName = this.bundleName;

            Locale[] selectedLocales;
            if (!languages.isEmpty()) {
                selectedLocales = languages.toArray(Locale[]::new);
            } else {
                selectedLocales = new Locale[]{defaultLocale};
            }
            provider.load(bundleName, selectedLocales);

            return provider;
        }
    }
}
