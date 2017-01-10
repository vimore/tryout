package com.securityx.health.agent.test.resources;


import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.health.agent.HealthAgentConfiguration;
import com.securityx.health.agent.jobs.ClouderaClusterHealthJob;
import com.securityx.health.agent.resources.Track;
import com.securityx.health.agent.test.MockCloudChamberServer;
import com.securityx.health.agent.test.MockPrometheusServer;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthenticator;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.mockito.Mockito;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

public class TrackResourceTest {
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static HealthAgentConfiguration configuration;
    private static Environment environment;

    private static MockPrometheusServer prometheusServer;
    private static MockCloudChamberServer cloudChamberServer;
    private static HttpProxyServer proxyServer;
    private static class UsernamePasswordAuthenticatingProxy implements ProxyAuthenticator {
        String getUsername() {
            return "proxyUser";
        }

        String getPassword() {
            return "proxyPass";
        }

        @Override
        public boolean authenticate(String userName, String password) {
            return getUsername().equals(userName) && getPassword().equals(password);
        }

        @Override
        public String getRealm() {
            return null;
        }
    }
    @BeforeClass
    public static void setup() throws Exception{
        String confFile = System.getProperty("user.dir")+"/resources/config/test_cfg.yml";

         configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));

        configuration.fixUpCloudChamberConfig();

        environment = new Environment(TrackResourceTest.class.getName(), mapper, validator, new MetricRegistry(), TrackResourceTest.class.getClassLoader());
        HealthAgentConfiguration.Host prometheusHost = ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY);
        prometheusServer = new MockPrometheusServer(prometheusHost.getHost(), prometheusHost.getPort());
        prometheusServer.start();
        HealthAgentConfiguration.Host cloudChamberHost = ClouderaClusterHealthJob.getPushGateway(configuration, ClouderaClusterHealthJob.CLOUD_CHAMBER_PUSH_GATEWAY);
        cloudChamberServer = new MockCloudChamberServer(cloudChamberHost.getHost(), cloudChamberHost.getPort());
        cloudChamberServer.start();
        proxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(configuration.getPushGateway().getProxyPort())
                .withProxyAuthenticator(new UsernamePasswordAuthenticatingProxy())
                .start();
    }

    @AfterClass
    public static void teardown() throws Exception{
        if(prometheusServer!=null) prometheusServer.stop();
        if(cloudChamberServer!=null) cloudChamberServer.stop();
        if(proxyServer!=null) proxyServer.stop();
    }
    private HttpServletRequest getRequest(MyServletInputStream is) throws Exception{
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(is);
        when(request.getHeader(argThat(new Matcher<String>() {
            @Override
            public boolean matches(Object item) {
                return "Content-Type".equals(item);
            }

            @Override
            public void describeMismatch(Object item, Description mismatchDescription) {

            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

            }

            @Override
            public void describeTo(Description description) {

            }
        }))).thenReturn("application/json");
        return request;
    }
    //NOT THREAD SAFE. Only for testing
    private class MyServletInputStream extends ServletInputStream{
        byte[] data;
        int index;
        MyServletInputStream(byte[] b){
            this.data = b;
            this.index = 0;
        }
        @Override
        public int read(){
            if(index<data.length) {
                return data[index++];
            }else{
                return -1;
            }
        }

        @Override
        public boolean isFinished() {
            return index<data.length;
        }

        @Override
        public boolean isReady(){
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            //Unsupported
        }
    }

    @Test
    public void testLogparser() throws Exception{
        Track track = new Track(configuration, environment);
        HashMap<String, Object> input = new HashMap<>();
        input.put("TOTAL_PARSER_INPUT", 47071);
        input.put("TOTAL_PARSER_NULLS", 35988);
        input.put("TOTAL_PARSER_OUTPUT", 47071);
        input.put("format-per-host.UnMatched-10_10_4_2", 2);
        input.put("format-per-host.UnMatched-W2K8R2-SRC_e8sec_lab", 2326);
        input.put("format-per-host.UnMatched-fwmarine", 2326);
        input.put("format-per-host.UnMatched-security1",1);

        input.put("format-per-host.UnMatched-w2k12-srv_e8sec_lab",1);
        input.put("format-per-host.UnMatched-w2k8r2-AD_e8sec_lab",1);
        input.put("format-per-host.UnMatched-zywall-usg-200",1);
        input.put("format.BlueCoat",1);
        input.put("format.CertMef",1);
        input.put("format.DnsMef",1);
        input.put("format.FlowMef",1);
        input.put("format.HETMef",1);
        input.put("format.HETMef.startTime_2016-08-03T06",1);
        String inputJson = mapper.writeValueAsString(input);

        MyServletInputStream sis = new MyServletInputStream(inputJson.getBytes(StandardCharsets.UTF_8));

        Response resp = track.track("job_201605250911_198823", getRequest(sis));
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void testPushToPrometheus() throws Exception{
        //http://healthcheck.e8security.com/api/track?tag=plutus&server=?{server}&project=?{project}&flow=?{flow}&executionId=?{executionId}&job=?{job}&status=?{status}

        Track track = new Track(configuration, environment);
        for(int i=0; i<10;i++) {
            Response response = track.track("plutus", "srv"+i, "proj1", "flow1", "12345"+Integer.toString(i), "runModelA", "SUCCESS");
            assertEquals(200, response.getStatus());
            TimeUnit.SECONDS.sleep(1);
        }

        for(int i=0; i<10;i++) {
            Response response = track.track("plutus", "srv"+i, "proj1", "flow1", "12345"+Integer.toString(i), "runModelA", "FAILED");
            assertEquals(200, response.getStatus());
            TimeUnit.SECONDS.sleep(1);
        }

        for(int i=0; i<10;i++) {
            Response response = track.track("plutus", "srv"+i, "proj1", "flow1", "12345"+Integer.toString(i), "runModelA", "STARTED");
            assertEquals(200, response.getStatus());
            TimeUnit.SECONDS.sleep(1);
        }
        for(int i=0; i<10;i++) {
            Response response = track.track("plutus", "srv"+i, "proj1", "flow1", "12345"+Integer.toString(i), "runModelA", "COMPLETED");
            assertEquals(200, response.getStatus());
            TimeUnit.SECONDS.sleep(1);
        }
        // TODO add code to read the generated file and verify
        // TODO add code to read Prometheus and verify
    }

}
