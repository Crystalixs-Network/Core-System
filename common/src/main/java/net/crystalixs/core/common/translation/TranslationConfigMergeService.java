package net.crystalixs.core.common.translation;

import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashSet;
import java.util.Set;

public final class TranslationConfigMergeService {

    public void merge(ConfigurationNode defaults, ConfigurationNode user) {
        // Add missing defaults
        for (var entry : defaults.childrenMap().entrySet()) {
            Object key = entry.getKey();
            ConfigurationNode defaultChild = entry.getValue();
            ConfigurationNode userChild = user.node(key);

            if (userChild.virtual()) {
                userChild.from(defaultChild);
            } else {
                merge(defaultChild, userChild);
            }
        }

        // Remove old keys that don't exist in defaults
        Set<Object> toRemove = new HashSet<>();
        for (Object key : user.childrenMap().keySet()) {
            if (!defaults.childrenMap().containsKey(key)) {
                toRemove.add(key);
            }
        }
        toRemove.forEach(object -> user.node(object).raw(null));
    }

}
