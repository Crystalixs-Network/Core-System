package net.crystalixs.core.common.config;

import java.util.logging.Logger;

public interface ConfigChangeLogger {

    void log(Logger logger, String fileName, ConfigChangeSet changeSet);

}
