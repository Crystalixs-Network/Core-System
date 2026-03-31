package net.crystalixs.core.common.logging;

public interface ChangeSetLogger {

    void log(StructuredLogger logger, String subject, String fileName, ChangeSet changeSet);

    static ChangeSetLogger createDefault() {
        return new DefaultChangeSetLogger();
    }

    final class DefaultChangeSetLogger implements ChangeSetLogger {

        @Override
        public void log(StructuredLogger logger, String subject, String fileName, ChangeSet changeSet) {
            if (!changeSet.hasChanges()) {
                return;
            }

            StructuredLogger syncLogger = logger.child("sync");
            syncLogger.info("synchronized changes applied", LogMetadata
                    .event("sync.applied")
                    .and(LogMetadata.Key.SUBJECT, subject)
                    .and(LogMetadata.Key.FILE, fileName)
                    .and(LogMetadata.Key.ENTRIES, changeSet.size()));

            for (ChangeSet.Entry entry : changeSet.getEntries()) {
                LogMetadata metadata = LogMetadata.event("sync.entry")
                        .and(LogMetadata.Key.PATH, entry.path())
                        .and(LogMetadata.Key.CHANGE_TYPE, entry.type().name().toLowerCase());
                if (entry.description() != null && !entry.description().isBlank()) {
                    metadata = metadata.and(LogMetadata.Key.DESCRIPTION, entry.description());
                }
                syncLogger.info("synchronized change entry", metadata);
            }
        }
    }
}
