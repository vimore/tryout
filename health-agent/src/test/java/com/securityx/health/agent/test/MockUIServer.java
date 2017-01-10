package com.securityx.health.agent.test;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MockUIServer {
    HttpServer httpServer;
    List<HttpContext> routes = new ArrayList<>();
    public MockUIServer(String ip, int port) throws Exception{
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(ip,port), 0);
        routes.add(httpServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                String response ="<html><title>Yeah</title><body>Yeah</body></html>";
                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), response.getBytes().length);
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

}
