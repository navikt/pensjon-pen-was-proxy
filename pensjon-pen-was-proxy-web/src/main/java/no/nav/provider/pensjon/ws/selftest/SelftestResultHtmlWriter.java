package no.nav.provider.pensjon.ws.selftest;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.LongAdder;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Provider
@Produces("text/html")
public class SelftestResultHtmlWriter implements MessageBodyWriter<SelftestResult> {
    private static final int HTML_LINK_MAX_ERROR_LENGTH = 100;

    @Override
    public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == SelftestResult.class;
    }

    @Override
    public long getSize(SelftestResult result, Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(SelftestResult result, Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        LongAdder position = new LongAdder();
        // Headers and stuff
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head><title>");
        sb.append(result.getApplication());
        sb.append(" selftest</title>");
        sb.append("<link rel=\"stylesheet\" href=\"../webjars/bootstrap/5.1.0/css/bootstrap.min.css\" />");
        sb.append("<script src=\"../webjars/bootstrap/5.1.0/js/bootstrap.min.js\"></script>");
        sb.append("<meta charset=\"UTF-8\" />");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div style=\"margin:1%;\">");

        // Aggregated information
        sb.append("<table class=\"table table-bordered table-responsive\"><tbody>");
        sb.append("<tr class=\"text-center ").append(contextualClass(result.isSuccess())).append(" \"><td colspan=\"4\">");
        sb.append(getAggregatedResultSpan(result.isSuccess()));
        sb.append("</td></tr>");

        sb.append("<tr><td><strong>Applikasjon</strong></td><td>");
        sb.append(result.getApplication());
        sb.append("</td>");
        sb.append("<td><strong>Generert</strong></td><td>");
        sb.append(result.getTimestamp());
        sb.append("</td></tr>");

        sb.append("<tr><td><strong>Versjon</strong></td><td>");
        sb.append(result.getVersion());
        sb.append("</td>");
        sb.append("<td><strong>Effektiv responstid</strong></td><td colspan=\"3\">");
        sb.append(result.getExecutionTime());
        sb.append(" ms</td>");
        sb.append("</tr>");

        sb.append("</tbody></table>");

        // Display results of all checks
        checkResultsTable(result, sb, position);

        sb.append("</div>");
        sb.append("</body></html>");

        outputStream.write(sb.toString().getBytes(UTF_8));
        outputStream.flush();
        outputStream.close();
    }

    private void checkResultsTable(SelftestResult result, StringBuilder sb, LongAdder position) {
        sb.append("<table class=\"table table-condensed table-hover table-responsive\">");
        sb.append("<thead><tr><th>Status</th><th>Type</th><th>Beskrivelse</th><th>Endpoint</th><th>Melding</th><th class=\"text-end\">Responstid</th></tr></thead>");
        sb.append("<tbody>");
        result.getChecks()
                .sorted(comparing(Check::isSuccess).thenComparing(Check::getDescription).reversed())
                .forEach(check -> {
                    position.increment();
                    sb.append("<tr>");
                    sb.append("<td class=\"").append(contextualClass(check.isSuccess())).append("\">");
                    sb.append(check.isSuccess() ? "OK" : "ERROR");
                    sb.append("</td>");
                    sb.append("<td>").append(check.getResourceType()).append("</td>");
                    sb.append("<td>").append(check.getDescription()).append("</td>");
                    sb.append("<td>").append(check.getEndpoint()).append("</td>");
                    sb.append("<td>");

                    if (check.isSuccess()) {
                        sb.append(escapeHtml4(check.getMessage()));
                    } else {
                        sb.append("<a data-bs-toggle=\"collapse\" href=\"#collapse").append(position).append("\">");
                        sb.append(escapeHtml4(abbreviate(check.getMessage(), HTML_LINK_MAX_ERROR_LENGTH)));
                        sb.append("</a>");
                        sb.append("<div id=\"collapse").append(position).append("\" class=\"collapse\">");
                        sb.append(escapeHtml4(check.getStacktrace()).replace("\n", "<br>"));
                        sb.append("</div>");
                    }

                    sb.append("</td>");
                    sb.append("<td class=\"text-end\">");
                    if (check.getResponseTime() != null) {
                        sb.append(check.getResponseTime()).append(" ms</td>");
                    }
                    sb.append("</tr>");
                });
        sb.append("</tbody>");
        sb.append("</table>");
    }

    private String getAggregatedResultSpan(final boolean success) {
        final String icon;
        final String color;

        if (success) {
            icon = checkLg();
            color = "green";
        } else {
            icon = xLg();
            color = "red";
        }

        return format("<div style=\"color: %s;\" aria-hidden=\"true\">%s</div>", color, icon);
    }

    /**
     * Check lg from Bootstrap Icons
     */
    private String checkLg() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"120\" height=\"120\" fill=\"currentColor\" class=\"bi bi-check-lg\" viewBox=\"0 0 16 16\">\n" +
                "  <path d=\"M12.736 3.97a.733.733 0 0 1 1.047 0c.286.289.29.756.01 1.05L7.88 12.01a.733.733 0 0 1-1.065.02L3.217 8.384a.757.757 0 0 1 0-1.06.733.733 0 0 1 1.047 0l3.052 3.093 5.4-6.425a.247.247 0 0 1 .02-.022Z\"/>\n" +
                "</svg>";
    }

    /**
     * X lg from Bootstrap Icons
     */
    private String xLg() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"120\" height=\"120\" fill=\"currentColor\" class=\"bi bi-x-lg\" viewBox=\"0 0 16 16\">\n" +
                "  <path fill-rule=\"evenodd\" d=\"M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z\"/>\n" +
                "  <path fill-rule=\"evenodd\" d=\"M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z\"/>\n" +
                "</svg>";
    }

    private String contextualClass(final boolean success) {
        return success ? "table-success" : "table-danger";
    }
}
