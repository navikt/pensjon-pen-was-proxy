package no.nav.provider.pensjon.ws.selftest;

import javax.ejb.Local;
import java.util.concurrent.ExecutionException;

@Local
public interface SelftestService {
    SelftestResult getResults() throws ExecutionException, InterruptedException;
}
