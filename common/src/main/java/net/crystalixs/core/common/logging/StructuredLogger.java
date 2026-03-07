package net.crystalixs.core.common.logging;

public interface StructuredLogger {

    StructuredLogger child(String component);

    default void info(String event) {
        info(event, LogMetadata.empty());
    }

    void info(String event, LogMetadata fields);

    default void warn(String event) {
        warn(event, LogMetadata.empty());
    }

    void warn(String event, LogMetadata fields);

    default void warn(String event, Throwable throwable) {
        warn(event, LogMetadata.empty(), throwable);
    }

    void warn(String event, LogMetadata fields, Throwable throwable);

    default void error(String event, Throwable throwable) {
        error(event, LogMetadata.empty(), throwable);
    }

    void error(String event, LogMetadata fields, Throwable throwable);
}
