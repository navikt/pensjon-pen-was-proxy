package no.nav.provider.pensjon.ws;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.slf4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.System.getProperty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

@WebServlet("/api/*")
public class PenProxyServlet extends ProxyServlet {
    public static final String TARGET_URI_PROPERTY = "pen.endpoint.nais.url";
    private static final Logger logger = getLogger(PenProxyServlet.class);
    private final String targetUri;

    public PenProxyServlet() {
        targetUri = ofNullable(getProperty(TARGET_URI_PROPERTY))
            .orElseThrow(() -> new RuntimeException("Missing '" + TARGET_URI_PROPERTY + "' system property"));
    }

    @Override
    protected String getConfigParam(String key) {
        if ("targetUri".equals(key)) {
            return targetUri;
        }
        return super.getConfigParam(key);
    }

    @Override
    protected HttpResponse doExecute(HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpRequest proxyRequest) throws IOException {
        logger.info("proxy {} uri: {} -- {}", servletRequest.getMethod(), servletRequest.getRequestURI(), proxyRequest.getRequestLine().getUri());

        return getProxyClient().execute(this.getTargetHost(servletRequest), proxyRequest);
    }
}
