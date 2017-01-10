package com.securityx.modelfeature.dao;

import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.apache.phoenix.query.QueryServices;
import org.apache.phoenix.schema.TableAlreadyExistsException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class PhoenixUtils {

    public static Connection getPhoenixConnection(final FeatureServiceConfiguration conf) throws Exception {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        String connectionURL = "jdbc:phoenix:" + conf.getZkQuorum();
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty(QueryServices.RPC_TIMEOUT_ATTRIB,
                String.valueOf(conf.getPhoenix().getHbaseRpcTimeout()));
        connectionProperties.setProperty(QueryServices.KEEP_ALIVE_MS_ATTRIB,
                String.valueOf(conf.getPhoenix().getQueryTimeoutMs()));

        if(conf.getPhoenix().getHbaseClientRetriesNumber() != null){
            connectionProperties.setProperty("hbase.client.retries.number", conf.getPhoenix().getHbaseClientRetriesNumber());
        }
        if(conf.getPhoenix().getHbaseClientPause() != null){
            connectionProperties.setProperty("hbase.client.pause", conf.getPhoenix().getHbaseClientPause());
        }
        if(conf.getPhoenix().getZookeeperRecoveryRetry() != null) {
            connectionProperties.setProperty("zookeeper.recovery.retry", conf.getPhoenix().getZookeeperRecoveryRetry());
        }
        if(conf.getPhoenix().getZookeeperRecoveryRetryIntervalmill() != null) {
            connectionProperties.setProperty("zookeeper.recovery.retry.intervalmill", conf.getPhoenix().getZookeeperRecoveryRetryIntervalmill());
        }

        Connection r = DriverManager.getConnection(connectionURL, connectionProperties);
        r.setAutoCommit(true);
        return r;
    }

    public static void execute(final Connection conn, final String sql) throws Exception {
        Statement stmt = conn.createStatement();
        try {
            stmt.execute(sql);
        } catch (Exception ex){
            System.out.println("Exception running SQL: " + sql);
            throw ex;
        }
    }

    /**
     * Execute a SQL DDL statement and ignore any "table already exists" errors
     */
    public static void executeNoTableExistsThrow(final Connection conn, final String sql) throws Exception {
        try {
            execute(conn, sql);
        } catch (TableAlreadyExistsException ex) {
            // GULP
        }
    }
}
