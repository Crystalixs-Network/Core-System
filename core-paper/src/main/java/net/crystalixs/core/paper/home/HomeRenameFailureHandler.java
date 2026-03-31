package net.crystalixs.core.paper.home;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.home.logging.HomeLogEvent;
import net.crystalixs.core.paper.home.logging.StructuredHomeLog;
import org.bukkit.entity.Player;

public final class HomeRenameFailureHandler {

    public void handle(Player player, HomeRenameExecutor.Failure failure, StructuredLogger logger) {
        if (failure.shouldLogWarn()) {
            HomeException exception = failure.exception();
            new StructuredHomeLog(logger).warn(HomeLogEvent.RENAME_FAILED, player, LogMetadata.of(LogMetadata.Key.ERROR, exception.error().name()), exception);
        }
        player.sendMessage(failure.message());
    }
}
