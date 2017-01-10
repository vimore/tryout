package com.securityx.health.agent;

import com.securityx.health.agent.health.ServerHealthCheck;
import com.securityx.health.agent.resources.Track;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;

public class HealthAgentApplication extends Application<HealthAgentConfiguration> {

    public static void main(final String[] args) throws Exception {
        new HealthAgentApplication().run(args);
    }

    @Override
    public String getName() {
        return "SystemHealthService";
    }

    @Override
    public void initialize(final Bootstrap<HealthAgentConfiguration> bootstrap) {
        bootstrap.addBundle(new SundialBundle<HealthAgentConfiguration>() {
            @Override
            public SundialConfiguration getSundialConfiguration(HealthAgentConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });
    }

    @Override
    public void run(final HealthAgentConfiguration config,
                    final Environment environment) throws Exception {
        //parse the config and replace the items correctly.
        //WARNING: currently only works for CloudChamber Config
        config.fixUpCloudChamberConfig();

        environment.jersey().register(new Track(config, environment));
        environment.getApplicationContext().setAttribute(HealthAgentConfiguration.class.getName(), config);
        final ServerHealthCheck healthCheck = new ServerHealthCheck(config.getTemplate());
        environment.healthChecks().register("template", healthCheck);
    }

}
