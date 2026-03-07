package net.crystalixs.core.common.translation;

import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayDeque;
import java.util.Deque;

public final class TranslationConfigMergeService {

    public TranslationChangeSet merge(ConfigurationNode defaults, ConfigurationNode user) {
        TranslationChangeSet changeSet = new TranslationChangeSet();
        merge(defaults, user, new ArrayDeque<>(), changeSet);
        return changeSet;
    }

    private void merge(ConfigurationNode defaults, ConfigurationNode user, Deque<Object> path, TranslationChangeSet changes) {
        if (defaults.isMap()) {
            if (!user.isMap() && !user.virtual()) {
                replace(user, defaults);
                changes.replaced(path);
                return;
            }
            for (var entry : defaults.childrenMap().entrySet()) {
                Object key = entry.getKey();
                ConfigurationNode defaultChild = entry.getValue();
                ConfigurationNode userChild = user.node(key);

                path.addLast(key);
                if (userChild.virtual()) {
                    replace(userChild, defaultChild);
                    changes.added(path);
                } else {
                    merge(defaultChild, userChild, path, changes);
                }
                path.removeLast();
            }

            user.childrenMap().keySet().stream()
                    .filter(key -> !defaults.childrenMap().containsKey(key))
                    .forEachOrdered(key -> {
                        path.addLast(key);
                        user.removeChild(key);
                        changes.removed(path);
                        path.removeLast();
                    });
            return;
        }
        if (defaults.isList()) {
            if (user.virtual()) {
                replace(user, defaults);
                changes.added(path);
                return;
            }
            if (user.isList()) {
                replace(user, defaults);
                changes.replaced(path);
                return;
            }

            replace(user, defaults);
            changes.replaced(path);
            return;
        }
        if (user.virtual()) {
            replace(user, defaults);
            changes.added(path);
            return;
        }
        if (user.isMap() || user.isList()) {
            replace(user, defaults);
            changes.replaced(path);
        }
    }

    private void replace(ConfigurationNode target, ConfigurationNode source) {
        target.from(source);
    }
}
