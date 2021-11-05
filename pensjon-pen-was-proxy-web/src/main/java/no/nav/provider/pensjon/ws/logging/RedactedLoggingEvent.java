package no.nav.provider.pensjon.ws.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.Map;

import static no.nav.provider.pensjon.ws.logging.RedactingUtils.redactIfNotNUll;

public final class RedactedLoggingEvent implements ILoggingEvent {
   private final ILoggingEvent loggingEvent;

   public RedactedLoggingEvent(@NotNull ILoggingEvent loggingEvent) {
      this.loggingEvent = loggingEvent;
   }

   @Override
   public void prepareForDeferredProcessing() {
      loggingEvent.prepareForDeferredProcessing();
   }

   @Override
   @Nullable
   public String getThreadName() {
      return loggingEvent.getThreadName();
   }

   @Override
   @Nullable
   public Level getLevel() {
      return loggingEvent.getLevel();
   }

   @Override
   @Nullable
   public String getMessage() {
      return redactIfNotNUll(loggingEvent.getMessage());
   }

   @Override
   @Nullable
   public Object[] getArgumentArray() {
      return loggingEvent.getArgumentArray();
   }

   @Override
   @Nullable
   public String getFormattedMessage() {
      return redactIfNotNUll(loggingEvent.getFormattedMessage());
   }

   @Override
   @Nullable
   public String getLoggerName() {
      return loggingEvent.getLoggerName();
   }

   @Override
   @Nullable
   public LoggerContextVO getLoggerContextVO() {
      return loggingEvent.getLoggerContextVO();
   }

   @Override
   @Nullable
   public IThrowableProxy getThrowableProxy() {
      final IThrowableProxy throwableProxy = loggingEvent.getThrowableProxy();
      return throwableProxy != null ? RedactingUtils.redact(throwableProxy) : null;
   }

   @Override
   @Nullable
   public StackTraceElement[] getCallerData() {
      return loggingEvent.getCallerData();
   }

   @Override
   public boolean hasCallerData() {
      return loggingEvent.hasCallerData();
   }

   @Override
   @Nullable
   public Marker getMarker() {
      return loggingEvent.getMarker();
   }

   @Override
   @NotNull
   public Map<String, String>  getMDCPropertyMap() {
      Map<String, String> map = loggingEvent.getMDCPropertyMap();
      return (map != null) ? RedactingUtils.redact(map) : Collections.emptyMap();
   }

   @Override
   @NotNull
   @SuppressWarnings("deprecation")
   public Map<String, String> getMdc() {
      Map<String, String> mdc = loggingEvent.getMdc();
      return mdc != null ? RedactingUtils.redact(mdc) : Collections.emptyMap();
   }

   @Override
   public long getTimeStamp() {
      return loggingEvent.getTimeStamp();
   }
}
