package no.nav.provider.pensjon.ws.selftest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SelftestResult {
    private final String application;
    private final String version;
    private final String timestamp;
    private final long executionTime;
    private final boolean success;
    private final List<Check> checks;

    public SelftestResult(final String application, final String version, final long executionTime, final String timestamp, final List<Check> checks) {
        this.application = application;
        this.version = version;
        this.executionTime = executionTime;
        this.timestamp = timestamp;
        this.checks = new ArrayList<>(checks);
        this.success = checks.stream().allMatch(Check::isSuccess);
    }

    public String getApplication() {
        return application;
    }

    public String getVersion() {
        return version;
    }

    public Stream<Check> getChecks() {
        return checks.stream();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public boolean isSuccess() {
        return success;
    }
}
