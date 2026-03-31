package net.crystalixs.core.common.translation;

import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TranslationFlattener {

    public Map<String, String> flattern(ConfigurationNode root) {
        Map<String, String> result = new LinkedHashMap<>();
        flattern(root, "", result);
        return result;
    }

    private void flattern(ConfigurationNode node, String path, Map<String, String> map) {
        if (!node.childrenMap().isEmpty()) {
            for (var entry : node.childrenMap().entrySet()) {
                String next = path.isEmpty()
                        ? entry.getKey().toString()
                        : path + "." + entry.getKey();

                flattern(entry.getValue(), next, map);
            }
            return;
        }

        String value = node.getString();
        if(value != null) {
            map.put(path, value);
        }
    }
}
