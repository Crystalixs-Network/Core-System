package net.crystalixs.core.paper.home;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import org.bukkit.entity.Player;

public final class HomeRenameFailureHandler {

    public void handle(Player player, HomeRenameExecutor.Failure failure, StructuredLogger logger) {
        if (failure.shouldLogWarn()) {
            HomeException exception = failure.exception();
            logger.warn("home rename failed", LogMetadata
                    .event("command.home.rename.failed")
                    .and(LogMetadata.Key.ACTOR, player.getName())
                    .and(LogMetadata.Key.SUBJECT, player.getUniqueId().toString())
                    .and(LogMetadata.Key.DESCRIPTION, exception.error().name()), exception);
        }
        player.sendMessage(failure.message());
    }
}
