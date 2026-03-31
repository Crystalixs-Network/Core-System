package net.crystalixs.core.common.bootstrap;

import net.crystalixs.core.common.logging.LogMetadata;

public abstract class AbstractPluginBootstrap<R extends AbstractPluginRuntime> {

    private final R runtime;

    protected AbstractPluginBootstrap(R runtime) {
        this.runtime = runtime;
    }

    public final void enable() {
        enableInternal();
        runtime.logger().info("plugin enabled", LogMetadata.event("plugin.enabled"));
    }

    public final void disable() {
        try {
            disableInternal();
        } finally {
            try {
                runtime.logger().info("plugin disabled", LogMetadata.event("plugin.disabled"));
            } finally {
                runtime.close();
            }
        }
    }

    public R runtime() {
        return runtime;
    }

    protected abstract void enableInternal();

    protected void disableInternal() {
    }
}
