package net.crystalixs.core.common.logging;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Comparator;
import java.util.List;

public final class ChangeSet {

    private static final Comparator<Entry> ENTRY_COMPARATOR = Comparator
            .comparingInt((Entry entry) -> entry.type().sortOrder())
            .thenComparing(Entry::path);

    private final List<Entry> entries = new ArrayList<>();

    public void added(Deque<Object> path) {
        entries.add(new Entry(Type.ADDED, format(path), null));
    }

    public void removed(Deque<Object> path) {
        entries.add(new Entry(Type.REMOVED, format(path), null));
    }

    public void replaced(Deque<Object> path) {
        entries.add(new Entry(Type.MODIFIED, format(path), "replaced incompatible node"));
    }

    public boolean hasChanges() {
        return !entries.isEmpty();
    }

    public int size() {
        return entries.size();
    }

    public List<Entry> getEntries() {
        return entries.stream().sorted(ENTRY_COMPARATOR).toList();
    }

    public record Entry(Type type, String path, String description) {

        public String render() {
            if (description == null || description.isBlank()) {
                return type.symbol() + " " + path;
            }
            return type.symbol() + " " + path + " (" + description + ")";
        }
    }

    public enum Type {
        ADDED('+', 0),
        REMOVED('-', 1),
        MODIFIED('~', 2);

        private final char symbol;
        private final int sortOrder;

        Type(char symbol, int sortOrder) {
            this.symbol = symbol;
            this.sortOrder = sortOrder;
        }

        public char symbol() {
            return symbol;
        }

        public int sortOrder() {
            return sortOrder;
        }
    }

    private static String format(Deque<Object> path) {
        if (path.isEmpty()) return "<root>";

        boolean isFirst = true;
        StringBuilder builder = new StringBuilder();
        for (Object element : path) {
            if (!isFirst) {
                builder.append(".");
            }
            builder.append(element);
            isFirst = false;
        }
        return builder.toString();
    }
}
