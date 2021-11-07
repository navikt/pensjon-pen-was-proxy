package no.nav.provider.pensjon.ws.selftest;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.concurrent.ExecutionException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

/**
 * See https://confluence.adeo.no/display/AURA/Selftest
 */
@Path("selftest")
@DeclareRoles("service")
@RunAs("service")
@Stateless
public class SelftestResource {
    @EJB
    private SelftestService selftestService;

    @GET
    @Produces({APPLICATION_JSON, TEXT_HTML})
    @PermitAll
    public SelftestResult selftest() throws ExecutionException, InterruptedException {
        return selftestService.getResults();
    }
}
