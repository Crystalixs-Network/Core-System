package net.crystalixs.core.common.logging;

public interface StructuredLogger {

    StructuredLogger child(String component);

    void info(String event);

    void info(String event, LogMetadata fields);

    void warn(String event);

    void warn(String event, LogMetadata fields);

    void warn(String event, LogMetadata fields, Throwable throwable);

    void error(String event, LogMetadata fields, Throwable throwable);
}
