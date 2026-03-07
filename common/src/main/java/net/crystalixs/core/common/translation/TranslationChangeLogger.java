package net.crystalixs.core.common.translation;

import java.util.logging.Logger;

public final class TranslationChangeLogger {

    public void log(Logger logger, String fileName, TranslationChangeSet changeSet) {
        if (!changeSet.hasChanges()) return;

        logger.info("Synchronized translations " + fileName + ":");
        for (String entry : changeSet.getEntries()) {
            logger.info("  " + entry);
        }
    }
}
