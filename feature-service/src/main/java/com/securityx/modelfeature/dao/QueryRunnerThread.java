package com.securityx.modelfeature.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.ImpalaClient;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.utils.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.securityx.modelfeature.utils.ImpalaUtils.closeConnections;

/**
 * Created by preetitambakhe on 17/10/16.
 */
public class QueryRunnerThread implements Callable {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryRunnerThread.class);

    String query = null;
    int totalRecords = 0;
    FeatureServiceConfiguration conf = null;

    public QueryRunnerThread(String query, int totalRecords, FeatureServiceConfiguration conf) {
        this.query = query;
        this.totalRecords = totalRecords;
        this.conf = conf;
    }
    @Override
    public List<Map<String, Object>> call() {

        List<Map<String, Object>> facetList = Lists.newLinkedList();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LOGGER.debug("Executing query: {}", this.query);
        try {
            conn = ImpalaClient.getImpalaConnection(this.conf);
            pstmt = conn.prepareStatement(this.query);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();
            //String localizedName = "";
            //boolean namePopulated = false;
            while(rs.next()) {
                Map<String, Object> facetKeyValues = Maps.newLinkedHashMap();
                String fieldValue = rs.getObject(1)!=null ? rs.getString(1) : null;
                double fieldCount = rs.getDouble(2);
                if(fieldValue!=null && !fieldValue.isEmpty()) {
                    facetKeyValues.put("value", fieldValue);
                    facetKeyValues.put("percent", MiscUtils.getPercentage(fieldCount, totalRecords));
                    String fieldName = rs.getMetaData().getColumnLabel(1);
                    facetKeyValues.put("fieldName", fieldName);
                    facetKeyValues.put("count", fieldCount);
                    facetList.add(facetKeyValues);
                }
            }

        } catch(Exception ex) {
            LOGGER.error("Query Failed to get logs data from Impala {}", ex.getMessage() ,ex);
        } finally {
            closeConnections(conn, pstmt, rs);
        }

        return facetList;

    }

}
