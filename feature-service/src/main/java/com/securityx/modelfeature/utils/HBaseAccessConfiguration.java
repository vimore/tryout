package com.securityx.modelfeature.utils;

import com.e8.sparkle.commons.hbase.HBaseClient;
import com.e8.sparkle.storage.hbase.rowobjects.Entity;
import com.e8.sparkle.storage.hbase.tableobjects.BaseTable;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.*;

/**
 * This class exists as a mechanism to tie the API server config file together with the config
 * info that we need to pass to the HBase access layer.  It is _not_ one of the standard configuration
 * objects that are loaded when the api server config is loaded.
 */
public class HBaseAccessConfiguration extends Configuration {
    public static final String IP_ADDRESS = "IP_ADDRESS";
    public static final String HOST_NAME = "HOST_NAME";
    public static final String MAC_ADDRESS = "MAC_ADDRESS";
    public static final String USER_NAME = "USER_NAME";
    public static final String ENTITY_ID = "ENTITY_ID";

    public static final HBaseClient hBaseClient = new HBaseClient();

    public HBaseAccessConfiguration (FeatureServiceConfiguration conf) {
        String zkQuorum = conf.getZkQuorum();
        int colonIndex = zkQuorum.indexOf(":");
        if (colonIndex == -1) {
            set("hbaseMaster", zkQuorum);
            set("zkQuorum", zkQuorum);
            set("zkClientPort", "2181");
        } else {
            String zkHost = zkQuorum.substring(0, colonIndex);
            String zkPort = zkQuorum.substring(colonIndex + 1);
            set("hbaseMaster", zkHost);
            set("zkQuorum", zkHost);
            set("zkClientPort", zkPort);
        }
        // String schema = conf.getPhoenix().getSchema() == null ? "" : conf.getPhoenix().getSchema();
        // We have a schema that should be passed into the hbase access layer
        set(BaseTable.SCHEMA_CONFIGURATION, conf.getPhoenix().getSchema());
    }

    public HBaseAccessConfiguration(String master, String quorum, String port, String tablePrefix) {
        set("hbaseMaster", master);
        set("zkQuorum", quorum);
        set("zkClientPort", port);
        if(tablePrefix==null){
            tablePrefix = "";
        }
        set(BaseTable.SCHEMA_CONFIGURATION, tablePrefix);
    }

    public static com.e8.sparkle.storage.hbase.query.QueryJson constructEqualQuery(String startTime, String endTime, String column, String value) {
        com.e8.sparkle.storage.hbase.query.QueryJson query = new com.e8.sparkle.storage.hbase.query.QueryJson();
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        Map<String, Object> columnFilter = new HashMap<String, Object>();
        if (Entity.IP_ADDRESS_FIELD.equals(column)) {
            columnFilter.put("field", Entity.IP_ADDRESS_FIELD);
        } else if (Entity.HOST_NAME_FIELD.equals(column)) {
            columnFilter.put("field", Entity.HOST_NAME_FIELD);
        } else if (Entity.MAC_ADDRESS_FIELD.equals(column)) {
            columnFilter.put("field", Entity.MAC_ADDRESS_FIELD);
        } else if (Entity.USER_NAME_FIELD.equals(column)) {
            columnFilter.put("field", Entity.USER_NAME_FIELD);
        } else if (Entity.ENTITY_ID_FIELD.equals(column)) {
            columnFilter.put("field", Entity.IP_ADDRESS_FIELD);
        }
        columnFilter.put("operator", BaseTable.EQUALS);
        List<Object> valueList = new ArrayList<Object>();
        valueList.add(value);
        columnFilter.put("values", valueList);
        List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
        queryList.add(columnFilter);
        query.setQuery(queryList);

        return query;
    }

    // We route all calls to get connections through the HBaseAccessConfiguration object in order to reduce the number
    // HBaseClient objects, since each one holds and returns a Connection object and we want to minimize the number of
    // HBase connections that are created.

    public Connection getConnection() throws IOException {
        return hBaseClient.getConnection(this);
    }

    public void close(Table t) throws IOException {
        if (t != null) {
            HBaseClient.close(null, t);
        }
    }
}