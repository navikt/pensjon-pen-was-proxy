package no.nav.provider.pensjon.ws.selftest;

import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/json")
public class SelftestResultJsonWriter implements MessageBodyWriter<SelftestResult> {

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
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);

        JsonGenerator gen = factory.createGenerator(outputStream);
        gen.writeStartObject()
                .write("application", result.getApplication())
                .write("version", result.getVersion())
                .write("timestamp", result.getTimestamp())
                .write("aggregateResult", result.getAggregateResult())
                .writeStartArray("checks");

        result.getChecks().forEach(check -> {
            gen.writeStartObject();
            gen.write("description", check.getDescription());
            gen.write("endpoint", check.getEndpoint());
            gen.write("responseTime", check.getResponseTime() == null ? "" : check.getResponseTime() + "ms");
            gen.write("result", check.getResult());
            if (Check.isFailure(check)) {
                gen.write("errorMessage", StringUtils.abbreviate(check.getMessage(), 250));
                gen.write("stacktrace", StringUtils.abbreviate(check.getStacktrace(), 250));
            }
            gen.writeEnd();
        });

        gen.writeEnd().writeEnd();
        gen.flush();
        gen.close();
    }
}
