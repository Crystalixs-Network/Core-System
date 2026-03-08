package net.crystalixs.core.paper.bootstrap;

import net.crystalixs.core.common.logging.StructuredLogger;
import net.crystalixs.core.common.translation.HotReloadWatcher;
import net.crystalixs.core.common.translation.TranslationBundleMeta;
import net.crystalixs.core.common.translation.TranslationProvider;
import net.crystalixs.core.paper.translation.PaperTranslationBundleLoader;

import java.util.Locale;

public final class PaperTranslationBootstrap implements AutoCloseable {

    private final HotReloadWatcher watcher;

    private PaperTranslationBootstrap(HotReloadWatcher watcher) {
        this.watcher = watcher;
    }

    public static PaperTranslationBootstrap create(PaperPluginRuntime runtime) {
        final StructuredLogger logger = runtime.componentLogger("translations");

        TranslationProvider provider = TranslationProvider.builder()
                .logger(logger)
                .withMiniMessage(runtime.miniMessage())
                .withLoader(new PaperTranslationBundleLoader(runtime.plugin(), logger))
                .bundle(TranslationBundleMeta.builder()
                        .bundleName("messages")
                        .defaultLocale(Locale.GERMANY)
                        .build())
                .language(Locale.GERMANY)
                .build();

        // TODO Config-Check ergänzen, sobald Hot-Reload konfigurierbar ist.
        HotReloadWatcher watcher = new HotReloadWatcher(logger,
                runtime.scheduler(),
                runtime.plugin().getDataPath().resolve("lang"),
                1000L,
                provider::reload
        );
        watcher.start();

        return new PaperTranslationBootstrap(watcher);
    }

    @Override
    public void close() {
        watcher.close();
    }
}
