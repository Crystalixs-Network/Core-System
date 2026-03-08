package net.crystalixs.core.velocity.bootstrap;

import net.crystalixs.core.common.config.ConfigDefinition;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.common.config.ConfigServiceFactory;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.velocity.config.VelocityConfig;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.config.platform.VelocityConfigurationProvider;

import java.io.IOException;

public final class VelocityConfigBootstrap {

    public VelocityConfigUpdater load(VelocityPluginRuntime runtime) {
        final StructuredLogger logger = runtime.logger().child("config");
        try {
            ConfigService<VelocityConfig> configService = ConfigServiceFactory.create(new ConfigDefinition<>(
                    runtime.dataDirectory().resolve("config.json"), "config.json",
                    VelocityConfig.class,
                    logger,
                    new VelocityConfigurationProvider(runtime.miniMessage()),
                    getClass().getClassLoader()
            ));
            configService.reload();
            return new VelocityConfigUpdater(configService);

        } catch (Exception exception) {
            logger.error("config load failed during startup", LogMetadata
                    .event("config.load_failed")
                    .and(LogMetadata.Key.FILE, runtime.dataDirectory().resolve("config.json")), exception);
            throw new IllegalStateException("Could not load config", exception);
        }
    }

    public void save(VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater) {
        if (configUpdater == null) {
            return;
        }
        final StructuredLogger logger = runtime.logger().child("config");
        try {
            configUpdater.save();
        } catch (IOException exception) {
            logger.error("config save failed during shutdown", LogMetadata
                    .event("config.save_failed")
                    .and(LogMetadata.Key.FILE, runtime.dataDirectory().resolve("config.json")), exception);
        }
    }
}
