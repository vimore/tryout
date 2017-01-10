package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoenixConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(PhoenixConfiguration.class);

    @JsonProperty
    private int hbaseRpcTimeout;

    @JsonProperty
    private int queryTimeoutMs;

    @JsonProperty("hbase.client.retries.number")
    private String hbaseClientRetriesNumber;

    @JsonProperty("hbase.client.pause")
    private String hbaseClientPause;

    @JsonProperty("zookeeper.recovery.retry")
    private String zookeeperRecoveryRetry;

    @JsonProperty("zookeeper.recovery.retry.intervalmill")
    private String zookeeperRecoveryRetryIntervalmill;

    // schema is the schema to use in all phoenix queries.  If null, no schema will be used.
    @JsonProperty
    private String schema = "";


    public int getHbaseRpcTimeout() {
        return hbaseRpcTimeout;
    }

    public void setHbaseRpcTimeout(int hbaseRpcTimeout) {
        this.hbaseRpcTimeout = hbaseRpcTimeout;
    }

    public int getQueryTimeoutMs() {
        return queryTimeoutMs;
    }

    public void setQueryTimeoutMs(int queryTimeoutMs) {
        this.queryTimeoutMs = queryTimeoutMs;
    }

    public String getHbaseClientRetriesNumber() {
        return hbaseClientRetriesNumber;
    }

    public void setHbaseClientRetriesNumber(String hbaseClientRetriesNumber) {
        this.hbaseClientRetriesNumber = hbaseClientRetriesNumber;
    }

    public String getHbaseClientPause() {
        return hbaseClientPause;
    }

    public void setHbaseClientPause(String hbaseClientPause) {
        this.hbaseClientPause = hbaseClientPause;
    }

    public String getZookeeperRecoveryRetry() {
        return zookeeperRecoveryRetry;
    }

    public void setZookeeperRecoveryRetry(String zookeeperRecoveryRetry) {
        this.zookeeperRecoveryRetry = zookeeperRecoveryRetry;
    }

    public String getZookeeperRecoveryRetryIntervalmill() {
        return zookeeperRecoveryRetryIntervalmill;
    }

    public void setZookeeperRecoveryRetryIntervalmill(String zookeeperRecoveryRetryIntervalmill) {
        this.zookeeperRecoveryRetryIntervalmill = zookeeperRecoveryRetryIntervalmill;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
        if (!this.schema.endsWith(".")) {
            // We make sure the schema ends in ., so we don't have to check and append it every
            // time we use it.
            this.schema += ".";
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("hbaseRpcTimeout", hbaseRpcTimeout)
                .add("queryTimeoutMs", queryTimeoutMs)
                .add("schema", schema)
                .toString();
    }
}
