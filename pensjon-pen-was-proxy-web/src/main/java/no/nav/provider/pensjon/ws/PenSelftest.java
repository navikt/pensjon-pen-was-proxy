package no.nav.provider.pensjon.ws;

import no.nav.provider.pensjon.ws.selftest.Selftestable;

import javax.ejb.Singleton;
import javax.ws.rs.client.WebTarget;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static no.nav.provider.pensjon.ws.PenProxyServlet.TARGET_URI_PROPERTY;

@Singleton
public class PenSelftest implements Selftestable {
    private final String targetUri;
    private final WebTarget target;

    public PenSelftest() {
        targetUri = ofNullable(System.getProperty(TARGET_URI_PROPERTY))
            .orElseThrow(() -> new RuntimeException("Missing '" + TARGET_URI_PROPERTY + "' system property"));

        target = newClient()
            .target(targetUri);
    }

    @Override
    public String performSelftest() {
        target.request().get();
        return "OK";
    }

    @Override
    public String getResourceType() {
        return "REST";
    }

    @Override
    public String getDescription() {
        return "Pen nais";
    }

    @Override
    public String getEndpoint() {
        return targetUri;
    }}
