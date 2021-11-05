package no.nav.provider.pensjon.ws.selftest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static no.nav.provider.pensjon.ws.selftest.SelftestConstants.SUCCESS;
import static no.nav.provider.pensjon.ws.selftest.SelftestConstants.WARNING;

public class Check {
    private final String description;
    private final String endpoint;
    private final String message;
    private final String stacktrace;
    private final Integer result;
    private final Long responseTime;
    private final String resourceType;

    public Check(String resourceType, String description, String endpoint, String message, String stacktrace, Integer result,
                 Long responseTime) {
        this.resourceType = resourceType;
        this.description = description;
        this.endpoint = endpoint;
        this.message = message;
        this.stacktrace = stacktrace;
        this.result = result;
        this.responseTime = responseTime;
    }

    public static Check success(String resourceType, String description, String endpoint, String message, long responseTime) {
        return new Check(resourceType, description, endpoint, message, null, SUCCESS, responseTime);
    }

    public static Check failure(String resourceType, String description, String endpoint, RuntimeException e, long responseTime) {
        return new Check(resourceType, description, endpoint, e.getMessage(), getStackTraceString(e), WARNING, responseTime);
    }

    public String getDescription() {
        return description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getMessage() {
        return message;
    }

	public String getStacktrace() {
		return stacktrace;
	}

	public Integer getResult() {
		return result;
	}

	public Long getResponseTime() {
		return responseTime;
	}

    public String getResourceType() {
        return resourceType;
    }

    public static boolean isSuccess(Check check) {
        return check.result == SUCCESS;
    }

    public static boolean isFailure(Check check) {
        return check.result != SUCCESS;
    }

    private static String getStackTraceString(final RuntimeException e) {
        try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ioException) {
            return "";
        }
    }
}
