package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpalaConfiguration extends Configuration {

    public static final Logger log = LoggerFactory.getLogger(ImpalaConfiguration.class);

    @JsonProperty
    private String host;

    @JsonProperty
    private String port;

    @JsonProperty
    private String connectionUrl;

    @JsonProperty
    private String jdbcDriver;

    @JsonProperty
    private String databaseName;

    @JsonProperty
    private String webProxyParquetTable;

    @JsonProperty
    private String iamParquetTable;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getWebProxyParquetTable() {
        return webProxyParquetTable;
    }

    public void setWebProxyParquetTable(String webProxyParquetTable) {
        this.webProxyParquetTable = webProxyParquetTable;
    }

    public String getIamParquetTable() {
        return iamParquetTable;
    }

    public void setIamParquetTable(String iamParquetTable) {
        this.iamParquetTable = iamParquetTable;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}

