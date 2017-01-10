package com.securityx.health.agent.util;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.exporter.common.TextFormat;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurePushGateway extends PushGateway {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SecurePushGateway.class);
    public final static int SECONDS_PER_MILLISECOND = 1000;
    private URI uri;
    private Proxy proxy;
    public SecurePushGateway(URI uri, Proxy proxy){
        super(uri.getHost()+":"+uri.getPort());
        this.uri = uri;
        this.proxy = proxy;
    }
    /**
     * Pushes all metrics in a registry, replacing all those with the same job as the grouping key.
     * <p>
     * This uses the POST HTTP method.
     */
    @Override
    public void push(CollectorRegistry registry, String job) throws IOException {
        doRequest(registry, job, null, "POST");
    }

    /**
     * Pushes all metrics in a Collector, replacing all those with the same job and no grouping key.
     * <p>
     * This is useful for pushing a single Gauge.
     * <p>
     * This uses the POST HTTP method.
     */
    @Override
    public void push(Collector collector, String job) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        push(registry, job);
    }

    /**
     * Pushes all metrics in a Collector, replacing all those with the same job and grouping key.
     * <p>
     * This is useful for pushing a single Gauge.
     * <p>
     * This uses the POST HTTP method.
     */
    @Override
    public void push(CollectorRegistry registry, String job, Map<String, String> groupingKey) throws IOException {
        doRequest(registry, job, groupingKey, "POST");
    }

    /**
     * Pushes all metrics in a Collector, replacing all those with the same job and grouping key.
     * <p>
     * This is useful for pushing a single Gauge.
     * <p>
     * This uses the POST HTTP method.
     */
    @Override
    public void push(Collector collector, String job, Map<String, String> groupingKey) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        push(registry, job, groupingKey);
    }

    /**
     * Pushes all metrics in a registry, replacing only previously pushed metrics of the same name and job and no grouping key.
     * <p>
     * This uses the PUT HTTP method.
     */
    @Override
    public void pushAdd(CollectorRegistry registry, String job) throws IOException {
        doRequest(registry, job, null, "PUT");
    }

    /**
     * Pushes all metrics in a Collector, replacing only previously pushed metrics of the same name and job and no grouping key.
     * <p>
     * This is useful for pushing a single Gauge.
     * <p>
     * This uses the PUT HTTP method.
     */
    @Override
    public void pushAdd(Collector collector, String job) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        pushAdd(registry, job);
    }

    /**
     * Pushes all metrics in a Collector, replacing only previously pushed metrics of the same name, job and grouping key.
     * <p>
     * This is useful for pushing a single Gauge.
     * <p>
     * This uses the PUT HTTP method.
     */
    @Override
    public void pushAdd(CollectorRegistry registry, String job, Map<String, String> groupingKey) throws IOException {
        doRequest(registry, job, groupingKey, "PUT");
    }

    /**
     * Pushes all metrics in a Collector, replacing only previously pushed metrics of the same name, job and grouping key.
     * <p>
     * This is useful for pushing a single Gauge.
     * <p>
     * This uses the PUT HTTP method.
     */
    @Override
    public void pushAdd(Collector collector, String job, Map<String, String> groupingKey) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        pushAdd(registry, job, groupingKey);
    }


    /**
     * Deletes metrics from the Pushgateway.
     * <p>
     * Deletes metrics with no grouping key and the provided job.
     * This uses the DELETE HTTP method.
     */
    @Override
    public void delete(String job) throws IOException {
        doRequest(null, job, null, "DELETE");
    }

    /**
     * Deletes metrics from the Pushgateway.
     * <p>
     * Deletes metrics with the provided job and grouping key.
     * This uses the DELETE HTTP method.
     */
    @Override
    public void delete(String job, Map<String, String> groupingKey) throws IOException {
        doRequest(null, job, groupingKey, "DELETE");
    }

    protected void doRequest(CollectorRegistry registry, String job, Map<String, String> groupingKey, String method) throws IOException {
        String url = uri.toString() + "/metrics/job/" + URLEncoder.encode(job, "UTF-8");

        if (groupingKey != null) {
            for (Map.Entry<String, String> entry: groupingKey.entrySet()) {
                url += "/" + entry.getKey() + "/" + URLEncoder.encode(entry.getValue(), "UTF-8");
            }
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Content-Type", TextFormat.CONTENT_TYPE_004);
        if (!method.equals("DELETE")) {
            connection.setDoOutput(true);
        }
        connection.setRequestMethod(method);

        connection.setConnectTimeout(10 * SECONDS_PER_MILLISECOND);
        connection.setReadTimeout(10 * SECONDS_PER_MILLISECOND);
        connection.connect();

        try {
            if (!method.equals("DELETE")) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                TextFormat.write004(writer, registry.metricFamilySamples());
                writer.flush();
                writer.close();
            }

            int response = connection.getResponseCode();
            if (response != HttpURLConnection.HTTP_ACCEPTED) {
                throw new IOException("Response code from " + url + " was " + response);
            }
        } finally {
            connection.disconnect();
        }
    }

    private static final int CHUNK_SIZE = 1000000;

    public static Proxy getProxy(String host, int port, String user, String pass){
        Proxy proxy = null;
        if(host!=null && !host.isEmpty()) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            //only enable authentication if the user and pass are sent
            if(user!=null && !user.isEmpty() && pass!=null && !pass.isEmpty()) {
                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return (new PasswordAuthentication(user, pass.toCharArray()));
                    }
                };
                Authenticator.setDefault(authenticator);
            }
        }
        return proxy;
    }
    public static String sendData(URI uri,
                                  Proxy proxy,
                                  InputStream entity,
                                  String contentType,
                                  String method,
                                  List<HashMap<String, String>> requestHeaders){
        try{
            HttpURLConnection connection;
            if(proxy!=null) {
                connection = (HttpURLConnection) uri.toURL().openConnection(proxy);
            }else{
                connection = (HttpURLConnection) uri.toURL().openConnection();
            }
            try {
                connection.setChunkedStreamingMode(CHUNK_SIZE);
                //connection.setFixedLengthStreamingMode(fileSize);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", contentType);
                connection.setRequestMethod(method);
                //set the request headers
                if(requestHeaders!=null){
                    for(HashMap<String, String> header : requestHeaders){
                        connection.setRequestProperty(header.get("name"), header.get("value"));
                    }
                }
                connection.setConnectTimeout(10 * SecurePushGateway.SECONDS_PER_MILLISECOND);
                connection.setReadTimeout(10 * SecurePushGateway.SECONDS_PER_MILLISECOND);
                connection.connect();
                OutputStream os = connection.getOutputStream();
                byte[] bytes = new byte[CHUNK_SIZE];
                int length;
                while ((length = entity.read(bytes)) > 0) {
                    os.write(bytes, 0, length);
                }
                os.flush();
                os.close();
                entity.close();
                int resp = connection.getResponseCode();
                String msg = connection.getResponseMessage();
                if (resp != HttpURLConnection.HTTP_ACCEPTED) {
                    LOGGER.error("Could not push the metrics to {}. Error: {}", uri.toString(), msg);
                    return String.format("{\"status\":\"%s\", \"code\":%d, \"desc\":\"%s\"}","error", resp, msg);
                }else{
                    LOGGER.debug("Pushed the metrics to {}, Status: {}", uri.toString(), msg);
                    return String.format("{\"status\":\"%s\", \"code\":%d, \"desc\":\"%s\"}","success", resp, msg);
                }
            }catch(Exception ex){
                LOGGER.error("Could not push API metrics to  {}. Error: {}", uri.toString(), ex.getMessage(), ex);
                return String.format("{\"status\":\"%s\", \"code\":%d, \"desc\":\"%s\"}","error", 500, ex.getMessage());
            }finally {
                connection.disconnect();
            }
        } catch (Exception ex){
            LOGGER.error("Could not push API metrics to  {}. Error: {}", uri.toString(), ex.getMessage(), ex);
            return String.format("{\"status\":\"%s\", \"code\":%d, \"desc\":\"%s\"}","error", 500, ex.getMessage());
        }
    }
}