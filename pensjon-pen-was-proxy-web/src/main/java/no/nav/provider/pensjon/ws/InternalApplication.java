package no.nav.provider.pensjon.ws;

import no.nav.provider.pensjon.ws.selftest.SelftestResource;
import no.nav.provider.pensjon.ws.selftest.SelftestResultHtmlWriter;
import no.nav.provider.pensjon.ws.selftest.SelftestResultJsonWriter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@ApplicationPath("internal")
public class InternalApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(asList(
                SelftestResource.class,
                SelftestResultHtmlWriter.class,
                SelftestResultJsonWriter.class
        ));
    }
}
