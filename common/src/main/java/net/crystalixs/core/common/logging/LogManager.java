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

    private final LogOutput output;

    private LogManager(LogOutput output) {
        this.output = output;
    }

    public static LogManager createForJavaUtil(java.util.logging.Logger platformLogger, Path logDirectory) {
        return create(javaUtilOutput(platformLogger), platformLogger::warning, logDirectory);
    }

    public static LogManager createForSlf4j(Logger platformLogger, Path logDirectory) {
        return create(slf4jOutput(platformLogger), platformLogger::warn, logDirectory);
    }

    @Override
    public StructuredLogger logger(String name) {
        return new LoggerImpl(name, output);
    }

    @Override
    public void close() {
        output.close();
    }

    private static LogManager create(LogOutput consoleLogOutput, Consumer<String> fallbackWarn, Path logDirectory) {
        List<LogOutput> outputs = new ArrayList<>();
        outputs.add(consoleLogOutput);
        try {
            outputs.add(new RollingFileOutput(logDirectory, DEFAULT_MAX_FILE_SIZE, DEFAULT_MAX_FILES));
        } catch (IOException exception) {
            fallbackWarn.accept("Failed to initialize file logging in " + logDirectory + ": " + exception.getMessage());
        }
        return new LogManager(new CompositeLogOutput(outputs));
    }

    private static LogOutput javaUtilOutput(java.util.logging.Logger platformLogger) {
        return new ConsoleLogOutput(
                platformLogger::info,
                platformLogger::warning,
                (message, throwable) -> platformLogger.log(WARNING, message, throwable),
                platformLogger::severe,
                (message, throwable) -> platformLogger.log(SEVERE, message, throwable)
        );
    }

    private static LogOutput slf4jOutput(Logger platformLogger) {
        return new ConsoleLogOutput(platformLogger::info, platformLogger::warn, platformLogger::warn, platformLogger::error, platformLogger::error);
    }

    public interface LogOutput extends AutoCloseable {

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

    private record LoggerImpl(String name, LogOutput output) implements StructuredLogger {
        @Override
        public StructuredLogger child(String component) {
            return new LoggerImpl(name + "/" + component, output);
        }

        @Override
        public void info(String event, LogMetadata fields) {
            log(Level.INFO, event, fields, null);
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
            output.log(new Entry(Instant.now(), level, name, event, fields, throwable));
        }
    }

    private record CompositeLogOutput(List<LogOutput> outputs) implements LogOutput {
        private CompositeLogOutput(List<LogOutput> outputs) {
            this.outputs = List.copyOf(outputs);
        }

        @Override
        public void log(Entry entry) {
            for (LogOutput output : outputs) output.log(entry);
        }

        @Override
        public void close() {
            RuntimeException failure = null;
            for (LogOutput output : outputs) {
                try {
                    output.close();
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

    private record ConsoleLogOutput(Consumer<String> infoLogger, Consumer<String> warnLogger, ThrowableLogger warnThrowableLogger, Consumer<String> errorLogger, ThrowableLogger errorThrowableLogger) implements LogOutput {

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
