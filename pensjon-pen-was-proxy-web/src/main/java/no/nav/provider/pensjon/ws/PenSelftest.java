package no.nav.provider.pensjon.ws;

import no.nav.provider.pensjon.ws.selftest.Selftestable;

import javax.ejb.Singleton;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static java.util.Optional.ofNullable;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static no.nav.provider.pensjon.ws.PenProxyServlet.TARGET_URI_PROPERTY;

@Singleton
public class PenSelftest implements Selftestable {
    private final String targetUri;
    private final WebTarget target;

    public PenSelftest() {
        targetUri = ofNullable(getProperty(TARGET_URI_PROPERTY))
            .orElseThrow(() -> new RuntimeException("Missing '" + TARGET_URI_PROPERTY + "' system property"));

        target = newClient()
            .target(targetUri)
                .path("/simuler/alderspensjon/v3/");
    }

    @Override
    public String performSelftest() {
        final Response response = target.request().get();
        if (response.getStatus() != 401) {
            throw new RuntimeException("Expected a 401 response from PEN. Got " + response.getStatus() + " message " + response.readEntity(String.class));
        } else {
            return "OK";
        }
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
