package net.crystalixs.core.common.config;

import java.util.logging.Logger;

public final class ConfigChangeLogger {

    public void log(Logger logger, String fileName, ConfigChangeSet changeSet) {
        if (!changeSet.hasChanges()) return;

        logger.info("Synchronized config " + fileName + ":");
        for (String entry : changeSet.getEntries()) {
            logger.info("  " + entry);
        }
    }
}
