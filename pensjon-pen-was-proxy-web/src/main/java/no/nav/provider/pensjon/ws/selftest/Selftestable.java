package no.nav.provider.pensjon.ws.selftest;

public interface Selftestable {
    String performSelftest();

    String getResourceType();

    String getDescription();

    String getEndpoint();
}
