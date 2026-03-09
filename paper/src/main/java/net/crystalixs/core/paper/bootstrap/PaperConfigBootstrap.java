package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.config.ConfigDefinition;
import net.crystalixs.core.common.config.ConfigService;
import net.crystalixs.core.common.config.ConfigServiceFactory;
import net.crystalixs.core.common.logging.LogMetadata;
import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.paper.config.PaperConfig;
import net.crystalixs.core.paper.config.platform.PaperConfigUpdater;
import net.crystalixs.core.paper.config.platform.PaperConfigurationProvider;

public final class PaperConfigBootstrap {

    public PaperConfigUpdater load(PaperPluginRuntime runtime) {
        final StructuredLogger logger = runtime.componentLogger("config");
        try {
            ConfigService<PaperConfig> configService = ConfigServiceFactory.create(new ConfigDefinition<>(
                    runtime.plugin().getDataPath().resolve("config.json"), "config.json",
                    PaperConfig.class,
                    logger,
                    new PaperConfigurationProvider(),
                    getClass().getClassLoader()
            ));
            configService.reload();
            return new PaperConfigUpdater(configService);

        } catch (Exception exception) {
            logger.error("config load failed during startup", LogMetadata
                    .event("config.load_failed")
                    .and(LogMetadata.Key.FILE, runtime.plugin().getDataPath().resolve("config.json")), exception);
            throw new IllegalStateException("Could not load config", exception);
        }
    }
}
