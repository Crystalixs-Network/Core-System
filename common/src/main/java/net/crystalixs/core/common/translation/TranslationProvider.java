package net.crystalixs.core.common.translation;

import net.kyori.adventure.key.KeyPattern.Value;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public final class TranslationProvider {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final TranslationBundleLoader loader;
    private final TranslationRegistry registry;
    private final Locale defaultLocale;

    private @Value String bundleName;
    private Locale[] locales;

    public TranslationProvider(MiniMessage miniMessage, TranslationBundleLoader loader, Locale defaultLocale) {
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
            logger.warning("No language was registered. Using default locale " + defaultLocale.toLanguageTag());
        }
        if (registry.store() == null) {
            registry.registerBundle(bundleName);
        }
        reload();
    }

    public void reload() {
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
        } catch (IOException exception) {
            logger.warning("Failed to reload translations: " + exception.getMessage());
        }
    }

    public static final class Builder {
        private final Logger logger = Logger.getLogger(getClass().getSimpleName());
        private final List<Locale> languages = new ArrayList<>();

        private MiniMessage miniMessage;
        private TranslationBundleLoader loader;
        private Locale defaultLocale;
        private String bundleName;

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
            if (bundleName == null) {
                throw new IllegalStateException("Bundle name is required");
            }
            if (defaultLocale == null) {
                throw new IllegalStateException("Default locale is required");
            }
            if (miniMessage == null) {
                logger.warning("No MiniMessage instance was provided. Using default instance.");
                this.miniMessage = MiniMessage.miniMessage();
            }

            TranslationProvider provider = new TranslationProvider(miniMessage, loader, defaultLocale);
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
