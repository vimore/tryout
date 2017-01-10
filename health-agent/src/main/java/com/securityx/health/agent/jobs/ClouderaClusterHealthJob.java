package com.securityx.health.agent.jobs;

import com.cloudera.api.ClouderaManagerClientBuilder;
import com.cloudera.api.DataView;
import com.cloudera.api.model.*;
import com.cloudera.api.v1.RolesResource;
import com.cloudera.api.v11.ClustersResourceV11;
import com.cloudera.api.v11.RootResourceV11;
import com.cloudera.api.v11.ServicesResourceV11;
import com.cloudera.api.v11.TimeSeriesResourceV11;
import com.securityx.health.agent.HealthAgentConfiguration;
import com.securityx.health.agent.util.SecurePushGateway;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

//Run the job every 4 minutes to fetch the data
@SimpleTrigger(repeatInterval = 2, timeUnit = TimeUnit.MINUTES)
public class ClouderaClusterHealthJob extends Job {


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ClouderaClusterHealthJob.class);
    static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final long MINUTES = 2 * 60 * 1000;
    private static final CharSequence[] SOLR_METRICS = new CharSequence[]{
            "query_avg_time_per_request_across_solr_replicas",
            "default_errors_rate_across_solr_replicas",
            "total_events_important_rate_across_solr_servers",
            "total_alerts_rate_across_solr_servers",
            "total_rollbacks_rate_across_solr_replicas",
            "total_write_bytes_rate_across_solr_servers",
            "total_health_unknown_rate_across_solr_servers",
            "total_health_good_rate_across_solr_servers",
            "total_health_bad_rate_across_solr_servers",
            "total_health_concerning_rate_across_solr_servers"
    };
    private static final CharSequence[] HBASE_METRICS = new CharSequence[] {
            "total_health_unknown_rate_across_regionservers",
            "total_health_unknown_rate_across_hbasethriftservers",
            "total_health_unknown_rate_across_masters",
            "total_health_concerning_rate_across_regionservers",
            "total_health_concerning_rate_across_hbasethriftservers",
            "total_health_concerning_rate_across_masters",
            "total_health_good_rate_across_regionservers",
            "total_health_good_rate_across_hbasethriftservers",
            "total_health_good_rate_across_masters"
    };
    public static final String PROMETHEUS_PUSH_GATEWAY = "prometheus";
    public static final String CSV_PUSH_GATEWAY = "csv";
    public static final String CLOUD_CHAMBER_PUSH_GATEWAY = "cloud-chamber";
    public static final String SYSTEM_HEALTH_FILE = "SystemHealth.csv";
    @Override
    public void doRun() throws JobInterruptException {
        try {
            LOGGER.info("Running the {}  job", ClouderaClusterHealthJob.class.getName());
            //initializeCxf();
            ServletContext ctx = SundialJobScheduler.getServletContext();
            HealthAgentConfiguration configuration = (HealthAgentConfiguration) ctx.getAttribute(HealthAgentConfiguration.class.getName());
            HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
            HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
            for(HealthAgentConfiguration.Host host : hosts){
                switch (host.getType()){
                    case "e8_cloudera":
                       fetchAndSaveClouderaManagerMetrics(clusterInfo, host, getPushGateway(configuration, PROMETHEUS_PUSH_GATEWAY), getPushGatewayProxy(configuration));
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

    /**
     *
     * @param host
     * @return the directory name where the metrics are stored.
     * @throws Exception
     */
    public static String fetchAndSaveClouderaManagerMetrics(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                                            HealthAgentConfiguration.Host host,
                                                            HealthAgentConfiguration.Host prometheusPushGateway,
                                                            Proxy proxy)
            throws Exception{

        LOGGER.info("fetching the metrics information {}",host.toString());
        RootResourceV11 apiRoot = new ClouderaManagerClientBuilder()
                .withHost(host.getHost())// CM of Cluster 1
                .withPort(host.getPort())
                .withUsernamePassword(host.getUser(), host.getPassword())
                .build()
                .getRootV11();
        ClustersResourceV11 clustersResource = apiRoot.getClustersResource();
        String dir = host.getDirectory();

        if(!Files.exists(Paths.get(dir)))
            Files.createDirectories(Paths.get(dir));

        for (ApiCluster cluster : clustersResource.readClusters(DataView.FULL)) {
            LOGGER.debug("Cluster Name: {}", cluster.getName());
            ServicesResourceV11 servicesResource = clustersResource.getServicesResource(cluster.getName());
            TimeSeriesResourceV11 timeSeriesResource = apiRoot.getTimeSeriesResource();

            for (ApiService service : servicesResource.readServices(DataView.FULL)) {
                String query = String.format("select * where serviceName = %s", service.getName());

                if(service.getDisplayName().contains("Solr")){
                    query = String.format("select %s where serviceName = %s", String.join(",", SOLR_METRICS), service.getName());
                }
                long current = System.currentTimeMillis();
                String to = getDateString(DATE_FORMAT, current);
                String from = getDateString(DATE_FORMAT, current - MINUTES);
                pushHealthCheck(prometheusPushGateway, service.getHealthChecks(),  clusterInfo.getTag()+":"+host.getType(), service.getName(), proxy);
                saveHealthCheck(service.getHealthChecks(),  clusterInfo.getTag()+":"+host.getType(), service.getName(), host.getDirectory());
                //servicesResource.getMetrics();
                LOGGER.debug("Cluster Name: {}, Service Name: {}, Query: {}", cluster.getName(), service.getName(), query);
                Response resp = timeSeriesResource.queryTimeSeries(new ApiTimeSeriesRequest(query, from, to, "text/csv", "RAW", true));
                LOGGER.debug("Cluster Name: {}, Service Name: {}, Service URL: {}", cluster.getName(), service.getDisplayName(), service.getServiceUrl());
                if (resp.hasEntity()) {
                    Object obj = resp.getEntity();
                    if (obj instanceof InputStream) {
                        InputStream is = (InputStream) obj;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String fileName = dir+service.getDisplayName()+".csv";
                        LOGGER.debug("Cluster Name: {}, Service Name: {}, Opening File: {}", cluster.getName(), service.getDisplayName(), fileName);
                        File file = new File(fileName);
                        boolean fileExists = file.exists();
                        FileOutputStream fos = new FileOutputStream(file, fileExists);
                        // assumes that the first line in the CSV file is the header
                        boolean isHeader = true;
                        String line, headerLine=null;
                        while((line = reader.readLine())!=null){
                            if(isHeader){
                                isHeader = false;
                                headerLine = line;
                                // Don't write the header if the file exists
                                if(fileExists){
                                    continue;
                                }
                            }
                            fos.write(line.getBytes(StandardCharsets.UTF_8));
                            fos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                            // we need to push every line to Prometheus
                            pushCdhMetricsToPrometheus(prometheusPushGateway, clusterInfo.getTag()+":"+host.getType(), service.getName(), headerLine, line, proxy);

                        }
                        reader.close();
                        fos.flush();
                        fos.close();
                    } else {
                        throw new UnknownFormatConversionException("getEntity did not return an input stream");
                    }
                }
                RolesResource rolesResource = servicesResource.getRolesResource(service.getName());
                for (ApiRole role : rolesResource.readRoles()) {
                    LOGGER.debug("Cluster Name: {}, Service Name: {},  Role Name: {}", cluster.getName(), service.getName(), role.getName());
                }
            }
        }
        return dir;
    }

    public static void pushHealthCheck(HealthAgentConfiguration.Host pushGateway,
                                       List<ApiHealthCheck> healthChecks,
                                       String tag,
                                       String serviceName,
                                       Proxy proxy){
        if(pushGateway == null){
            LOGGER.debug("Push Gateway is null {} {}", tag, serviceName);
            return;
        }
        for(ApiHealthCheck healthCheck : healthChecks){
            String summary =  healthCheck.getSummary().toString();
            double val;
            switch (summary){
                case "GOOD":
                    val=1;
                    break;
                case "BAD":
                    val=-1;
                    break;
                case "UNKNOWN":
                    val = -2;
                    break;
                default:
                    val = 0;
                    break;
            }
            ServerHealthJob.pushHealthCheckToPrometheus(pushGateway, tag, serviceName, summary, val, proxy);
        }
    }

    public static void saveHealthCheck(List<ApiHealthCheck> healthChecks,
                                       String tag,
                                       String serviceName,
                                       String directory) throws Exception{

        String fileName = directory+ File.separator+ClouderaClusterHealthJob.SYSTEM_HEALTH_FILE;
        try {
            File file = new File(fileName);
            boolean fileExists = file.exists();
            FileOutputStream fos = new FileOutputStream(file, fileExists);

            if(!fileExists){
                fos.write("\"tag\",\"serviceName\",\"summary\",\"timestamp\"".getBytes(StandardCharsets.UTF_8));
                fos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
            String date = ClouderaClusterHealthJob.getDateString(ClouderaClusterHealthJob.DATE_FORMAT,System.currentTimeMillis());
            for(ApiHealthCheck healthCheck : healthChecks) {
                fos.write(String.format("%s,%s,%s,%s",
                        tag,
                        serviceName,
                        healthCheck.getSummary().toString(),
                        date)
                        .getBytes(StandardCharsets.UTF_8));
                fos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }

            fos.flush();
            fos.close();
        }catch (IOException ex){
            LOGGER.error("Could not save metrics to file {}. Error: {}", fileName, ex.getMessage(), ex);
            throw ex;
        }
    }
    private static void pushCdhMetricsToPrometheus(HealthAgentConfiguration.Host pushGateway,
                                                   String tag,
                                                   String serviceName,
                                                   String header,
                                                   String data,
                                                   Proxy proxy){
        if(pushGateway==null){
            LOGGER.debug("Prometheus PushGateway is null. ");
            return;
        }
        if(header==null){
            LOGGER.error("Cannot push metrics. Header line cannot be null");
        }else {
            try {
                CollectorRegistry registry = new CollectorRegistry();
                Gauge gauage = Gauge.build()
                        .namespace(tag)
                        .name(serviceName)
                        .help(String.format("Last time %s was recorded in unixtime.", serviceName))
                        .labelNames(header.split(","))
                        .register(registry);
                gauage.labels(data.split(","));
                // send the data
                URI uri = new URI(pushGateway.getScheme(), null, pushGateway.getHost(),pushGateway.getPort(), null, null, null);
                PushGateway pg = new SecurePushGateway(uri, proxy);
                pg.pushAdd(registry, tag + ":" + serviceName);
            } catch (Exception ex) {
                LOGGER.error("Could not push metrics to push gateway {}. Error: {}", pushGateway.toString(), ex.getMessage(), ex);
            }
        }
    }

    public static String getDateString( String format, long millis){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        date.setTime(millis);
        return sdf.format(date);
    }
    public static HealthAgentConfiguration.Host getPushGateway(HealthAgentConfiguration configuration, String type)
            throws ConfigurationException {
        HealthAgentConfiguration.Host[] hpg = configuration.getPushGateway().getHosts();
        if(hpg==null || hpg.length==0){
            throw new ConfigurationException(String.format("Push Gateway  configuration incorrect for %s", type));
        }
        HealthAgentConfiguration.Host host = null;
        for(HealthAgentConfiguration.Host h : hpg) {
            if (h.getType().equals(type)) {
                host = h;
                break;
            }
        }
        if(host!=null) {
            String proxyUser = configuration.getPushGateway().getProxyUser();
            String proxyPass = configuration.getPushGateway().getProxyPass();
            HashMap<String, String> map = new HashMap<>();
            if (proxyUser != null && !proxyUser.isEmpty() && proxyPass!=null && !proxyPass.isEmpty()) {
                String cred = proxyUser + ":" + proxyPass;
                String auth = Base64.getEncoder().encodeToString(cred.getBytes(StandardCharsets.UTF_8));
                map.put("name","Proxy-Authorization");
                map.put("value", "Basic " + auth);
            }
            List<HashMap<String, String>> headers = host.getHeaders();
            if(!map.isEmpty()){
                headers.add(map);
            }
        }
        return host;
    }
    public static Proxy getPushGatewayProxy(HealthAgentConfiguration configuration){
        HealthAgentConfiguration.PushGateway pushGateway = configuration.getPushGateway();

        return SecurePushGateway.getProxy(pushGateway.getProxyHost(),
                pushGateway.getProxyPort(),
                pushGateway.getProxyUser(),
                pushGateway.getProxyPass());
    }
}
