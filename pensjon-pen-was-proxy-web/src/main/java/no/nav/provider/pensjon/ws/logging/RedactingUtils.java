package no.nav.provider.pensjon.ws.logging;

import ch.qos.logback.classic.spi.IThrowableProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public final class RedactingUtils {
   private static final Pattern fnrRegex = Pattern.compile("\\b\\d{11}\\b");

   @NotNull
   public static String redact(@NotNull String string) {
      return fnrRegex.matcher(string)
              .replaceAll("***********");
   }

   @Nullable
   public static String redactIfNotNUll(@Nullable String string) {
      return string != null ? redact(string) : null;
   }

   @NotNull
   public static Map<String, String> redact(@NotNull Map<String, String> map) {
      return map.entrySet()
              .stream()
              .collect(
                      HashMap::new,
                      (m, e) -> m.put(e.getKey(), redactIfNotNUll(e.getValue())),
                      HashMap::putAll
              );
   }

   @NotNull
   public static RedactedThrowableProxy redact(@NotNull IThrowableProxy throwableProxy) {
      return new RedactedThrowableProxy(throwableProxy);
   }
}
