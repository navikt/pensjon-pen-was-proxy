package no.nav.provider.pensjon.ws;

import net.logstash.logback.argument.StructuredArgument;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.slf4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getProperty;
import static java.lang.System.nanoTime;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static net.logstash.logback.argument.StructuredArguments.entries;
import static no.nav.provider.pensjon.ws.logging.RedactingUtils.redact;
import static org.slf4j.LoggerFactory.getLogger;

@WebServlet("/api/*")
public class PenProxyServlet extends ProxyServlet {
    public static final String TARGET_URI_PROPERTY = "pen.endpoint.nais.url";
    private static final Logger logger = getLogger(PenProxyServlet.class);
    private final String configuredTargetUri;

    public PenProxyServlet() {
        configuredTargetUri = ofNullable(getProperty(TARGET_URI_PROPERTY))
                .orElseThrow(() -> new RuntimeException("Missing '" + TARGET_URI_PROPERTY + "' system property"));
    }

    @Override
    protected String getConfigParam(String key) {
        if ("targetUri".equals(key)) {
            return configuredTargetUri;
        } else {
            return super.getConfigParam(key);
        }
    }

    @Override
    protected HttpResponse doExecute(HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpRequest proxyRequest) throws IOException {
        final long start = nanoTime();
        try {
            final HttpResponse response = getProxyClient().execute(this.getTargetHost(servletRequest), proxyRequest);
            logger.info("proxy {}", markers(servletRequest, proxyRequest, response, nanoTime() - start));
            return response;
        } catch (final RuntimeException | IOException e) {
            logger.error("proxy failed {}", markers(servletRequest, proxyRequest, nanoTime() - start), e);
            throw e;
        }
    }

    private StructuredArgument markers(HttpServletRequest servletRequest, HttpRequest proxyRequest, HttpResponse execute, long timeUsage) {
        final Map<String, Object> map = new HashMap<>();
        try {
            requestMarkers(servletRequest, proxyRequest, timeUsage, map);
            map.put("status", execute.getStatusLine().getStatusCode());
        } catch (Exception ignored) {
            // Ignore any exceptions creating markers
        }
        return entries(map);
    }

    private StructuredArgument markers(HttpServletRequest servletRequest, HttpRequest proxyRequest, long timeUsage) {
        final Map<String, Object> map = new HashMap<>();
        try {
            requestMarkers(servletRequest, proxyRequest, timeUsage, map);
        } catch (Exception ignored) {
            // Ignore any exceptions creating markers
        }
        return entries(map);
    }

    private void requestMarkers(HttpServletRequest servletRequest, HttpRequest proxyRequest, long timeUsage, Map<String, Object> map) throws URISyntaxException {
        map.put("method", servletRequest.getMethod());
        map.put("timeUsage", NANOSECONDS.toMillis(timeUsage));
        map.put("requestUri", redact(servletRequest.getRequestURI()));
        map.put("proxyUri", redact(proxyRequest.getRequestLine().getUri()));
        map.put("path", redact(new URI(proxyRequest.getRequestLine().getUri()).getPath()));
    }
}
