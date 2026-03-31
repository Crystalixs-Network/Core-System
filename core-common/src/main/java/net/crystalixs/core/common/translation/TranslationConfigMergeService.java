package net.crystalixs.core.common.translation;

import net.crystalixs.core.common.logging.ChangeSet;
import net.crystalixs.core.common.sync.AbstractNodeMergeService;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Deque;

public final class TranslationConfigMergeService extends AbstractNodeMergeService {

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
}
