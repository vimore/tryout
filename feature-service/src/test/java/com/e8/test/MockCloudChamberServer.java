package com.e8.test;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.hadoop.hbase.util.HttpServerUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MockCloudChamberServer {
    HttpServer httpServer;
    List<HttpContext> routes = new ArrayList<>();

    public static void writeResponse(HttpExchange httpExchange, String response)
            throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
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
        routes.add(httpServer.createContext("/whois/metrics", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), 0);
                writeResponse(httpExchange, "height of cache tree: 141938 height of geo cache : 149999 Size of domain whois cache : 118431 Size of domain passive cache : 2631 Size of domain reverse cache :  1496 Size of domain nod cache :  0 Size of users details : 21 Size of nonce details: 0");
            }}));
        routes.add(httpServer.createContext("/adv/domainwhois", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), 0);
                List<NameValuePair> params = URLEncodedUtils.parse(httpExchange.getRequestURI(),"UTF-8");
                for(NameValuePair param : params) {
                    if(param.getName().equals("domain")){
                        if(param.getValue().equals("www.amazon.com")){
                            writeResponse(httpExchange, "{\"domainName\":\"amazon.com\",\"updatedDate\":\"2014-04-30T19:11:08.000+0000\",\"creationDate\":\"1994-11-01T05:00:00.000+0000\",\"nameServers\":\"pdns1.ultradns.net, ns2.p31.dynect.net, ns1.p31.dynect.net, ns3.p31.dynect.net, pdns6.ultradns.co.uk, ns4.p31.dynect.net\",\"domainRegistrar\":{\"registrar\":\"MarkMonitor, Inc.\",\"whoisServer\":\"http://www.markmonitor.com\"},\"domainRegistrant\":[{\"name\":\"Hostmaster, Amazon Legal Dept.\",\"organization\":\"Amazon Technologies, Inc.\",\"street\":\"P.O. Box 8102\",\"city\":\"Reno\",\"postalCode\":\"89507\",\"country\":\"US\",\"phone\":\"+1.2062664064\",\"fax\":\"+1.2062667010\",\"email\":\"hostmaster@amazon.com\",\"state\":\"NV\"}],\"domainAdminContact\":[{\"name\":\"Hostmaster, Amazon Legal Dept.\",\"organization\":\"Amazon Technologies, Inc.\",\"street\":\"P.O. Box 8102\",\"city\":\"Reno\",\"postalCode\":\"89507\",\"country\":\"US\",\"phone\":\"+1.2062664064\",\"fax\":\"+1.2062667010\",\"email\":\"hostmaster@amazon.com\",\"state\":\"NV\"}],\"domainTechContact\":[{\"name\":\"Hostmaster, Amazon Legal Dept.\",\"organization\":\"Amazon Technologies, Inc.\",\"street\":\"P.O. Box 8102\",\"city\":\"Reno\",\"postalCode\":\"89507\",\"country\":\"US\",\"phone\":\"+1.2062664064\",\"fax\":\"+1.2062667010\",\"email\":\"hostmaster@amazon.com\",\"state\":\"NV\"}],\"lastUpdatedDate\":\"2014-09-24T07:26:58.000+0000\",\"registrationExpirationDate\":\"2022-10-31T04:00:00.000+0000\"}");
                        }
                        if(param.getValue().equals("xec.esc")){
                            writeResponse(httpExchange, "");
                        }
                    }
                }
            }}));
        routes.add(httpServer.createContext("/adv/whois/ip", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(Response.Status.OK.getStatusCode(), 0);
                writeResponse(httpExchange, "{\"startAddress\":\"74.125.0.0\",\"netName\":\"GOOGLE\",\"netHandle\":\"NET-74-125-0-0-1\",\"parent\":\"NET-74-0-0-0-0\",\"netType\":\"Direct Allocation\",\"regDate\":\"2007-03-13T00:00:00.000+0000\",\"updatedDate\":\"2012-02-24T00:00:00.000+0000\",\"ref\":\"http://whois.arin.net/rest/net/NET-74-125-0-0-1\",\"lastUpdatedDate\":\"2014-07-01T04:41:22.000+0000\",\"route\":{\"originASN\":\"\"},\"org\":{\"orgName\":\"Google Inc.\",\"orgId\":\"GOGL\",\"city\":\"Mountain View\",\"state\":\"CA\",\"postalCode\":\"94043\",\"country\":\"US\",\"regDate\":\"2000-03-30T00:00:00.000+0000\",\"updatedDate\":\"2013-08-07T00:00:00.000+0000\",\"ref\":\"http://whois.arin.net/rest/org/GOGL\"},\"orgAbuse\":[{\"orgAbuseHandle\":\"ZG39-ARIN\",\"orgAbuseName\":\"Google Inc\",\"orgAbusePhone\":\"+1-650-253-0000\",\"orgAbuseEmail\":\"arin-contact@google.com\",\"orgAbuseRef\":\"http://whois.arin.net/rest/poc/ZG39-ARIN\",\"lastUpdatedDate\":\"2014-07-01T04:41:22.000+0000\"}],\"orgTech\":[{\"orgTechHandle\":\"ZG39-ARIN\",\"orgTechName\":\"Google Inc\",\"orgTechPhone\":\"+1-650-253-0000\",\"orgTechEmail\":\"arin-contact@google.com\",\"orgTechRef\":\"http://whois.arin.net/rest/poc/ZG39-ARIN\",\"lastUpdatedDate\":\"2014-07-01T04:41:22.000+0000\"}],\"endAddress\":\"74.125.255.255\"}");
            }}));
    }
    public void start(){
        httpServer.start();
    }
    public void stop(){
        httpServer.stop(0);
    }
}
