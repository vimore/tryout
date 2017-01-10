package com.securityx.health.agent.health;

import com.codahale.metrics.health.HealthCheck;

public class ServerHealthCheck extends HealthCheck {
    private final String template;

    public ServerHealthCheck(String template) {
        this.template = template;
    }

    @Override
    protected HealthCheck.Result check() throws Exception {
        final String saying = String.format(template, "Health Agent");
        if (!saying.contains("Health Agent")) {
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }
}