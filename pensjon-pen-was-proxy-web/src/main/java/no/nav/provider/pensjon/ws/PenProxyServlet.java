package no.nav.provider.pensjon.ws;

import net.logstash.logback.argument.StructuredArgument;
import org.apache.http.Header;
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
import java.util.Optional;

import static java.lang.System.getProperty;
import static java.lang.System.nanoTime;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Stream.of;
import static net.logstash.logback.argument.StructuredArguments.entries;
import static no.nav.provider.pensjon.ws.logging.RedactingUtils.redact;
import static org.slf4j.LoggerFactory.getLogger;

@WebServlet("/api/*")
public class PenProxyServlet extends ProxyServlet {
    public static final String TARGET_URI_PROPERTY = "pen.endpoint.nais.url";
    private static final Logger logger = getLogger(PenProxyServlet.class);
    public static final String NAV_CALL_ID = "Nav-Call-Id";
    public static final String X_CORRELATION_ID = "X-Correlation-ID";
    private final String configuredTargetUri;

    public PenProxyServlet() {
        configuredTargetUri = ofNullable(getProperty(TARGET_URI_PROPERTY))
                .orElseThrow(() -> new RuntimeException("Missing '" + TARGET_URI_PROPERTY + "' system property"));
    }

    @Override
    protected String getConfigParam(final String key) {
        if ("targetUri".equals(key)) {
            return configuredTargetUri;
        } else {
            return super.getConfigParam(key);
        }
    }

    @Override
    public void copyRequestHeaders(final HttpServletRequest servletRequest, final HttpRequest proxyRequest) {
        super.copyRequestHeaders(servletRequest, proxyRequest);

        final String correlationId = findCorrelationId(servletRequest);
        proxyRequest.setHeader(NAV_CALL_ID, correlationId);
        proxyRequest.setHeader(X_CORRELATION_ID, correlationId);
    }

    @Override
    protected HttpResponse doExecute(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final HttpRequest proxyRequest) throws IOException {
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

    private StructuredArgument markers(final HttpServletRequest servletRequest, final HttpRequest proxyRequest, final HttpResponse execute, final long timeUsage) {
        final Map<String, Object> map = new HashMap<>();
        try {
            requestMarkers(servletRequest, proxyRequest, timeUsage, map);
            map.put("status", execute.getStatusLine().getStatusCode());
        } catch (Exception ignored) {
            // Ignore any exceptions creating markers
        }
        return entries(map);
    }

    private StructuredArgument markers(final HttpServletRequest servletRequest, final HttpRequest proxyRequest, final long timeUsage) {
        final Map<String, Object> map = new HashMap<>();
        try {
            requestMarkers(servletRequest, proxyRequest, timeUsage, map);
        } catch (Exception ignored) {
            // Ignore any exceptions creating markers
        }
        return entries(map);
    }

    private void requestMarkers(final HttpServletRequest servletRequest, final HttpRequest proxyRequest, final long timeUsage, final Map<String, Object> map) throws URISyntaxException {
        ofNullable(proxyRequest.getFirstHeader(NAV_CALL_ID)).map(Header::getValue).ifPresent(it -> {
            map.put("correlationId", it);
            map.put("transaction", it);
        });
        map.put("method", servletRequest.getMethod());
        map.put("timeUsage", NANOSECONDS.toMillis(timeUsage));
        map.put("requestUri", redact(servletRequest.getRequestURI()));
        map.put("proxyUri", redact(proxyRequest.getRequestLine().getUri()));
        map.put("path", redact(new URI(proxyRequest.getRequestLine().getUri()).getPath()));
    }

    private String findCorrelationId(final HttpServletRequest request) {
        return of(ofNullable(request.getHeader(NAV_CALL_ID)), ofNullable(request.getHeader(X_CORRELATION_ID)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseGet(() -> randomUUID().toString());
    }
}
