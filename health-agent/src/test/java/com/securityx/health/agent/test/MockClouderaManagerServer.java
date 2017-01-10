package com.securityx.health.agent.test;


import com.sun.net.httpserver.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MockClouderaManagerServer {
    HttpServer httpServer;
    List<HttpContext> routes = new ArrayList<>();
    public MockClouderaManagerServer(String ip, int port) throws Exception{
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(ip,port), 0);
        routes.add(httpServer.createContext("/api/v11/clusters", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), 0);
                //Write the response string
                String response ="{\n" +
                        "  \"items\" : [ {\n" +
                        "    \"name\" : \"cluster\",\n" +
                        "    \"displayName\" : \"Cluster 5\",\n" +
                        //"    \"version\" : \"CDH5\",\n" +
                        //"    \"fullVersion\" : \"5.4.4\",\n" +
                        "    \"maintenanceMode\" : false,\n" +
                        "    \"maintenanceOwners\" : [ ],\n" +
                        //"    \"clusterUrl\" : \"http://127.0.0.1:7180/cmf/clusterRedirect/cluster\",\n" +
                        //"    \"hostsUrl\" : \"http://127.0.0.1:7180/cmf/clusterRedirect/cluster/hosts\",\n" +
                        "    \"entityStatus\" : \"CONCERNING_HEALTH\"\n" +
                        "  } ]\n" +
                        "}";
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }}));

    }
    public void start(){
        httpServer.start();
    }
    public void stop(){
        httpServer.stop(0);
    }
    public static void main(String[] args) throws Exception{
        MockClouderaManagerServer prometheusServer = new MockClouderaManagerServer("127.0.0.1", 9083);
        prometheusServer.start();
    }
}
