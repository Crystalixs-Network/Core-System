package net.crystalixs.core.common.translation;

import org.spongepowered.configurate.ConfigurationNode;

public final class TranslationLoaderService {

    private final MiniMessageAdapter adapter;

    public TranslationLoaderService(MiniMessageAdapter adapter) {
        this.adapter = adapter;
    }

    public void load(ConfigurationNode root, TranslationRegistry registry) {
        for (var entry : root.childrenMap().entrySet()) {
            String key = entry.getKey().toString();
            ConfigurationNode node = entry.getValue();

            if (!node.childrenMap().isEmpty()) {
                load(node, registry);
                continue;
            }

            String raw = node.getString();
            if (raw != null) {
                registry.register(key, adapter.parse(raw));
            }
        }
    }
}
