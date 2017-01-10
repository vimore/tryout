package com.securityx.modelfeature.health;


import com.codahale.metrics.health.HealthCheck;

public class ModelFeatureServiceHealth extends HealthCheck {
    private final String name;
    public ModelFeatureServiceHealth(String name) {
        this.name = name;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
