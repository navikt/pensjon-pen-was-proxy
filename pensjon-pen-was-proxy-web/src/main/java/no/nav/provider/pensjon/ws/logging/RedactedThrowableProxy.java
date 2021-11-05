package no.nav.provider.pensjon.ws.logging;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static no.nav.provider.pensjon.ws.logging.RedactingUtils.redact;
import static no.nav.provider.pensjon.ws.logging.RedactingUtils.redactIfNotNUll;

public final class RedactedThrowableProxy implements IThrowableProxy {
   private final IThrowableProxy throwableProxy;

   public RedactedThrowableProxy(@NotNull IThrowableProxy throwableProxy) {
      this.throwableProxy = throwableProxy;
   }

   @Nullable
   public String getMessage() {
      return redactIfNotNUll(throwableProxy.getMessage());
   }

   @Nullable
   public String getClassName() {
      return throwableProxy.getClassName();
   }

   @Nullable
   public StackTraceElementProxy[] getStackTraceElementProxyArray() {
      return throwableProxy.getStackTraceElementProxyArray();
   }

   public int getCommonFrames() {
      return throwableProxy.getCommonFrames();
   }

   @Nullable
   public IThrowableProxy getCause() {
      final IThrowableProxy cause = throwableProxy.getCause();
      return cause != null ? redact(cause) : null;
   }

   @Nullable
   public IThrowableProxy[] getSuppressed() {
      final IThrowableProxy[] suppressed = throwableProxy.getSuppressed();

      if (suppressed != null && suppressed.length > 0) {
         final IThrowableProxy[] redactedArray = new  IThrowableProxy[suppressed.length];

         for (int i = 0; i < suppressed.length; i++) {
            redactedArray[i] = suppressed[i] != null ? redact(suppressed[i]) : null;
         }

         return redactedArray;
      } else {
         return suppressed;
      }
   }
}
