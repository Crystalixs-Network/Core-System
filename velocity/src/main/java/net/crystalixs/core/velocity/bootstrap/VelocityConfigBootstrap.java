package net.crystalixs.core.velocity.bootstrap;

import net.crystalixs.core.common.config.ConfigDefinition;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.common.config.ConfigServiceFactory;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.config.platform.VelocityConfigurationProvider;

import java.io.IOException;

public final class VelocityConfigBootstrap {

    public VelocityConfigUpdater load(VelocityPluginRuntime runtime) {
        try {
            ConfigService<VelocityConfig> configService = ConfigServiceFactory.create(new ConfigDefinition<>(
                    runtime.dataDirectory().resolve("config.json"), "config.json",
                    VelocityConfig.class,
                    runtime.logger(),
                    new VelocityConfigurationProvider(runtime.miniMessage()),
                    getClass().getClassLoader()
            ));
            configService.reload();
            return new VelocityConfigUpdater(configService);

        } catch (Exception exception) {
            runtime.logger().error("config load failed during startup", LogMetadata
                    .event("config.load_failed")
                    .and(LogMetadata.Key.FILE, runtime.dataDirectory().resolve("config.json")), exception);
            throw new IllegalStateException("Could not load config", exception);
        }
    }

    public void save(VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater) {
        if (configUpdater == null) {
            return;
        }
        try {
            configUpdater.save();
        } catch (IOException exception) {
            runtime.logger().error("config save failed during shutdown", LogMetadata
                    .event("config.save_failed")
                    .and(LogMetadata.Key.FILE, runtime.dataDirectory().resolve("config.json")), exception);
        }
    }
}
