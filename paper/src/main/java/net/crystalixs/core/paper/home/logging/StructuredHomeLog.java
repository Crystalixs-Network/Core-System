package net.crystalixs.core.paper.home.logging;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import org.bukkit.entity.Player;

public final class StructuredHomeLog {

    private final StructuredLogger logger;

    public StructuredHomeLog(StructuredLogger logger) {
        this.logger = logger;
    }

    public void info(HomeLogEvent event, Player actor, String description) {
        logger.info(event.key(), metadata(event, actor, description));
    }

    public void warn(HomeLogEvent event, Player actor, String description, Throwable throwable) {
        logger.warn(event.key(), metadata(event, actor, description), throwable);
    }

    private LogMetadata metadata(HomeLogEvent event, Player actor, String description) {
        return LogMetadata.event(event.key())
                .and(LogMetadata.Key.ACTOR, actor.getName())
                .and(LogMetadata.Key.SUBJECT, actor.getUniqueId().toString())
                .and(LogMetadata.Key.DESCRIPTION, description);
    }
}
