package net.crystalixs.core.paper.translation;

import net.crystalixs.core.common.translation.TranslationBundle;
import net.crystalixs.core.common.translation.TranslationBundleLoader;
import net.crystalixs.core.common.translation.TranslationRegistry;
import net.kyori.adventure.key.KeyPattern.Value;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;

import java.io.IOException;
import java.util.Locale;

public final class PaperTranslationProvider {

    private final MiniMessage miniMessage;
    private final TranslationBundleLoader loader;

    public PaperTranslationProvider(MiniMessage miniMessage, TranslationBundleLoader loader) {
        this.miniMessage = miniMessage;
        this.loader = loader;
    }

    public void load(@Value String bundleName, Locale... locales) throws IOException {
        TranslationRegistry registry = new TranslationRegistry(miniMessage, locales[0]);
        registry.registerBundle(bundleName);

        MiniMessageTranslationStore store = registry.store();
        for (Locale locale : locales) {
            TranslationBundle bundle = loader.load(bundleName, locale);
            bundle.entries().forEach((key, value) -> store.register(key, locale, value));
        }
    }
}
