package net.crystalixs.core.paper.home.logging;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import org.bukkit.entity.Player;

public final class StructuredHomeLog {

    private final StructuredLogger logger;

    public StructuredHomeLog(StructuredLogger logger) {
        this.logger = logger;
    }

    public void info(HomeLogEvent event, Player actor) {
        logger.info(event.key(), metadata(event, actor));
    }

    public void info(HomeLogEvent event, Player actor, LogMetadata metadata) {
        logger.info(event.key(), metadata(event, actor).and(metadata));
    }

    public void warn(HomeLogEvent event, Player actor, Throwable throwable) {
        logger.warn(event.key(), metadata(event, actor), throwable);
    }

    public void warn(HomeLogEvent event, Player actor, LogMetadata metadata, Throwable throwable) {
        logger.warn(event.key(), metadata(event, actor).and(metadata), throwable);
    }

    private LogMetadata metadata(HomeLogEvent event, Player actor) {
        return LogMetadata.event(event.key())
                .and(LogMetadata.Key.ACTOR, actor.getName())
                .and(LogMetadata.Key.SUBJECT, actor.getUniqueId().toString());
    }
}
