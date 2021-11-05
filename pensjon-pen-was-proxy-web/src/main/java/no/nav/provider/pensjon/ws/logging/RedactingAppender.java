package no.nav.provider.pensjon.ws.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;


public final class RedactingAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {
   @NotNull
   private final AppenderAttachableImpl<ILoggingEvent> appenderAttachable = new AppenderAttachableImpl<>();

   @Override
   public void append(@Nullable ILoggingEvent eventObject) {
      if (eventObject != null) {
         this.appenderAttachable.appendLoopOnAppenders(new RedactedLoggingEvent(eventObject));
      }
   }

   @Override
   public void addAppender(@Nullable Appender<ILoggingEvent> newAppender) {
      this.appenderAttachable.addAppender(newAppender);
   }

   @Override
   @Nullable
   public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
      return this.appenderAttachable.iteratorForAppenders();
   }

   @Override
   @NotNull
   public Appender<ILoggingEvent> getAppender(@Nullable String name) {
      return this.appenderAttachable.getAppender(name);
   }

   @Override
   public boolean isAttached(@Nullable Appender<ILoggingEvent> appender) {
      return this.appenderAttachable.isAttached(appender);
   }

   @Override
   public void detachAndStopAllAppenders() {
      this.appenderAttachable.detachAndStopAllAppenders();
   }

   @Override
   public boolean detachAppender(@Nullable Appender<ILoggingEvent> appender) {
      return this.appenderAttachable.detachAppender(appender);
   }

   @Override
   public boolean detachAppender(@Nullable String name) {
      return this.appenderAttachable.detachAppender(name);
   }
}
