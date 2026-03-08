package net.crystalixs.core.velocity.bootstrap;

import net.crystalixs.core.common.translation.HotReloadWatcher;
import net.crystalixs.core.common.translation.TranslationBundleMeta;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.velocity.config.platform.VelocityConfigUpdater;
import net.crystalixs.core.velocity.translation.VelocityTranslationBundleLoader;

import java.util.Locale;

public final class VelocityTranslationBootstrap implements AutoCloseable {

    private final TranslationProvider provider;
    private final HotReloadWatcher watcher;

    private VelocityTranslationBootstrap(TranslationProvider provider, HotReloadWatcher watcher) {
        this.provider = provider;
        this.watcher = watcher;
    }

    public static VelocityTranslationBootstrap create(VelocityPluginRuntime runtime, VelocityConfigUpdater configUpdater) {
        TranslationProvider provider = TranslationProvider.builder()
                .logger(runtime.logger())
                .withMiniMessage(runtime.miniMessage())
                .withLoader(new VelocityTranslationBundleLoader(runtime.dataDirectory(), runtime.logger().child("translations")))
                .bundle(TranslationBundleMeta.builder()
                        .bundleName("messages")
                        .defaultLocale(Locale.GERMANY)
                        .build())
                .language(Locale.GERMANY)
                .build();

        HotReloadWatcher watcher = null;
        if (configUpdater.current().isHotReloadingEnabled()) {
            watcher = new HotReloadWatcher(
                    runtime.logger().child("translations"),
                    runtime.scheduler(),
                    runtime.dataDirectory().resolve("lang"),
                    1000L,
                    provider::reload
            );
            watcher.start();
        }

        return new VelocityTranslationBootstrap(provider, watcher);
    }

    public TranslationProvider provider() {
        return provider;
    }

    @Override
    public void close() {
        if (watcher != null) {
            watcher.close();
        }
    }
}
