package com.securityx.mef.log.mapreduce;

import com.cloudera.com.amazonaws.util.json.JSONException;
import com.cloudera.com.amazonaws.util.json.JSONObject;
import com.codahale.metrics.graphite.Graphite;
import com.google.common.base.Splitter;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;

public class HealthReporter {
    private static final int CHUNK_SIZE = 16384;
    private static final int SECONDS = 1000;
    private HttpURLConnection healthServerConnection;
    private JSONObject healthMsg;
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthReporter.class);
    private URI healthServerUri;


    public HealthReporter(URI healthServerUri) {

        this.healthMsg = new JSONObject();
        this.healthServerUri = healthServerUri;

    }

    private String getCounterName(Counter c, String domain){
        return getName(domain+"."+c.getDisplayName());
    }

    private String getName(String domain){
        return domain;
    }

    private String getCounterValue(Counter c){
        return String.valueOf(c.getValue());
    }

    public void publishCounter(String domain, CounterGroup counters) throws IOException {
        Iterator<Counter> it = counters.iterator();
        while (it.hasNext()) {
            Counter c = it.next();
            if (c.getName().contains("startTime_"))
                continue;

            try {
                this.healthMsg.put(getCounterName(c, domain),getCounterValue(c));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String key, String value) throws IOException {
        try {
            this.healthMsg.put(getName(key), value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void close() throws IOException {

        try{
            this.healthServerConnection = (HttpURLConnection) healthServerUri.toURL().openConnection();
            try {
                healthServerConnection.setChunkedStreamingMode(CHUNK_SIZE);
                //healthServerConnection.setFixedLengthStreamingMode(fileSize);
                healthServerConnection.setDoOutput(true);
                healthServerConnection.setRequestProperty("Content-Type", "application/json");
                healthServerConnection.setRequestMethod("POST");
                healthServerConnection.setConnectTimeout(10 * SECONDS);
                healthServerConnection.setReadTimeout(10 * SECONDS);
                healthServerConnection.connect();
                OutputStream os = healthServerConnection.getOutputStream();
                byte[] bytes = new byte[CHUNK_SIZE];

                ByteArrayInputStream in = new ByteArrayInputStream( this.healthMsg.toString().getBytes());
                int len;
                while ((len = in.read(bytes)) > 0) {
                    os.write(bytes, 0, len);
                }

                os.flush();
                os.close();
                int resp = healthServerConnection.getResponseCode();
                String msg = healthServerConnection.getResponseMessage();
                if (resp != HttpURLConnection.HTTP_OK) {
                    LOGGER.error("Could not push the metrics to {}. Error: {}", healthServerUri.toString(), msg);
                }else{
                    LOGGER.debug("Pushed the metrics to {}, Error: {}", healthServerUri.toString(), msg);
                }
            }finally {
                healthServerConnection.disconnect();
            }
        } catch (Exception ex){
            LOGGER.error("Could not push API metrics to  {}. Error: {}", healthServerUri.toString(), ex.getMessage());
        }

    }

}
