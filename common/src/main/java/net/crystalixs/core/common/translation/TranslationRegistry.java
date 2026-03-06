package net.crystalixs.core.common.translation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.text;

public final class TranslationRegistry {

    private final Map<String, Component> translations = new ConcurrentHashMap<>();

    public void register(String key, Component component) {
        translations.put(key, component);
    }

    public Component get(String key) {
        Component result = translations.get(key);
        if (result == null) {
            return text("Missing translation: " + key, NamedTextColor.RED);
        }
        return result;
    }

}
