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
            syncLogger.info("synchronized changes", LogMetadata.of("subject", subject)
                    .and("file", fileName)
                    .and("entries", changeSet.size()));

            for (ChangeSet.Entry entry : changeSet.getEntries()) {
                syncLogger.info(entry.render());
            }
        }
    }
}
