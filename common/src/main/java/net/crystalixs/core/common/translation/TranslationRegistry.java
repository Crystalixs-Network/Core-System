package net.crystalixs.core.common.translation;

import net.kyori.adventure.key.KeyPattern.Value;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;

import java.util.Locale;

import static net.kyori.adventure.key.Key.key;

public final class TranslationRegistry {

    private final MiniMessage miniMessage;
    private MiniMessageTranslationStore store;

    private final Locale defaultLocale;

    public TranslationRegistry(MiniMessage miniMessage, Locale defaultLocale) {
        this.miniMessage = miniMessage;
        this.defaultLocale = defaultLocale;
    }

    public void registerBundle(@Value String bundleName, Locale... locales) {
        store = MiniMessageTranslationStore.create(key("crystalixs", bundleName), miniMessage);
        store.defaultLocale(defaultLocale);

        GlobalTranslator.translator().addSource(store);
    }

    public MiniMessageTranslationStore store() {
        return store;
    }
}
