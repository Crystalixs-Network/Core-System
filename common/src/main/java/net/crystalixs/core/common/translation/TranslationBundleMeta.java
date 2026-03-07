package net.crystalixs.core.common.translation;

import net.kyori.adventure.key.KeyPattern.Value;

import java.util.Locale;

public interface TranslationBundleMeta {

    @Value String bundleName();

    Locale defaultLocale();

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private @Value String bundleName;
        private Locale defaultLocale;

        public Builder bundleName(@Value String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        public Builder defaultLocale(Locale defaultLocale) {
            this.defaultLocale = defaultLocale;
            return this;
        }

        public TranslationBundleMeta build() {
            return new TranslationBundleMetaImpl(bundleName, defaultLocale);
        }
    }

    record TranslationBundleMetaImpl(@Value String bundleName, Locale defaultLocale) implements TranslationBundleMeta {
    }
}
