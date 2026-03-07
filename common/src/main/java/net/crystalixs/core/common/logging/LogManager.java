package net.crystalixs.core.common.logging;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

public final class LogManager implements LogFactory {

    private static final long DEFAULT_MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final int DEFAULT_MAX_FILES = 14;

    private final Sink sink;

    private LogManager(Sink sink) {
        this.sink = sink;
    }

    public static LogManager createForJavaUtil(java.util.logging.Logger platformLogger, Path logDirectory, String filePrefix) {
        return create(new ConsoleSink(platformLogger::info, platformLogger::warning,
                        (message, throwable) -> platformLogger.log(WARNING, message, throwable),
                        platformLogger::severe,
                        (message, throwable) -> platformLogger.log(SEVERE, message, throwable)),
                platformLogger::warning,
                logDirectory,
                filePrefix);
    }

    public static LogManager createForSlf4j(Logger platformLogger, Path logDirectory, String filePrefix) {
        return create(new ConsoleSink(platformLogger::info, platformLogger::warn, platformLogger::warn, platformLogger::error, platformLogger::error),
                platformLogger::warn,
                logDirectory, filePrefix);
    }

    @Override
    public StructuredLogger logger(String name) {
        return new LoggerImpl(name, sink);
    }

    @Override
    public void close() {
        sink.close();
    }

    private static LogManager create(Sink consoleSink, Consumer<String> fallbackWarn, Path logDirectory, String filePrefix) {
        List<Sink> sinks = new ArrayList<>();
        sinks.add(consoleSink);
        try {
            sinks.add(new RollingFileSink(logDirectory, filePrefix, DEFAULT_MAX_FILE_SIZE, DEFAULT_MAX_FILES));
        } catch (IOException exception) {
            fallbackWarn.accept("Failed to initialize file logging in " + logDirectory + ": " + exception.getMessage());
        }
        return new LogManager(new CompositeSink(sinks));
    }

    public interface Sink extends AutoCloseable {

        void log(Entry entry);

        @Override
        default void close() {
        }
    }

    public enum Level {
        INFO, WARN, ERROR
    }

    public record Entry(Instant timestamp, Level level, String loggerName, String message, LogMetadata fields, Throwable throwable) {
    }

    private record LoggerImpl(String name, Sink sink) implements StructuredLogger {

        @Override
        public StructuredLogger child(String component) {
            return new LoggerImpl(name + "/" + component, sink);
        }

        @Override
        public void info(String event) {
            info(event, LogMetadata.empty());
        }

        @Override
        public void info(String event, LogMetadata fields) {
            log(Level.INFO, event, fields, null);
        }

        @Override
        public void warn(String event) {
            warn(event, LogMetadata.empty());
        }

        @Override
        public void warn(String event, LogMetadata fields) {
            log(Level.WARN, event, fields, null);
        }

        @Override
        public void warn(String event, LogMetadata fields, Throwable throwable) {
            log(Level.WARN, event, fields, throwable);
        }

        @Override
        public void error(String event, LogMetadata fields, Throwable throwable) {
            log(Level.ERROR, event, fields, throwable);
        }

        private void log(Level level, String event, LogMetadata fields, Throwable throwable) {
            sink.log(new Entry(Instant.now(), level, name, event, fields, throwable));
        }
    }

    private record CompositeSink(List<Sink> sinks) implements Sink {
        private CompositeSink(List<Sink> sinks) {
            this.sinks = List.copyOf(sinks);
        }

        @Override
        public void log(Entry entry) {
            for (Sink sink : sinks) sink.log(entry);
        }

        @Override
        public void close() {
            RuntimeException failure = null;
            for (Sink sink : sinks) {
                try {
                    sink.close();
                } catch (RuntimeException exception) {
                    if (failure == null) {
                        failure = exception;
                    } else {
                        failure.addSuppressed(exception);
                    }
                }
            }
            if (failure != null) {
                throw failure;
            }
        }
    }

    private record ConsoleSink(Consumer<String> infoLogger, Consumer<String> warnLogger, ThrowableLogger warnThrowableLogger, Consumer<String> errorLogger, ThrowableLogger errorThrowableLogger) implements Sink {

        @Override
        public void log(Entry entry) {
            String message = entry.loggerName() + " | " + entry.message();
            String fields = entry.fields().renderInline();
            if (!fields.isEmpty()) {
                message += " | " + fields;
            }

            switch (entry.level()) {
                case INFO -> infoLogger.accept(message);
                case WARN -> {
                    if (entry.throwable() == null) {
                        warnLogger.accept(message);
                    } else {
                        warnThrowableLogger.log(message, entry.throwable());
                    }
                }
                case ERROR -> {
                    if (entry.throwable() == null) {
                        errorLogger.accept(message);
                    } else {
                        errorThrowableLogger.log(message, entry.throwable());
                    }
                }
            }
        }
    }

    @FunctionalInterface
    private interface ThrowableLogger {
        void log(String message, Throwable throwable);
    }
}


