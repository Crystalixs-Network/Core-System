package net.crystalixs.core.common.sync;

import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public final class NodeOrderNormalizer {

    public <N extends ConfigurationNode> N orderedLike(N defaults, N user) {
        @SuppressWarnings("unchecked")
        N ordered = (N) defaults.copy();
        overlay(defaults, user, ordered);
        return ordered;
    }

    public boolean matchesNotDefaultOrder(ConfigurationNode defaults, ConfigurationNode user) {
        if (!defaults.isMap() || !user.isMap()) {
            return false;
        }

        List<Object> defaultKeys = new ArrayList<>(defaults.childrenMap().keySet());
        List<Object> userKeys = new ArrayList<>(user.childrenMap().keySet());
        if (!defaultKeys.equals(userKeys)) {
            return true;
        }

        for (Object key : defaultKeys) {
            if (matchesNotDefaultOrder(defaults.node(key), user.node(key))) {
                return true;
            }
        }
        return false;
    }

    private void overlay(ConfigurationNode defaults, ConfigurationNode user, ConfigurationNode ordered) {
        if (!defaults.isMap() || !user.isMap()) {
            if (!user.virtual()) {
                ordered.from(user);
            }
            return;
        }

        for (Object key : defaults.childrenMap().keySet()) {
            ConfigurationNode defaultChild = defaults.node(key);
            ConfigurationNode userChild = user.node(key);
            ConfigurationNode orderedChild = ordered.node(key);

            if (defaultChild.isMap() && userChild.isMap()) {
                overlay(defaultChild, userChild, orderedChild);
                continue;
            }

            if (!userChild.virtual()) {
                orderedChild.from(userChild);
            }
        }
    }
}
