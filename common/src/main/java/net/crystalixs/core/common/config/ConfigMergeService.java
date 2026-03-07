package net.crystalixs.core.common.config;

import net.crystalixs.core.common.logging.ChangeSet;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ConfigMergeService {

    public ChangeSet merge(ConfigurationNode defaults, ConfigurationNode user) {
        ChangeSet changeSet = new ChangeSet();
        merge(defaults, user, new ArrayDeque<>(), changeSet);

        return changeSet;
    }

    private void merge(ConfigurationNode defaults, ConfigurationNode user, Deque<Object> path, ChangeSet changes) {
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
            }
            return;
        }
        if (defaults.virtual()) {
            replace(user, defaults);
            changes.added(path);
        }
    }

    private void replace(ConfigurationNode target, ConfigurationNode source) {
        target.from(source);
    }
}
