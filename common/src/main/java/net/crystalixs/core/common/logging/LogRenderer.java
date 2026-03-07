package net.crystalixs.core.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class LogRenderer {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    public String format(LogManager.Entry entry) {
        StringBuilder builder = new StringBuilder()
                .append(TIMESTAMP_FORMAT.format(entry.timestamp()))
                .append(" | ")
                .append(padLevel(entry.level()))
                .append(" | ")
                .append(entry.loggerName())
                .append(" | ")
                .append(entry.message());

        String fields = entry.fields().renderInline();
        if (!fields.isEmpty()) {
            builder.append(" | ").append(fields);
        }

        if (entry.throwable() != null) {
            builder.append(System.lineSeparator()).append(stackTrace(entry.throwable()));
        }
        return builder.toString();
    }

    private String padLevel(LogManager.Level level) {
        return switch (level) {
            case INFO -> "INFO ";
            case WARN -> "WARN ";
            case ERROR -> "ERROR";
        };
    }

    private String stackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString().stripTrailing();
    }
}


