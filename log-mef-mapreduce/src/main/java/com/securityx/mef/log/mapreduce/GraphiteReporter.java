package com.securityx.mef.log.mapreduce;

import com.codahale.metrics.graphite.Graphite;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import java.io.IOException;
import java.util.Iterator;

public class GraphiteReporter {
    private final String hostname;
    private final String realm;
    private Graphite graphiteClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphiteReporter.class);

    public GraphiteReporter(String host, int port, String hostname, String realm) {

        this.graphiteClient = new Graphite(host, port, SocketFactory.getDefault());
        this.hostname = hostname;
        if (realm == null)
            this.realm="";
        else if (realm.endsWith("."))
            this.realm = realm;
        else
            this.realm = realm.concat(".");
    }

    private String getCounterName(Counter c, String domain){
        return getName(domain+"."+c.getDisplayName().replaceAll("\\s", "_").replaceAll("[^a-zA-Z0-9\\._-]", ""));
    }

    private String getName(String domain){
        return this.realm+"parser.stats."+this.hostname+"."+domain+".count";
    }

    private String getCounterValue(Counter c){
        return String.valueOf(c.getValue());
    }

    public void publishCounter(String domain, CounterGroup counters) throws IOException {
        if (! this.graphiteClient.isConnected())
            this.graphiteClient.connect();
        Iterator<Counter> it = counters.iterator();
        long currentTime = System.currentTimeMillis() / 1000;
        while (it.hasNext()) {
            Counter c = it.next();
            //System.out.println("jyria : counter :" + getCounterName(c, domain) + " : " + getCounterValue(c));
            // dont' send buckets stats as the counter name is changing
            if (c.getName().contains("startTime_"))
                continue;
            LOGGER.info("graphite : sending "+getCounterName(c, domain) + ": " + getCounterValue(c));
            this.graphiteClient.send(getCounterName(c, domain), getCounterValue(c), currentTime);

        }
        this.graphiteClient.flush();
    }

    public void publish(String key, String value) throws IOException {
        if (! this.graphiteClient.isConnected())
            this.graphiteClient.connect();
        long currentTime = System.currentTimeMillis() / 1000;
        this.graphiteClient.send(getName(key), value, currentTime);
    }


    public void close() throws IOException {
        this.graphiteClient.close();
        if (this.graphiteClient.getFailures() > 0 ) {
            LOGGER.info("graphite encountered " + String.valueOf(this.graphiteClient.getFailures()) + " error(s)");
        }

    }

}
