package net.crystalixs.core.common.command.help;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record NetworkHelpCatalog(@NotNull String sourceId, @NotNull SourceType sourceType, @NotNull Instant generatedAt, @NotNull List<Entry> entries) {

    public NetworkHelpCatalog {
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(sourceType, "sourceType");
        Objects.requireNonNull(generatedAt, "generatedAt");
        entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
    }

    public enum SourceType {
        PROXY, BACKEND
    }

    public record Entry(
            @NotNull String syntax,
            @NotNull String description,
            String permission,
            @NotNull String command,
            @NotNull List<Argument> arguments
    ) {
        public Entry {
            Objects.requireNonNull(syntax, "syntax");
            Objects.requireNonNull(description, "description");
            Objects.requireNonNull(command, "command");
            arguments = List.copyOf(Objects.requireNonNull(arguments, "arguments"));
        }
    }

    public record Argument(@NotNull String syntax, boolean optional, @NotNull String description) {
        public Argument {
            Objects.requireNonNull(syntax, "syntax");
            Objects.requireNonNull(description, "description");
        }
    }
}
