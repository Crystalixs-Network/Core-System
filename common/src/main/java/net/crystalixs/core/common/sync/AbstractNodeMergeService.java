package net.crystalixs.core.common.sync;

import net.crystalixs.core.common.logging.ChangeSet;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractNodeMergeService {

    public final ChangeSet merge(ConfigurationNode defaults, ConfigurationNode user) {
        ChangeSet changeSet = new ChangeSet();
        merge(defaults, user, new ArrayDeque<>(), changeSet);
        return changeSet;
    }

    private void merge(ConfigurationNode defaults, ConfigurationNode user, Deque<Object> path, ChangeSet changes) {
        if (!defaults.isMap()) {
            mergeNonMap(defaults, user, path, changes);
            return;
        }

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
    }

    protected final void replace(ConfigurationNode target, ConfigurationNode source) {
        target.from(source);
    }

    protected abstract void mergeNonMap(ConfigurationNode defaults, ConfigurationNode user, Deque<Object> path, ChangeSet changes);
}
