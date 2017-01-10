package com.securityx.health.agent.jobs;

import com.securityx.health.agent.HealthAgentConfiguration;
import com.securityx.health.agent.util.SecurePushGateway;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SimpleTrigger(repeatInterval = 60, timeUnit = TimeUnit.SECONDS)
public class ReportUploadJob extends Job{
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ReportUploadJob.class);
    public void doRun() throws JobInterruptException {
        try {
            LOGGER.info("Running the {}  job", ClouderaClusterHealthJob.class.getName());
            ServletContext ctx = SundialJobScheduler.getServletContext();
            HealthAgentConfiguration configuration = (HealthAgentConfiguration) ctx.getAttribute(HealthAgentConfiguration.class.getName());
            //Client client = (Client) ctx.getAttribute(Client.class.getName());
            HealthAgentConfiguration.ClusterInfo clusterInfo = configuration.getClusterInfo();
            HealthAgentConfiguration.Host[] hosts = clusterInfo.getHosts();
            String dir;
            for(HealthAgentConfiguration.Host host : hosts){
                switch (host.getType()){
                    case "e8_cloudera":
                        pushCdhMetricsToCloudChamber(clusterInfo, host, host.getDirectory(), false,
                                ClouderaClusterHealthJob.getPushGateway(configuration,
                                        ClouderaClusterHealthJob.CLOUD_CHAMBER_PUSH_GATEWAY),
                                ClouderaClusterHealthJob.getPushGatewayProxy(configuration)
                        );
                        break;
                    case "e8_api":
                        pushSystemMetricsToCloudChamber(clusterInfo, host, host.getDirectory(), false,
                                ClouderaClusterHealthJob.getPushGateway(configuration,
                                        ClouderaClusterHealthJob.CLOUD_CHAMBER_PUSH_GATEWAY),
                                ClouderaClusterHealthJob.getPushGatewayProxy(configuration));
                        break;
                    case "e8_ui":
                        pushSystemMetricsToCloudChamber(clusterInfo, host, host.getDirectory(), false,
                                ClouderaClusterHealthJob.getPushGateway(configuration,
                                        ClouderaClusterHealthJob.CLOUD_CHAMBER_PUSH_GATEWAY),
                                ClouderaClusterHealthJob.getPushGatewayProxy(configuration));
                        break;
                    case "e8_directory_monitor":
                        pushFilesToCloudChamber(clusterInfo, host, configuration,
                                ClouderaClusterHealthJob.getPushGatewayProxy(configuration));
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
    public void pushCdhMetricsToCloudChamber(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                             HealthAgentConfiguration.Host host,
                                             String dir,
                                             boolean append,
                                             HealthAgentConfiguration.Host cloudChamber,
                                             Proxy proxy){

        try {
            String tag = clusterInfo.getTag()+":"+host.getType();
            Path path = Paths.get(dir);
            if(!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Files.walk(path).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        uploadFileToCloudChamber(tag,
                                filePath.getFileName().toString(),
                                append, filePath, cloudChamber, proxy);
                    } catch (Exception ex) {
                        LOGGER.error("Could not push CM metrics to cloud chamber {}", ex.getMessage(), ex);
                    }
                }
            });

        }catch(Exception ex){
            LOGGER.error("Could not push metrics to cloud chamber {}", ex.getMessage(), ex);
        }
    }
    public void pushSystemMetricsToCloudChamber(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                                HealthAgentConfiguration.Host host,
                                                String dir,
                                                boolean append,
                                                HealthAgentConfiguration.Host cloudChamber,
                                                Proxy proxy){
        try{
            String tag = clusterInfo.getTag()+":"+host.getType();
            Path path = Paths.get(dir+ File.separator+ ClouderaClusterHealthJob.SYSTEM_HEALTH_FILE);
            File file = path.toFile();
            if(file.exists()){
                try {
                    uploadFileToCloudChamber(tag,
                            host.getType() + ClouderaClusterHealthJob.SYSTEM_HEALTH_FILE,
                            append, path, cloudChamber, proxy);
                }catch(Exception ex){
                    LOGGER.error("Could not push {} {} metrics to cloud chamber {}", tag, host.getType(), ex.getMessage(), ex);
                }
            }else{
                LOGGER.error("File {} does not exist", file.toString());
            }
        }catch(Exception ex){
            LOGGER.error("{}", ex.getMessage(),  ex);
        }
    }
    public static void uploadFileToCloudChamber(String tag, String name,
                                                Boolean append,
                                                Path filePath,
                                                HealthAgentConfiguration.Host cloudChamber,
                                                Proxy proxy)
            throws  Exception{
        if(cloudChamber == null){
            return;
        }
        URI uri = new URI(cloudChamber.getScheme(),
                null,
                cloudChamber.getHost(),
                cloudChamber.getPort(),
                cloudChamber.getPath(),
                String.format(cloudChamber.getQuery(),
                        tag.replace(":","/"), //use a directory separator instead of the hyphen
                        name,
                        append,
                        cloudChamber.getUser(),
                        cloudChamber.getPassword()),
                null);
        List<HashMap<String, String>> headers = cloudChamber.getHeaders();

        String resp = SecurePushGateway.sendData(uri,
                proxy,
                new FileInputStream(filePath.toFile()),
                MediaType.MULTIPART_FORM_DATA,
                "POST",
                headers);

        if(resp.contains("error")){
            LOGGER.error(resp);
            throw new WebApplicationException(resp);
        }else{
            LOGGER.info(resp);
        }
    }

    private static List<Path> fileList(Path directory,
                                       long timeOfLastRun,
                                       List<Path> fileNames,
                                       final String filterPattern) {
        DirectoryStream.Filter<Path> fileFilter = entry -> entry.getFileName().startsWith(filterPattern);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, fileFilter)) {
            for (Path path : directoryStream) {
                if(Files.isDirectory(path)){
                    fileList(path, timeOfLastRun, fileNames, filterPattern);
                }else {
                    FileTime ft = Files.getLastModifiedTime(path);
                    if (ft.compareTo(FileTime.fromMillis(timeOfLastRun)) > 0) {
                        fileNames.add(path);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Could not fetch the files to upload for job {} from {}. Error: {}",
                    ReportUploadJob.class.getName(), directory, ex.toString(), ex);
        }
        return fileNames;
    }

    private static final long MILLIS_IN_MINUTE = 60*1000;
    private static void pushFilesToCloudChamber(HealthAgentConfiguration.ClusterInfo clusterInfo,
                                                HealthAgentConfiguration.Host host,
                                                HealthAgentConfiguration conf,
                                                Proxy proxy){
        try {
            String tag = clusterInfo.getTag()+":"+host.getType();
            if (host.getDirectory() == null) {
                return;
            }
            List<Path> fileList = new ArrayList<>();
            fileList(Paths.get(host.getDirectory()), System.currentTimeMillis() - MILLIS_IN_MINUTE, fileList, host.getFilterPattern());
            for (Path p : fileList) {
                uploadFileToCloudChamber(tag,p.toFile().getName(),
                        false, p,
                        ClouderaClusterHealthJob.getPushGateway(conf, "cloud-chamber"),
                        proxy);
            }
        }catch (Exception ex){
            LOGGER.error("Could not upload metrics to cloud chamber. Error: {}", ex.getMessage(), ex);
        }
    }
}
