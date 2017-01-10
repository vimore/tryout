package com.securityx.health.agent.test.jobs;


import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.health.agent.HealthAgentConfiguration;
import com.securityx.health.agent.jobs.ClouderaClusterHealthJob;
import com.securityx.health.agent.jobs.ServerHealthJob;
import com.securityx.health.agent.test.*;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthenticator;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class CheckClusterHealthJobTest {
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static HealthAgentConfiguration configuration;
    private static Environment environment;
    private static ClouderaClusterHealthJob job;
    private static MockPrometheusServer prometheusServer;
    private static MockCloudChamberServer cloudChamberServer;
    private static MockFeatureServiceServer featureServiceServer;
    private static MockUIServer uiServer;
    private static MockClouderaManagerServer clouderaManagerServer;
    private static HttpProxyServer proxyServer;
    @BeforeClass
    public static void setup() throws Exception{
        String confFile = System.getProperty("user.dir")+"/resources/config/test_cfg.yml";

        configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));

        configuration.fixUpCloudChamberConfig();

        environment = new Environment(CheckClusterHealthJobTest.class.getName(), mapper, validator, new MetricRegistry(), CheckClusterHealthJobTest.class.getClass().getClassLoader());

        job = new ClouderaClusterHealthJob();

        HealthAgentConfiguration.Host prometheusHost = ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY);
        prometheusServer = new MockPrometheusServer(prometheusHost.getHost(), prometheusHost.getPort());
        prometheusServer.start();

        HealthAgentConfiguration.Host cloudChamberHost = ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.CLOUD_CHAMBER_PUSH_GATEWAY);
        cloudChamberServer = new MockCloudChamberServer(cloudChamberHost.getHost(), cloudChamberHost.getPort());
        cloudChamberServer.start();

        HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
        HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
        //Client client = (Client) ctx.getAttribute(Client.class.getName());
        for(HealthAgentConfiguration.Host host : hosts){
            switch (host.getType()){
                case "e8_api":
                    featureServiceServer = new MockFeatureServiceServer(host.getHost(), host.getPort());
                    featureServiceServer.start();
                    break;
                case "e8_ui":
                    uiServer = new MockUIServer(host.getHost(), host.getPort());
                    uiServer.start();
                    break;
                case "e8_cloudera":
                    clouderaManagerServer = new MockClouderaManagerServer(host.getHost(), host.getPort());
                    clouderaManagerServer.start();
            }
        }
        proxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(configuration.getPushGateway().getProxyPort())
                .withProxyAuthenticator(new ProxyAuthenticator() {
                    @Override
                    public boolean authenticate(String userName, String password) {
                        return userName.equals("proxyUser") && password.equals("proxyPass");
                    }

                    @Override
                    public String getRealm() {
                        return null;
                    }
                }).start();
    }

    @AfterClass
    public static void teardown() throws Exception{
        prometheusServer.stop();
        cloudChamberServer.stop();
        featureServiceServer.stop();
        uiServer.stop();
        clouderaManagerServer.stop();
        proxyServer.stop();
    }
    @Ignore
    @Test
    public void testFetchAndSaveClouderaManagerMetrics() throws Exception{
        HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
        HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
        HealthAgentConfiguration.Host cmHost=null;
        for(HealthAgentConfiguration.Host host : hosts){
            if (host.getType().equals("e8_cloudera")){
                cmHost = host;
                break;
            }
        }
        assertNotNull(cmHost);

        Proxy proxy = ClouderaClusterHealthJob.getPushGatewayProxy(configuration);
        String cdhDir = ClouderaClusterHealthJob.fetchAndSaveClouderaManagerMetrics(clusterInfo, cmHost,
                ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY),
                proxy);

        assertNotNull(cdhDir);
        assertNotEquals(0, cdhDir.length());
        assertTrue(Files.exists(Paths.get(cdhDir)));
        assertTrue(Files.exists(Paths.get(cdhDir+"/Flume.csv")));
        assertTrue(Files.exists(Paths.get(cdhDir+"/HBase.csv")));
        assertTrue(Files.exists(Paths.get(cdhDir+"/HDFS.csv")));
        assertTrue(Files.exists(Paths.get(cdhDir+"/Impala.csv")));
        assertTrue(Files.exists(Paths.get(cdhDir+"/MapReduce.csv")));
        assertTrue(Files.exists(Paths.get(cdhDir+"/Solr.csv")));
        assertTrue(Files.exists(Paths.get(cdhDir+"/ZooKeeper.csv")));
    }
    @Test
    public void testFetchAndPushApiMetricsToPrometheus() throws Exception{
        HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
        Proxy proxy = ClouderaClusterHealthJob.getPushGatewayProxy(configuration);
        HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
        HealthAgentConfiguration.Host cmHost=null;
        for(HealthAgentConfiguration.Host host : hosts){
            if (host.getType().equals("e8_api")){
                cmHost = host;
                break;
            }
        }
        assertNotNull(cmHost);
        String msg = ServerHealthJob.fetchAndPushApiMetricsToPrometheus(clusterInfo, cmHost,
                ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY),
                proxy);
        JsonNode node = mapper.readTree(msg);
        assertEquals("success", node.get("status").asText());
    }

    @Test
    public void testFetchAndPushUIStatus() throws Exception{
        HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
        HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
        HealthAgentConfiguration.Host cmHost=null;
        for(HealthAgentConfiguration.Host host : hosts){
            if (host.getType().equals("e8_ui")){
                cmHost = host;
                break;
            }
        }

        Proxy proxy = ClouderaClusterHealthJob.getPushGatewayProxy(configuration);
        assertNotNull(cmHost);
        String msg = ServerHealthJob.fetchAndPushUIStatus(clusterInfo, cmHost,
                ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY),
                proxy);
        JsonNode node = mapper.readTree(msg);
        assertEquals("success", node.get("status").asText());
    }
}

