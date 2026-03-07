package net.crystalixs.core.common.config;

import net.crystalixs.core.common.sync.AbstractNodeMergeService;
import net.crystalixs.core.common.logging.ChangeSet;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Deque;

public final class ConfigMergeService extends AbstractNodeMergeService {

    @Override
    protected void mergeNonMap(ConfigurationNode defaults, ConfigurationNode user, Deque<Object> path, ChangeSet changes) {
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
}
