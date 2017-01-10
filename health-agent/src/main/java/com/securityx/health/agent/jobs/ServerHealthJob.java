package com.securityx.health.agent.jobs;

import com.securityx.health.agent.HealthAgentConfiguration;
import com.securityx.health.agent.util.SecurePushGateway;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.exporter.common.TextFormat;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static com.securityx.health.agent.util.InsecureHttpsURLConnection.setAcceptAllVerifier;

@SimpleTrigger(repeatInterval = 60, timeUnit = TimeUnit.SECONDS)
public class ServerHealthJob extends Job {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ServerHealthJob.class);
    public void doRun() throws JobInterruptException {
        try {
            LOGGER.info("Running the {}  job", ServerHealthJob.class.getName());
            initializeCxf();
            ServletContext ctx = SundialJobScheduler.getServletContext();
            HealthAgentConfiguration configuration = (HealthAgentConfiguration) ctx.getAttribute(HealthAgentConfiguration.class.getName());
            HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
            HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
            Proxy proxy = ClouderaClusterHealthJob.getPushGatewayProxy(configuration);
            //Client client = (Client) ctx.getAttribute(Client.class.getName());
            for(HealthAgentConfiguration.Host host : hosts){
                switch (host.getType()){
                    case "e8_api":
                        fetchAndPushApiMetricsToPrometheus(clusterInfo,
                                host,
                                ClouderaClusterHealthJob.getPushGateway(configuration,  ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY),
                                proxy);
                        break;
                    case "e8_ui":
                       fetchAndPushUIStatus(clusterInfo,
                               host,
                               ClouderaClusterHealthJob.getPushGateway(configuration,  ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY),
                               proxy);

                        break;
                }
            }
        } catch (Exception ex){
            LOGGER.error("failed to run job {}", ex.getMessage(), ex);
            JobInterruptException jie =  new JobInterruptException();
            jie.initCause(ex);
            throw jie;
        }
    }

    /*
  cat <<EOF | curl --data-binary @- http://pushgateway.example.org:9091/metrics/job/some_job/instance/some_instance
  # TYPE some_metric counter
  some_metric{label="val1"} 42
  # This one even has a timestamp (but beware, see below).
  some_metric{label="val2"} 34 1398355504000
  # TYPE another_metric gauge
  # HELP another_metric Just an example.
  another_metric 2398.283
  EOF
  */
    public static String fetchAndPushApiMetricsToPrometheus(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                                            HealthAgentConfiguration.Host apiHost,
                                                            HealthAgentConfiguration.Host prometheus,
                                                            Proxy proxy){

        String tag = clusterInfo.getTag()+":"+apiHost.getType();
        try {
            URI apiUri = new URI(apiHost.getScheme(), null, apiHost.getHost(), apiHost.getPort(), apiHost.getPath(), null, null);
            HttpURLConnection connection = (HttpURLConnection) apiUri.toURL().openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", MediaType.TEXT_PLAIN);
            connection.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(10 * SecurePushGateway.SECONDS_PER_MILLISECOND);
            connection.setReadTimeout(10 * SecurePushGateway.SECONDS_PER_MILLISECOND);
            connection.connect();

            LOGGER.debug("{} responded {} ", apiUri, connection.getResponseCode());

            if( connection.getResponseCode()!=200){
                LOGGER.error("Could not fetch API metrics data from {}. Status: {}",
                        apiUri,  connection.getResponseCode());
                if(prometheus!=null){
                    pushHealthCheckToPrometheus(prometheus, tag, apiHost.getType(), "BAD", 1, proxy);
                }
                saveHealthCheck(apiHost.getDirectory(), tag, apiHost.getType(), "BAD");
            }else{
                InputStream entity = connection.getInputStream();

                if(prometheus!=null){
                    pushMetricsToPrometheus(clusterInfo, apiHost, prometheus, entity, proxy);
                }

                if(prometheus!=null) {
                    pushHealthCheckToPrometheus(prometheus, tag, apiHost.getType(), "GOOD", 1, proxy);
                }
                saveHealthCheck(apiHost.getDirectory(), tag, apiHost.getType(), "GOOD");
            }
            return "{\"status\":\"success\", \"code\":200, \"message\":\"saved the metrics\"}";
        } catch (Exception ex){
            LOGGER.error("Could not fetch API metrics  {}", ex.getMessage(), ex);
            pushHealthCheckToPrometheus(prometheus, tag, apiHost.getType(), "BAD", -1, proxy);
            saveHealthCheck(apiHost.getDirectory(), tag, apiHost.getType(), "BAD");
            return String.format("{\"status\":\"error\", \"code\":500, \"message\":\"%s\"}", ex.getMessage());
        }
    }

    private static String pushMetricsToPrometheus(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                                  HealthAgentConfiguration.Host apiHost,
                                                  final HealthAgentConfiguration.Host prometheus,
                                                  InputStream entity,
                                                  Proxy proxy){
        String empty ="";
        if(prometheus == null){
            LOGGER.debug("Prometheus Push Gateway is null. Not sending");
            return empty;
        }
        try {
            String tag = clusterInfo.getTag()+":"+apiHost.getType();
            URI pgUri = new URI(prometheus.getScheme(), null,
                    prometheus.getHost(), prometheus.getPort(),
                    String.format("/metrics/job/%s/instance/%s", tag, apiHost.getHost()),
                    null, null);

            return SecurePushGateway.sendData(pgUri,
                    proxy,
                    entity,
                    TextFormat.CONTENT_TYPE_004,
                    "PUT",
                    apiHost.getHeaders());

        }catch(URISyntaxException ex){
            LOGGER.error("Could not Push API Metrics to Prometheus URI {} ", ex.getMessage(), ex);
            //ignore the exception
        }
        return empty;
    }
    private static void saveHealthCheck(String directory, String tag, String serviceName, String summpary){
        String fileName = directory+File.separator+ClouderaClusterHealthJob.SYSTEM_HEALTH_FILE;
        try{
            if(!Files.exists(Paths.get(directory))){
                Files.createDirectories(Paths.get(directory));
            }
            File file = new File(fileName);
            boolean fileExists = file.exists();
            FileOutputStream fos = new FileOutputStream(file, fileExists);

            if(!fileExists){
                fos.write("\"tag\",\"serviceName\",\"summary\",\"timestamp\"".getBytes(StandardCharsets.UTF_8));
                fos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
            String date = ClouderaClusterHealthJob.getDateString(ClouderaClusterHealthJob.DATE_FORMAT,System.currentTimeMillis());
            fos.write(String.format("%s,%s,%s,%s",
                    tag,
                    serviceName,
                    summpary,
                    date)
                    .getBytes(StandardCharsets.UTF_8));
            fos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }catch(Exception ex){
            LOGGER.error("Could not save metrics to file {}. Error: {}", fileName, ex.getMessage(), ex);
        }
    }


    private void initializeCxf() {
        final Bus defaultBus = BusFactory.getDefaultBus();
        final ConduitInitiatorManager extension = defaultBus.getExtension(ConduitInitiatorManager.class);
        extension.registerConduitInitiator("http://cxf.apache.org/transports/http", new HTTPTransportFactory());
    }

    static void pushHealthCheckToPrometheus(HealthAgentConfiguration.Host pushGateway,
                                            String tag, String serviceName, String summary, double val,
                                            Proxy proxy){
        if(pushGateway == null){
            LOGGER.debug("Proemetheus Push Gateway is null {} {} {}", tag, serviceName, summary);
            return;
        }
        try {
            CollectorRegistry registry = new CollectorRegistry();
            Gauge gauage = Gauge.build()
                    .namespace(tag)
                    .name(serviceName)
                    .help(String.format("Last time %s was recorded in unixtime.", serviceName))
                    .labelNames("serviceName", "summary", "timestamp", "value")
                    .register(registry);

            gauage.labels(serviceName,summary, Long.toString(System.currentTimeMillis()), Double.toString(val));
            LOGGER.debug("Gauge data {}", gauage.toString());
            // send the data
            URI uri = new URI(pushGateway.getScheme(),  null, pushGateway.getHost(), pushGateway.getPort(), null, null, null);
            PushGateway pg = new SecurePushGateway(uri,proxy);
            pg.pushAdd(registry, tag + ":" + serviceName);
        } catch (Exception ex) {
            LOGGER.error("Could not push metrics to push gateway {}. Error: {}", pushGateway.toString(), ex.getMessage(), ex);
        }
    }

    public static String fetchAndPushUIStatus(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                              HealthAgentConfiguration.Host uiHost,
                                              HealthAgentConfiguration.Host pushGateway,
                                              Proxy proxy){
        String tag = clusterInfo.getTag()+":"+uiHost.getType();
        try {
            URI uri = new URI(uiHost.getScheme(), null, uiHost.getHost(), uiHost.getPort(), null, null, null);
            HttpURLConnection connection;
            if(uiHost.getScheme().equalsIgnoreCase("https")) {
                connection = (HttpsURLConnection) uri.toURL().openConnection();
                //TURN OFF THE SSL CERTIFICATE CHECKING
                //TODO: WE NEED TO FIX OUR UI SERVER
                setAcceptAllVerifier((HttpsURLConnection)connection);
            }else{
                connection = (HttpURLConnection) uri.toURL().openConnection();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", MediaType.TEXT_HTML);
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(10 * SecurePushGateway.SECONDS_PER_MILLISECOND);
            connection.setReadTimeout(10 * SecurePushGateway.SECONDS_PER_MILLISECOND);
            connection.connect();

            String msg = connection.getResponseMessage();
            if(connection.getResponseCode() == 200){
                if(pushGateway!=null){
                    pushHealthCheckToPrometheus(pushGateway, tag, uiHost.getType(), "GOOD", 0, proxy);
                }
                saveHealthCheck(uiHost.getDirectory(), tag, uiHost.getType(), "GOOD");
                return String.format("{\"status\":\"success\", \"code\":%d, \"message\":\"%s\"}", connection.getResponseCode(), msg);
            }else{
                if(pushGateway!=null){
                    pushHealthCheckToPrometheus(pushGateway, tag, uiHost.getType(), "BAD", -1, proxy);
                }
                saveHealthCheck(uiHost.getDirectory(), tag, uiHost.getType(), "BAD");
                return String.format("{\"status\":\"success\", \"code\":%d, \"message\":\"%s\"}", connection.getResponseCode(), msg);
            }

        } catch (Exception ex){
            LOGGER.error("Could not fetch UI metrics  {}. Error: {}", uiHost.toString(), ex.getMessage(), ex);
            return String.format("{\"status\":\"error\", \"code\":500, \"message\":\"%s\"}", ex.getMessage());
        }
    }
}