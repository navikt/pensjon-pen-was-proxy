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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.LongAdder;

import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Provider
@Produces("text/html")
public class SelftestResultHtmlWriter implements MessageBodyWriter<SelftestResult> {

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
        sb.append("<link rel=\"stylesheet\" href=\"../webjars/bootstrap-icons/1.7.0/font/bootstrap-icons.css\" />");
        sb.append("<script src=\"../webjars/bootstrap/5.1.0/js/bootstrap.min.js\"></script>");
        sb.append("<meta charset=\"UTF-8\" />");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div style=\"margin:1%;\">");

        // Aggregated information
        sb.append("<table class=\"table table-bordered table-responsive\"><tbody>");
        sb.append("<tr class=\"text-center ").append(contextualClass(result.getAggregateResult())).append(" \"><td colspan=\"4\">");
        sb.append(getAggregatedResultSpan(result.getAggregateResult()));
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
        sb.append("<table class=\"table table-condensed table-hover table-responsive\">");
        sb.append("<thead><tr><th>Status</th><th>Type</th><th>Beskrivelse</th><th>Endpoint</th><th>Melding</th><th class=\"text-end\">Responstid</th></tr></thead>");
        sb.append("<tbody>");
        result.getChecks()
            .sorted(comparing(Check::getResult).thenComparing(Check::getResult).reversed())
            .forEach(check -> {
                position.increment();
                sb.append("<tr>");
                sb.append("<td class=\"").append(contextualClass(check.getResult())).append("\">");
                sb.append(Check.isSuccess(check) ? "OK" : "ERROR");
                sb.append("</td>");
                sb.append("<td>").append(check.getResourceType()).append("</td>");
                sb.append("<td>").append(check.getDescription()).append("</td>");
                sb.append("<td>").append(check.getEndpoint()).append("</td>");
                sb.append("<td>");

                if (Check.isSuccess(check)) {
                    sb.append(escapeHtml4(check.getMessage()));
                } else {
                    sb.append("<a data-bs-toggle=\"collapse\" href=\"#collapse").append(position).append("\">");
                    sb.append(escapeHtml4(abbreviate(check.getMessage(), SelftestConstants.HTML_LINK_MAX_ERROR_LENGTH)));
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

        sb.append("</div>");
        sb.append("</body></html>");

        outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }

    private String getAggregatedResultSpan(Integer aggregateResult) {
        String icon;
        String color;

        if (aggregateResult.equals(SelftestConstants.SUCCESS)) {
            icon = "check";
            color = "green";
        } else {
            icon = "x";
            color = "red";
        }

        return String.format("<i class=\"bi-%s\" style=\"color: %s; font-size: 75px;\" aria-hidden=\"true\"/>", icon, color);
    }

    private String contextualClass(Integer result) {
        return result == SelftestConstants.SUCCESS ? "table-success" : "table-danger";
    }
}
