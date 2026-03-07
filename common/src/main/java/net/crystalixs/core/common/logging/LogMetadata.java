package net.crystalixs.core.common.logging;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public final class LogMetadata {

    private final Map<String, Object> values;

    private LogMetadata(Map<String, Object> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public static LogMetadata empty() {
        return new LogMetadata(Map.of());
    }

    public static LogMetadata of(String key, Object value) {
        return empty().and(key, value);
    }

    public LogMetadata and(String key, Object value) {
        LinkedHashMap<String, Object> copy = new LinkedHashMap<>(values);
        copy.put(key, value);
        return new LogMetadata(copy);
    }

    String format() {
        if (values.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(", ", " {", "}");
        values.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }

    public String renderInline() {
        String formatted = format();
        if (formatted.isEmpty()) {
            return "";
        }
        return formatted.substring(2, formatted.length() - 1);
    }
}
