package net.crystalixs.core.common.command.help;

import org.jetbrains.annotations.NotNull;

public final class NetworkHelpSyncProtocol {

    public static final @NotNull String REDIS_TOPIC = "core:help_sync:catalog";
    public static final int VERSION = 2;

    private NetworkHelpSyncProtocol() {
    }
}
