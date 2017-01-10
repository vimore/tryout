package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import io.dropwizard.Configuration;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.ValidationMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphiteConfiguration extends Configuration {

    public static final Logger log = LoggerFactory.getLogger(GraphiteConfiguration.class);

    @JsonProperty
    private boolean enabled;

    @JsonProperty
    private String host;

    @JsonProperty
    private int port;

    @JsonProperty
    private Duration outputPeriod = Duration.minutes(1);

    @ValidationMethod(message = "is not valid")
    public boolean isValid() {
        if (!enabled) {
            return true;
        }
        if (port <= 0) {
            log.error("Port must be > 0");
            return false;
        }
        if (Strings.isNullOrEmpty(host)) {
            log.error("Host may not be blank");
            return false;
        }
        if (outputPeriod == null) {
            log.error("Output period must be specified");
            return false;
        }
        if (outputPeriod.toSeconds() < 1) {
            log.error("Output period must be >= 1 seconds");
            return false;
        }
        return true;
    }

    public boolean isEnabled() {
                             return enabled;
                                                             }

    public String getHost() {
                          return host;
                                                    }

    public int getPort() {
                       return port;
                                              }

    public Duration getOutputPeriod() {
        return outputPeriod;
    }
}

