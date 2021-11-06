package no.nav.provider.pensjon.ws.selftest;


import org.slf4j.Logger;

import javax.ejb.Singleton;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.StreamSupport;

import static java.lang.System.nanoTime;
import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toList;
import static no.nav.provider.pensjon.ws.selftest.Check.failure;
import static no.nav.provider.pensjon.ws.selftest.Check.success;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class SelftestServiceBean implements SelftestService {
    private final Logger logger = getLogger(getClass());

    @Any
    @Inject
    private Instance<Selftestable> selftestables;

    @Override
    public SelftestResult getResults() {
        final long start = nanoTime();

        final List<Check> checks = StreamSupport.stream(selftestables.spliterator(), false)
                .map(this::performSelftest)
                .collect(toList());

        return new SelftestResult("pensjon-pen-was-proxy", getApplicationVersion(), NANOSECONDS.toMillis(nanoTime() - start), now().toString(), checks);
    }

    private Check performSelftest(final Selftestable selftestable) {
        final long startCheck = nanoTime();
        try {
            return success(selftestable.getResourceType(), selftestable.getDescription(), selftestable.getEndpoint(), selftestable.performSelftest(), NANOSECONDS.toMillis(nanoTime() - startCheck));
        } catch (final RuntimeException e) {
            logger.info("Selftest failed", e);
            return failure(selftestable.getResourceType(), selftestable.getDescription(), selftestable.getEndpoint(), e, NANOSECONDS.toMillis(nanoTime() - startCheck));
        }
    }

    private String getApplicationVersion() {
        return ofNullable(getClass().getPackage().getImplementationVersion())
                .orElse("UNKNOWN VERSION");
    }
}
