package i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Central access point for all user-facing text. Strings live in
 * messages*.properties; no UI class hard-codes a literal.
 */
public final class Messages {

  private static final String BUNDLE_NAME = "messages";

  private static ResourceBundle bundle =
      ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());

  private Messages() {
  }

  public static void setLocale(Locale locale) {
    bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
  }

  public static String get(String key, Object... args) {
    String pattern = bundle.getString(key);
    if (args.length == 0) {
      return pattern;
    }
    return MessageFormat.format(pattern, args);
  }
}
