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
                syncLogger.info(entry.render(), LogMetadata.event("sync.entry"));
            }
        }
    }
}
