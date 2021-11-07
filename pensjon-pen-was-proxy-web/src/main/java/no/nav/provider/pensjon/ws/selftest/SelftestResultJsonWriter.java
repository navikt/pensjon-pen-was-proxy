package no.nav.provider.pensjon.ws.selftest;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.apache.commons.lang3.StringUtils.abbreviate;

@Provider
@Produces("application/json")
public class SelftestResultJsonWriter implements MessageBodyWriter<SelftestResult> {
    private static final int SUCCESS = 0;
    private static final int WARNING = 2;

    @Override
    public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == SelftestResult.class;
    }

    @Override
    public long getSize(SelftestResult result, Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(SelftestResult result, Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream) {
        final JsonGenerator gen = Json.createGeneratorFactory(null).createGenerator(outputStream);

        gen
                .writeStartObject()
                .write("application", result.getApplication())
                .write("version", result.getVersion())
                .write("timestamp", result.getTimestamp())
                .write("aggregateResult", result.isSuccess() ? SUCCESS : WARNING)
                .writeStartArray("checks");

        result.getChecks().forEach(check -> {
            gen
                    .writeStartObject()
                    .write("description", check.getDescription())
                    .write("endpoint", check.getEndpoint())
                    .write("responseTime", check.getResponseTime() == null ? "" : check.getResponseTime() + "ms")
                    .write("result", check.isSuccess() ? SUCCESS : WARNING);

            if (check.isFailure()) {
                gen
                        .write("errorMessage", abbreviate(check.getMessage(), 250))
                        .write("stacktrace", abbreviate(check.getStacktrace(), 250));
            }

            gen.writeEnd();
        });

        gen.writeEnd().writeEnd();
        gen.flush();
        gen.close();
    }
}
