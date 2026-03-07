package net.crystalixs.core.common.logging;

import java.util.logging.Logger;

public final class ChangeLogger {

    public void log(Logger logger, String subject, String fileName, ChangeSet changeSet) {
        if (!changeSet.hasChanges()) return;

        logger.info("Synchronized " + subject + " " + fileName + ":");
        for (String entry : changeSet.getEntries()) {
            logger.info("  " + entry);
        }
    }
}
