package net.crystalixs.core.common.translation;

import java.io.IOException;
import java.util.Locale;

public interface TranslationBundleLoader {

    TranslationBundle load(String bundleName, Locale locale) throws IOException;

}
