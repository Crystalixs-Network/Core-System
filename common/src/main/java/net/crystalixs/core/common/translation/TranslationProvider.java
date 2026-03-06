package net.crystalixs.core.common.translation;

import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;

import java.io.IOException;
import java.util.Locale;

public final class TranslationProvider {

    private final TranslationBundleLoader loader;
    private final TranslationRegistry registry;

    private String bundleName;
    private Locale[] locales;

    public TranslationProvider(MiniMessage miniMessage, TranslationBundleLoader loader, Locale defaultLocale) {
        this.loader = loader;
        this.registry = new TranslationRegistry(miniMessage, defaultLocale);
    }

    public void load(@KeyPattern.Value String bundleName, Locale... locales) throws IOException {
        this.bundleName = bundleName;
        this.locales = locales;

        if (registry.store() == null) {
            registry.registerBundle(bundleName);
        }
        reload();
    }

    public void reload() throws IOException {
        MiniMessageTranslationStore store = registry.store();

        for (Locale locale : locales) {
            TranslationBundle bundle = loader.load(bundleName, locale);
            bundle.entries().forEach((key, value) -> store.register(key, locale, value));
        }
    }

}
