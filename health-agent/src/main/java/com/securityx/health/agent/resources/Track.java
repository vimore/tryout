package com.securityx.health.agent.resources;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.securityx.health.agent.HealthAgentConfiguration;
import com.securityx.health.agent.jobs.ClouderaClusterHealthJob;
import com.securityx.health.agent.util.CsvReporter;
import com.securityx.health.agent.util.Event;
import com.securityx.health.agent.util.SecurePushGateway;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.securityx.health.agent.jobs.ClouderaClusterHealthJob.CLOUD_CHAMBER_PUSH_GATEWAY;
import static com.securityx.health.agent.jobs.ClouderaClusterHealthJob.PROMETHEUS_PUSH_GATEWAY;

@Path("/track")
@Produces(MediaType.APPLICATION_JSON)
public class Track {

    private static final Logger LOGGER = LoggerFactory.getLogger(Track.class);

    private HealthAgentConfiguration configuration;
    private Environment environment;
    private final MetricRegistry metrics = new MetricRegistry();
    private CsvReporter reporter;
    private File directory;
    private ObjectMapper objectMapper;
    public Track(HealthAgentConfiguration configuration, Environment environment) throws IOException {
        this.configuration = configuration;
        this.environment = environment;
        objectMapper = environment.getObjectMapper();

        directory = new File(configuration.getClusterInfo().getTrackDirectory());
        if(!directory.exists()){
            Files.createDirectories(Paths.get(directory.toString()));
        }
        this.reporter = CsvReporter.forRegistry(metrics)
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.MINUTES)
                .convertDurationsTo(TimeUnit.MINUTES)
                .build(directory);
    }

    @POST
    @Path("/logparser")
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response track(@QueryParam("name") String name, @Context HttpServletRequest request) throws WebApplicationException{
        try{
            HashMap<String, String> map = objectMapper.readValue(request.getInputStream(), new TypeReference<HashMap<String, String>>() {
            });
            Event event = new Event(map);
            // save metrics locally
            reporter.reportEvent(System.currentTimeMillis(), name, event);
            HealthAgentConfiguration.Host[] hpg = configuration.getPushGateway().getHosts();
            Proxy proxy = ClouderaClusterHealthJob.getPushGatewayProxy(configuration);
            Response resp = Response
                                .status(Response.Status.OK)
                                .entity(getMessage(Response.Status.OK, "saved data the request"))
                                .build();
            for(HealthAgentConfiguration.Host host : hpg) {
                switch (host.getType()) {
                    case PROMETHEUS_PUSH_GATEWAY:
                        //TODO: RAMV - not implemented yet.
                        LOGGER.error("Prometheus is current unsupported in logparser endpoint");
                        break;
                    case CLOUD_CHAMBER_PUSH_GATEWAY:
                        resp = pushToCloudChamber(directory, name, host, proxy);
                        break;
                    default:
                        LOGGER.error("Unknown Host Configuration: " + host.toString());
                        break;
                }
            }
            return resp;
        }catch (IOException ex){
            LOGGER.error("Could not read the input data. Error: {}", ex.getMessage(), ex);

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(getMessage(Response.Status.BAD_REQUEST, ex.getMessage()))
                    .build();
        }

    }

    private String getMessage(Response.Status status, String message){
        try {
            HashMap<String, Object> msgData = new HashMap<>();
            if(status.getStatusCode()!= 200) {
                msgData.put("status", "error");
            }else{
                msgData.put("status", "success");
            }
            msgData.put("code", status.getStatusCode());
            msgData.put("reason", status.getReasonPhrase());
            msgData.put("description", message);
            return objectMapper.writeValueAsString(msgData);
        }catch (Exception ex){
            LOGGER.error("Could not create the response string. Error: {}", ex.getMessage(), ex);
        }
        return "";
    }
    /*
job.notification.started.1.url=http://healthcheck.e8security.com/api/track?tag=plutus&server=?{server}&project=?{project}&flow=?{flow}&executionId=?{executionId}&job=?{job}&status=?{status}
job.notification.started.1.method=GET
job.notification.started.1.headers=Accept:application/json

job.notification.completed.1.url=http://healthcheck.e8security.com/api/track?tag=plutus&server=?{server}&project=?{project}&flow=?{flow}&executionId=?{executionId}&job=?{job}&status=?{status}
job.notification.completed.1.method=GET
job.notification.completed.1.headers=Accept:application/json

job.notification.success.1.url=http://healthcheck.e8security.com/api/track?tag=plutus&server=?{server}&project=?{project}&flow=?{flow}&executionId=?{executionId}&job=?{job}&status=?{status}
job.notification.success.1.method=GET
job.notification.success.1.headers=Accept:application/json

job.notification.failure.1.url=http://healthcheck.e8security.com/api/track?tag=plutus&server=?{server}&project=?{project}&flow=?{flow}&executionId=?{executionId}&job=?{job}&status=?{status}
job.notification.failure.1.method=GET
job.notification.failure.1.headers=Accept:application/json

     */
    @GET
    @Path("/azkaban")
    @Timed
    public Response track(@QueryParam("tag") String tag,
                          @QueryParam("server") String server,
                          @QueryParam("project") String project,
                          @QueryParam("flow") String flow,
                          @QueryParam("executionId") String executionId,
                          @QueryParam("job") String job,
                          @QueryParam("status") String status) throws WebApplicationException{
        //TODO: figure out how to push to multiple hosts
        String name = String.format("%s_%s_%s", project, flow, job);
        String gauageName = String.format("%s_%s_unixtime", name, status);
        HealthAgentConfiguration.Host[] hpg = configuration.getPushGateway().getHosts();
        if(hpg==null || hpg.length==0){
            throw new WebApplicationException("PushGateway configuration incorrect");
        }
        //Always save the metrics locally
        Response resp = saveToCsv(tag, server, project, flow, executionId, job, status, name, gauageName);
        if (resp.getStatus() != 200) {
            String err = resp.readEntity(String.class);
            LOGGER.error("Could not save message to CSV: {}", err);
        }
        Proxy proxy = ClouderaClusterHealthJob.getPushGatewayProxy(configuration);
        // try and push metrics to CloudChamber and Prometheus
        for(HealthAgentConfiguration.Host host : hpg) {
            switch (host.getType()) {
                case "prometheus":
                    resp = pushToPrometehus(tag, server, project, flow, executionId, job, status, name, gauageName, host);
                    break;
                case "cloud-chamber":
                    resp = pushToCloudChamber(directory, name, host, proxy);
                    break;
                default:
                    LOGGER.error("Unknown Host Configuration: " + host.toString());
                    break;
            }
            if(resp.getStatus()!=Response.Status.ACCEPTED.getStatusCode()
                    || resp.getStatus()!=Response.Status.OK.getStatusCode()){
                break;
            }
        }
        return resp;
    }
    private Response saveToCsv(String tag,
                               String server,
                               String project,
                               String flow,
                               String executionId,
                               String job,
                               String status,
                               String name,
                               String gaugeName){
        /*
        SortedMap<String,Meter> meters = metrics.getMeters();
        Meter meter;
        if(meters.containsKey(gaugeName)){
            meter = meters.get(gaugeName);
        }else{
            meter = new Meter();
            metrics.register(gaugeName, meter);
        }
        meter.mark();
        reporter.report();*/
        HashMap<String, String> map = new HashMap<>();
        map.put("tag", tag);
        map.put("server", server);
        map.put("project", project);
        map.put("flow", flow);
        map.put("executionId", executionId);
        map.put("job", job);
        map.put("status", status);
        Event event = new Event(map);
        reporter.reportEvent(System.currentTimeMillis(), name, event);
        return Response
                .ok(getMessage(Response.Status.OK, "Saved gauge: "+gaugeName))
                .build();

    }

    @VisibleForTesting
    private Response pushToPrometehus(String tag,
                                      String server,
                                      String project,
                                      String flow,
                                      String executionId,
                                      String job,
                                      String status,
                                      String name,
                                      String gaugeName,
                                      HealthAgentConfiguration.Host host){


        try {
            LOGGER.debug("Sending metrics for job {} - {} - {}", tag, name, gaugeName);
            CollectorRegistry registry = new CollectorRegistry();
            Gauge gauage = Gauge.build()
                    .namespace(tag)
                    .name(gaugeName)
                    .help(String.format("Last time %s was recorded in unixtime.", gaugeName))
                    .labelNames("timestamp","tag", "server", "project", "flow", "job", "status", "executionId", "value")
                    .register(registry);
            //gauage.setToCurrentTime();
            String value;
            switch (status){
                case "STARTED":
                    value = "1";
                    break;
                case "COMPLETED":
                    value = "2";
                    break;
                case "FAILED":
                    value = "3";
                    break;
                case "SUCCESS":
                    value = "4";
                    break;
                default:
                    value = "-1";
                    break;
            }

            gauage.labels(Long.toString(System.currentTimeMillis()),tag, server, project, flow, job, status, executionId,value);
            // send the data
            PushGateway pg = new PushGateway(host.getHost()+":"+host.getPort());
            pg.pushAdd(registry, name);

        }catch (Exception ex){
            // internal server error
            //throw new WebApplicationException( getMessage(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage()), ex);
            LOGGER.error("Could not push metrics to Prometheus. {} - {} - {}. Error: {}", tag, name, gaugeName, ex.getMessage(), ex);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(getMessage(Response.Status.INTERNAL_SERVER_ERROR, "Failed to send metrics to prometheus"))
                    .build();
        }
        return Response
                .ok()
                .entity(getMessage(Response.Status.OK, "pushed gauge: "+gaugeName))
                .build();
    }

    private Response pushToCloudChamber(File directory, String name, HealthAgentConfiguration.Host host, Proxy proxy){
        final File file = new File(directory, name + ".csv");
        if(file.exists()){
            try {
                URI uri = new URI(host.scheme,
                        null,
                        host.getHost(),
                        host.getPort(),
                        host.getPath(),
                        String.format(host.getQuery(),
                                    configuration.getClusterInfo().getTag()+"/"+directory.getName(),
                                    name,
                                    false,
                                    host.getUser(),
                                    host.getPassword()),
                        null);
                FileInputStream fis = new FileInputStream(file);

                String resp = SecurePushGateway.sendData(uri,
                        proxy,
                        fis, MediaType.MULTIPART_FORM_DATA, "POST", host.getHeaders());
                LOGGER.debug(resp);
                if(resp.contains("error")){
                    LOGGER.error("Could not push file {} to CloudChamber. Error: {}", file.getName(), resp);

                    return Response
                            .status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(getMessage(Response.Status.INTERNAL_SERVER_ERROR, "Could not push file to CloudChamber: "+file.getName()+" Reason: "+resp))
                            .build();
                }else{
                    LOGGER.info("Sent {} to CloudChamber. Resp: {} ", file.getName(), resp);
                    return Response.ok().entity(resp).build();
                }
            }catch (Exception ex){
                LOGGER.error("Could not push file {} to CloudChamber. Error: {}", file.getName(), ex.getMessage(), ex);
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(getMessage(Response.Status.INTERNAL_SERVER_ERROR, "Could not push file to CloudChamber: "+file.getName()+" Reason: "+ex.getMessage()))
                        .build();
            }
        }else{
            LOGGER.error("Pushed metrics to cloudChamber: File does not exist: {}", file.getName());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(getMessage(Response.Status.INTERNAL_SERVER_ERROR, "Could not push file to CloudChamber. File does not exist: "+file.getName()))
                    .build();
        }
    }
}
