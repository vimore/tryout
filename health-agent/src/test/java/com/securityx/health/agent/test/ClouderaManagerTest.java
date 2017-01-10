package com.securityx.health.agent.test;


import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.ApiCluster;
import com.cloudera.api.model.ApiRole;
import com.cloudera.api.model.ApiService;
import com.cloudera.api.model.ApiTimeSeriesRequest;
import com.cloudera.api.v1.RolesResource;
import com.cloudera.api.v11.ClustersResourceV11;
import com.cloudera.api.v11.RootResourceV11;
import com.cloudera.api.v11.ServicesResourceV11;
import com.cloudera.api.v11.TimeSeriesResourceV11;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.health.agent.HealthAgentConfiguration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UnknownFormatConversionException;

public class ClouderaManagerTest {
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private String confFile = System.getProperty("user.dir")+"/resources/config/dev_cfg.yml";

    private HealthAgentConfiguration configuration;

    @Before
    public void setup() throws Exception{
        configuration = new ConfigurationFactory<>(HealthAgentConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
    }
/*
    @Test
    public void testClusterList() throws Exception{
        String name = "cloudera-manager-test";
        // "http://admin:admin@10.10.80.91:7180/api/v1/clusters/"
        WebClient client = WebClient.create(new URI("http", "admin:admin", "10.10.80.91", 7180, "/api/v1/clusters/", null, null));
        Response response = client.header("Accept","application/json").get();

        assertEquals(200,response.getStatus());
        if(response.hasEntity()) {
            String data = response.readEntity(String.class);
            System.out.println(data);
        }
    }
*/
    //This needs a live ClouderaManger server. The mocked up one is very dumb. So commenting out now.
    @Ignore
    @Test
    public void testClusterList() throws Exception{
        RootResourceV11 apiRoot = new ClouderaManagerClientBuilder()
                .withHost("10.10.80.91")// CM of Cluster 1
                .withPort(7180)
                .withUsernamePassword("admin", "admin")
                .build()
                .getRootV11();
        ClustersResourceV11 clustersResource = apiRoot.getClustersResource();

        for (ApiCluster cluster : clustersResource.readClusters(DataView.FULL)) {
            System.out.println(cluster.getName());
            ServicesResourceV11 servicesResource = clustersResource.getServicesResource(cluster.getName());
            TimeSeriesResourceV11 timeSeriesResource = apiRoot.getTimeSeriesResource();

            for (ApiService service : servicesResource.readServices(DataView.FULL)) {
                String query = String.format("select mem_virtual  where serviceName = %s", service.getName());
                long current = System.currentTimeMillis();
                String to = getISO8601(current);
                long FIVE_MINUTES = 5*60*1000;
                String from = getISO8601(current-FIVE_MINUTES);
                //servicesResource.getMetrics()
                System.out.println(query);
                Response resp = timeSeriesResource.queryTimeSeries(new ApiTimeSeriesRequest(query, from, to, "text/csv", "RAW", true));
                System.out.println("\t" + service.getName());
                File file = File.createTempFile(service.getName(),  ".csv");
                FileOutputStream fos  = new FileOutputStream(file);
                System.out.println(file.getAbsolutePath());
                if(resp.hasEntity()){
                    Object obj = resp.getEntity();
                    if(obj instanceof InputStream) {
                        InputStream is = (InputStream) obj;
                        /*
                        1M = 1000000 bytes

                         */
                        byte[] bytes = new byte[1000000];
                        for(int length = is.read(bytes); length > 0;  length = is.read(bytes)){
                            fos.write(bytes, 0, length);
                        }
                        is.close();
                    }else {
                        throw new UnknownFormatConversionException("getEntity did not return an input stream");
                    }
                }
                fos.flush();
                fos.close();
                System.out.println("\t" + service.getName() );


                RolesResource rolesResource = servicesResource.getRolesResource(service.getName());
                for (ApiRole role : rolesResource.readRoles()) {
                    System.out.println("\t\t" + role.getName());
                }

            }
        }

    }
    private static String getISO8601(long millis){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = new Date();
        date.setTime(millis);
        return sdf.format(date);
    }
}
