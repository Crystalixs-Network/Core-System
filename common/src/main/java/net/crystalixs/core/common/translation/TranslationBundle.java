package net.crystalixs.core.common.translation;

import java.util.Locale;
import java.util.Map;

public record TranslationBundle(Locale locale, Map<String, String> entries) {
}
