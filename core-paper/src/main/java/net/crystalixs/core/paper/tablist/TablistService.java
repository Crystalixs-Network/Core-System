package net.crystalixs.core.paper.tablist;

import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.CorePlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public final class TablistService {

    private final StructuredLogger logger;
    private final LuckPerms luckPerms;

    private TablistService(StructuredLogger logger, LuckPerms luckPerms) {
        this.logger = logger;
        this.luckPerms = luckPerms;
    }

    public static TablistService create(CorePlugin plugin, StructuredLogger logger) {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            logger.warn("LuckPerms is required for rank based tablist sorting", LogMetadata.event("tablist.missing_dependency"));
            return null;
        }
        return new TablistService(logger, LuckPermsProvider.get());
    }
}
