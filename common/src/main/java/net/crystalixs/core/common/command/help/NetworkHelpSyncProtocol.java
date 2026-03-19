package net.crystalixs.core.common.command.help;

import net.crystalixs.core.common.command.help.NetworkHelpCatalog.Entry;
import net.crystalixs.core.common.command.help.NetworkHelpCatalog.SourceType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class NetworkHelpSyncProtocol {

    public static final @NotNull String REDIS_TOPIC = "core:help_sync:catalog";
    public static final int VERSION = 2;

    private NetworkHelpSyncProtocol() {
    }

    public record SnapshotPayload(int version, @NotNull String sourceId, @NotNull SourceType sourceType, long generatedAtEpochMilli, @NotNull List<EntryPayload> entries) {
        public SnapshotPayload {
            Objects.requireNonNull(sourceId, "sourceId");
            Objects.requireNonNull(sourceType, "sourceType");
            entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
        }

        public static @NotNull SnapshotPayload fromCatalog(@NotNull NetworkHelpCatalog catalog) {
            return new SnapshotPayload(VERSION, catalog.sourceId(), catalog.sourceType(), catalog.generatedAt().toEpochMilli(), catalog.entries().stream()
                    .map(entry -> new EntryPayload(
                            entry.syntax(),
                            entry.description(),
                            entry.permission(),
                            entry.command(),
                            entry.arguments().stream()
                                    .map(argument -> new ArgumentPayload(argument.syntax(), argument.optional(), argument.description()))
                                    .toList()))
                    .toList());
        }

        public @NotNull NetworkHelpCatalog toCatalog() {
            return new NetworkHelpCatalog(sourceId, sourceType, Instant.ofEpochMilli(generatedAtEpochMilli), entries.stream()
                    .map(entry -> new Entry(
                            entry.syntax(),
                            entry.description(),
                            entry.permission(),
                            entry.command(),
                            entry.arguments().stream()
                                    .map(argument -> new NetworkHelpCatalog.Argument(argument.syntax(), argument.optional(), argument.description()))
                                    .toList()))
                    .toList());
        }
    }

    public record EntryPayload(
            @NotNull String syntax,
            @NotNull String description,
            String permission,
            @NotNull String command,
            @NotNull List<ArgumentPayload> arguments
    ) {
        public EntryPayload {
            Objects.requireNonNull(syntax, "syntax");
            Objects.requireNonNull(description, "description");
            Objects.requireNonNull(command, "command");
            arguments = List.copyOf(Objects.requireNonNull(arguments, "arguments"));
        }
    }

    public record ArgumentPayload(@NotNull String syntax, boolean optional, @NotNull String description) {
        public ArgumentPayload {
            Objects.requireNonNull(syntax, "syntax");
            Objects.requireNonNull(description, "description");
        }
    }
}
