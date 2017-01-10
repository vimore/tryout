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

public class MockPrometheusServer {
    HttpServer httpServer;
    List<HttpContext> routes = new ArrayList<>();
    public MockPrometheusServer(String ip, int port) throws Exception{
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(ip,port), 0);
        routes.add(httpServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
                //Write the response string
                String response ="<html><title>Yeah</title><body>"+httpExchange.getRequestURI()+"</body></html>";
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }}));
        routes.add(httpServer.createContext("/metrics", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
                OutputStream os = httpExchange.getResponseBody();
                String response ="<html><title>Yeah</title><body>"+httpExchange.getRequestURI()+"</body></html>";
                os.write(response.getBytes());
                os.close();
            }}));
        routes.add(httpServer.createContext("/metrics/job", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
                OutputStream os = httpExchange.getResponseBody();
                String response ="<html><title>Yeah</title><body>"+httpExchange.getRequestURI()+"</body></html>";
                os.write(response.getBytes());
                os.close();
            }}));
        routes.add(httpServer.createContext("/metrics/job/{foo}", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
                OutputStream os = httpExchange.getResponseBody();
                String response ="<html><title>Yeah</title><body>"+httpExchange.getRequestURI()+"</body></html>";
                os.write(response.getBytes());
                os.close();
            }}));
        routes.add(httpServer.createContext("/metrics/job/{foo}/instance", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
                OutputStream os = httpExchange.getResponseBody();
                String response ="<html><title>Yeah</title><body>"+httpExchange.getRequestURI()+"</body></html>";
                os.write(response.getBytes());
                os.close();
            }}));
        routes.add(httpServer.createContext("/metrics/job/{foo}/instance/{bar}", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.ACCEPTED.getStatusCode(), 0);
                OutputStream os = httpExchange.getResponseBody();
                String response ="<html><title>Yeah</title><body>"+httpExchange.getRequestURI()+"</body></html>";
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
        MockPrometheusServer prometheusServer = new MockPrometheusServer("127.0.0.1", 9083);
        prometheusServer.start();
    }
}
