package net.crystalixs.core.common.config;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class ConfigChangeSet {

    private final List<String> entries = new ArrayList<>();

    public void added(Deque<Object> path) {
        entries.add("+ added " + format(path));
    }

    public void removed(Deque<Object> path) {
        entries.add("- removed " + format(path));
    }

    public void replaced(Deque<Object> path) {
        entries.add("~ replaced incompatible node at " + format(path));
    }

    public boolean hasChanges() {
        return !entries.isEmpty();
    }

    public List<String> getEntries() {
        return List.copyOf(entries);
    }

    private static String format(Deque<Object> path) {
        if (path.isEmpty()) return "<root>";

        boolean isFirst = true;
        StringBuilder builder = new StringBuilder();

        for (Object element : path) {
            if (!isFirst) builder.append(".");

            builder.append(element);
            isFirst = false;
        }
        return builder.toString();
    }
}
