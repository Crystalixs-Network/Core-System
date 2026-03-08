package net.crystalixs.core.common.logging;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public final class LogMetadata {

    private static final LogMetadata EMPTY = new LogMetadata(Map.of());
    private final Map<String, Object> values;

    private LogMetadata(Map<String, Object> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public static LogMetadata of(Key key, Object value) {
        return EMPTY.and(key, value);
    }

    public static LogMetadata event(String event) {
        return of(Key.EVENT, event);
    }

    public LogMetadata and(Key key, Object value) {
        LinkedHashMap<String, Object> copy = new LinkedHashMap<>(values);
        copy.put(key.value, value);
        return new LogMetadata(copy);
    }

    public String renderInline() {
        if (values.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(", ");
        values.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }

    public enum Key {
        EVENT("event"),
        FILE("file"),
        DIRECTORY("directory"),
        BUNDLE("bundle"),
        LOCALE("locale"),
        LOCALE_COUNT("localeCount"),
        SUBJECT("subject"),
        ENTRIES("entries"),
        DEBOUNCE_MS("debounceMs"),
        PATH("path"),
        CHANGE_TYPE("changeType"),
        DESCRIPTION("description");

        private final String value;

        Key(String value) {
            this.value = value;
        }
    }
}
