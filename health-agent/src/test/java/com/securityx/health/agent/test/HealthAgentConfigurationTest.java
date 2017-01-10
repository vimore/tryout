package com.securityx.health.agent.test;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.securityx.health.agent.HealthAgentConfiguration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class HealthAgentConfigurationTest implements UnitTest {
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testDevConfiguration() throws Exception{
        String confFile = System.getProperty("user.dir")+"/resources/config/dev_cfg.yml";

        HealthAgentConfiguration configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
        assertNotNull(configuration);
    }

    @Test
    public void testProdConfiguration() throws Exception{
        String confFile = System.getProperty("user.dir")+"/resources/config/prod_cfg.yml";

        HealthAgentConfiguration configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
        assertNotNull(configuration);
    }

    @Test
    public void testKeysProdDev() throws Exception{
        String prodConfFile = System.getProperty("user.dir")+"/resources/config/prod_cfg.yml";
        String devConfFile = System.getProperty("user.dir")+"/resources/config/dev_cfg.yml";

        mapper.copy()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        YAMLFactory yamlFactory = new YAMLFactory();
        JsonNode prodJson = mapper.readTree(yamlFactory.createParser(new FileInputStream(prodConfFile)));
        JsonNode devJson = mapper.readTree(yamlFactory.createParser(new FileInputStream(devConfFile)));
        Comparator<JsonNode> comparator = (o1, o2) -> {/* This is only invoked if and only if the keys are equal */ return 0;};
        assertTrue(prodJson.equals(comparator, devJson));
    }

    @Test
    public void testFixConfiguration() throws Exception {
        String confFile = System.getProperty("user.dir") + "/resources/config/dev_cfg.yml";

        HealthAgentConfiguration configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
        assertNotNull(configuration);
        configuration.fixUpCloudChamberConfig();
        HealthAgentConfiguration.PushGateway pushGateway = configuration.getPushGateway();
        String proxyHost = pushGateway.getProxyHost();
        assertEquals("test.test.com", proxyHost);
        int proxyPort = pushGateway.getProxyPort();
        assertEquals(8080, proxyPort);
        String proxyUser = pushGateway.getProxyUser();
        assertEquals("testUser", proxyUser);
        String proxyPass = pushGateway.getProxyPass();
        assertEquals("testPass", proxyPass);
        HealthAgentConfiguration.Host[] hosts = pushGateway.getHosts();
        for(HealthAgentConfiguration.Host host: hosts){
            switch (host.getType()){
                case "cloud-chamber":
                    List<HashMap<String, String>> headers = host.getHeaders();
                    for(HashMap<String, String> map: headers){
                        String name = map.get("name");
                        String value = map.get("value");
                        switch (name) {
                            case "Authorization":
                                assertEquals("3a57d231604f567b029184e28d3944c50f6ce4a1", value);
                                break;
                            case "X-HMAC-Nonce":
                                assertEquals("1398322270633", value);
                                break;
                            default:
                                assertFalse("Unknown header name: " + name, true);
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    public void testDevConfigurationWithSubstitution() throws Exception{
        String confFile = System.getProperty("user.dir")+"/resources/config/dev_cfg.yml";

        HealthAgentConfiguration configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
        assertNotNull(configuration);
        HealthAgentConfiguration.PushGateway pushGateway = configuration.getPushGateway();
        String proxyHost = pushGateway.getProxyHost(configuration);
        assertEquals("test.test.com", proxyHost);
        int proxyPort = pushGateway.getProxyPort(configuration);
        assertEquals(8080, proxyPort);
        String proxyUser = pushGateway.getProxyUser(configuration);
        assertEquals("testUser", proxyUser);
        String proxyPass = pushGateway.getProxyPass(configuration);
        assertEquals("testPass", proxyPass);
        HealthAgentConfiguration.Host[] hosts = pushGateway.getHosts();
        for(HealthAgentConfiguration.Host host: hosts){
            switch (host.getType()){
                case "cloud-chamber":
                    List<HashMap<String, String>> headers = host.getHeaders();
                    for(HashMap<String, String> map: headers){
                        String name = map.get("name");
                        String value = map.get("value");
                        String actValue = HealthAgentConfiguration.getItem(configuration, value);
                        switch (name) {
                            case "Authorization":
                                assertEquals("3a57d231604f567b029184e28d3944c50f6ce4a1", actValue);
                                break;
                            case "X-HMAC-Nonce":
                                assertEquals("1398322270633", actValue);
                                break;
                            default:
                                assertFalse("Unknown header name: " + name, true);
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
