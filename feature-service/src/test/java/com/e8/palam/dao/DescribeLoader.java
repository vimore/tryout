package com.e8.palam.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.config.ConfigurationConstants;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;

/**
 * Created by harish on 1/2/15.
 */
public class DescribeLoader {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private FeatureServiceConfiguration               conf           = null;
    private FeatureServiceCache                       cache          = null;
    private com.securityx.modelfeature.config.configloaders.DescribeLoader describeLoader = null;

    @Before
    public void setup() throws Exception{
        String confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml";
        conf = new ConfigurationFactory<FeatureServiceConfiguration>(FeatureServiceConfiguration.class, validator, mapper, "dw").build(new File(confFile));


        describeLoader = new com.securityx.modelfeature.config.configloaders.DescribeLoader();
        cache = new FeatureServiceCache(conf);
        ConfigurationConstants configurationConstants = new ConfigurationConstants();
        configurationConstants.setTimeSeriesFilePath("src/main/config/timeSeries.yml");
    }

    @Test
    public void test() {
        com.securityx.modelfeature.config.configloaders.DescribeLoader loader = new com.securityx.modelfeature.config.configloaders.DescribeLoader();
        loader.loadDescribe(conf);
    }
}
