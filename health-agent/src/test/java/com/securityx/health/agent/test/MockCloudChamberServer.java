package com.securityx.health.agent.test;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MockCloudChamberServer {
    HttpServer httpServer;
    List<HttpContext> routes = new ArrayList<>();
    public MockCloudChamberServer(String ip, int port) throws Exception{
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(ip,port), 0);
        routes.add(httpServer.createContext("/*", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), 0);
            }}));

        routes.add(httpServer.createContext("/report/upload", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
            }}));

    }
    public void start(){
        httpServer.start();
    }
    public void stop(){
        httpServer.stop(0);
    }
}
