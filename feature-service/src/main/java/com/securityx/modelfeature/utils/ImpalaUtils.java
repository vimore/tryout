package com.securityx.modelfeature.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.ImpalaClient;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.config.ImpalaConfiguration;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class ImpalaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpalaUtils.class);
    private static final ImpalaColumnNameMapper columnNameMapper = ImpalaColumnNameMapper.createInstance();
    /**
     * Close the prepared statement and connection in the same order
     * @param connection The connection to close
     * @param preparedStatement The prepared statement to close
     */
    public static void closeConnections(Connection connection,
                                        Statement preparedStatement){
        if(preparedStatement!= null){
            try{
                preparedStatement.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close prepared statement. Error: {}", ex.getMessage(), ex);
            }
        }
        if(connection != null){
            try{
                connection.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }

    public static void closeConnections(Statement preparedStatement,
                                        ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close result set. Error: {}", ex.getMessage(), ex);
            }
        }
        if(preparedStatement!= null){
            try{
                preparedStatement.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close prepared statement. Error: {}", ex.getMessage(), ex);
            }
        }
    }
    public static void enableCountDistinct(Connection conn){
        LOGGER.debug("Enabling APPX_COUNT_DISTINCT on the connection ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            pstmt = conn.prepareStatement("SET APPX_COUNT_DISTINCT=true");
            rs = pstmt.executeQuery();
        }catch(Exception ex){
            LOGGER.error("Could not set the APPX_COUNT_DISTINCT option: {} ", ex.getMessage(), ex);
        }finally {
            closeConnections(pstmt,rs);
        }
    }
    /**
     * Close the result set, prepared statement and connection in the same order
     * @param connection The connection to close
     * @param preparedStatement The prepared statement to close
     * @param resultSet the result set to close
     */
    public static void closeConnections(Connection connection,
                                        Statement preparedStatement,
                                        ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close result set. Error: {}", ex.getMessage(), ex);
            }
        }
        if(preparedStatement!= null){
            try{
                preparedStatement.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close prepared statement. Error: {}", ex.getMessage(), ex);
            }
        }
        if(connection != null){
            try{
                connection.close();
            }catch(Exception ex){
                LOGGER.warn("Could not close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }
    public static List<Map<String, String>> executeQuery(String sql, String tableName, FeatureServiceConfiguration conf) {
        return executeQuery(sql, conf, tableName, false);
    }
    /**
     * Utility method Execute the Sql query being passed using impala configuration and populate the result
     * @param sql    SQL query to be executed
     * @param conf   FeatureServiceConfiguration
     * @return
     */
    public static List<Map<String, String>> executeQuery(String sql,
                                                         FeatureServiceConfiguration conf,
                                                         String tableName,
                                                         boolean enableCountDistinct){

        List<Map<String, String>> rows = new LinkedList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LOGGER.debug("Executing Impala Query: {}", sql);
        try {
            conn = ImpalaClient.getImpalaConnection(conf);

            if(enableCountDistinct) enableCountDistinct(conn);

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData rsMeta = rs.getMetaData();
            while(rs.next()) {
                Map<String, String> dataMap = getResultSetData(rs,rsMeta, tableName, conf);
                rows.add(dataMap);
            }
        } catch(Exception ex) {
            LOGGER.error("Query Failed to get logs data from Impala {}", ex.getMessage() ,ex);
            return null;
        }finally {
            closeConnections(conn, pstmt, rs);
        }
        LOGGER.debug("Impala returned {} rows for query {}", rows.size(), sql);
        return rows;
    }

    /**
     * Utility method to read the resultset and resultset metadata and populate in Hashmap
     * in the form of key-value pairs
     * @param rs       ResultSet
     * @param rsMeta   ResultSetMetaData
     * @return
     * @throws Exception
     */

    public static Map<String, String> getResultSetData(ResultSet rs, ResultSetMetaData rsMeta, String tableName, FeatureServiceConfiguration conf) throws Exception {

        int columns = rsMeta.getColumnCount();
        Map<String, String> dataMap = new HashMap<>();
        for(int i=1; i <= columns; i++) {
            // can't use rsMeta.getTableName() since it returns null.
            String columnName = getColumnName(tableName, rsMeta.getColumnName(i), conf);
            String columnValue = rs.getString(i);
            dataMap.put(columnName,columnValue);

        }
        return dataMap;
    }
    public static String getColumnName(String tableName, String columnName, FeatureServiceConfiguration conf){
        String newName;
        if(tableName.endsWith(conf.getImpalaConfiguration().getIamParquetTable())){
             newName = columnNameMapper.mapIamColumnName(columnName);

        }else if(tableName.endsWith(conf.getImpalaConfiguration().getWebProxyParquetTable())){
            newName = columnNameMapper.mapWebProxyColumnName(columnName);
        }else{
            LOGGER.error("Unknown table name: {}", tableName);
            newName = columnName;
        }
        if(newName == null){
            LOGGER.debug("Could not map columnName {} from table {}", columnName, tableName);
            newName = columnName;
        }
        return newName;
    }
    /**
     * Method to execute facet queries and populate values and their corresponding counts
     * @param sql              Sql query to be executed
     * @param fieldName        The field name for which distinct values and counts are to be fetched
     * @param totalRecords     Total no of records matching the search criteria
     * @param conf             FeatureServiceConfiguration
     * @return
     */
    public static List<Map<String, String>> executeFacetQuery(String sql,
                                                              String fieldName,
                                                              long totalRecords,
                                                              FeatureServiceConfiguration conf) {
        List<Map<String, String>> facetList = Lists.newLinkedList();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LOGGER.debug("Executing Impala Facet query: {}", sql);
        try {
            conn = ImpalaClient.getImpalaConnection(conf);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();

            while(rs.next()) {
                Map<String, String> facetKeyValues = Maps.newLinkedHashMap();
                String fieldValue = rs.getObject(1)!=null ? rs.getString(1) : null;
                double fieldCount = rs.getDouble(2);
                //RAM: I do not understand this code. what are "value", "percent", "fieldName" and "count"?
                //@Preeti can you please explain?
                if(fieldValue!=null && !fieldValue.isEmpty()) {
                    facetKeyValues.put("value", fieldValue);
                    facetKeyValues.put("percent", Double.toString(MiscUtils.getPercentage(fieldCount, totalRecords)));
                    facetKeyValues.put("fieldName", fieldName);
                    facetKeyValues.put("count",  Double.toString(fieldCount));
                    facetList.add(facetKeyValues);
                }
            }
        } catch(Exception ex) {
            LOGGER.error("Query Failed to get logs data from Impala {}", ex.getMessage() ,ex);
        } finally {
            closeConnections(conn, pstmt, rs);
        }
        LOGGER.debug("Impala returned {} facets for Facet Query : {}", facetList.size(), sql);
        return facetList;
    }

    /**
     * Ge th etotal no of records matching the search criteria
     * @param tableName      Table to be queried
     * @param whereClause    where clause part of query to eb used for getting counts
     * @param conf           FeatureServiceConfiguration
     * @return
     */
    public static long getTotalRecordsForFilter(String tableName,
                                               String whereClause,
                                               FeatureServiceConfiguration conf) {

        long noOfRecords = 0;
        //String tableName = SearchUtils.getImpalaTableName(modelId);
        String sql = " SELECT COUNT(*) FROM " + tableName + whereClause;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LOGGER.debug("Executing Impala Query: {}", sql);
        try {
            conn = ImpalaClient.getImpalaConnection(conf);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();
            while(rs.next()) {
                noOfRecords = rs.getLong(1);
            }
        } catch(Exception ex) {
            LOGGER.error("Query Failed to get logs data from Impala {}", ex.getMessage() ,ex);
        } finally {
            closeConnections(conn, pstmt, rs);
        }

        LOGGER.debug("Impala returned {} rows for query : {}", noOfRecords, sql);
        return noOfRecords;
    }

    /**
     * Ge the count of records for a given period of time
     * @param modelId      Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param startTime    String specifying start Time in the query
     * @param endTime      String specifying end Time in the query
     * @param conf         FeatureServiceConfiguration
     * @return
     */
    public static int getTotalRecordsTimeFilter(int modelId,
                                                String startTime,
                                                String endTime,
                                                FeatureServiceConfiguration conf){

        int noOfRecords = 0;
        String tableName = getImpalaTableName(modelId,conf);
        String sql = " SELECT COUNT(*) FROM " + tableName + " WHERE STARTTIMEISO >= '" + startTime + "' AND STARTTIMEISO < '" + endTime+"' ";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LOGGER.debug("Executing Impala query: {}", sql);
        try {
            conn = ImpalaClient.getImpalaConnection(conf);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();
            while(rs.next()) {
                noOfRecords = rs.getInt(1);
            }
        } catch(Exception ex) {
            LOGGER.error("Query Failed to get logs data from Impala {}", ex.getMessage() ,ex);
        } finally {
            closeConnections(conn, pstmt, rs);
        }
        LOGGER.debug("Impala returned {} rows for query : {}", noOfRecords, sql);
        return noOfRecords;
    }
    /**
     * Get the selected entity query based on model id for a given set of entities
     * @param facetInfo           PeerGroupFacetHelper
     * @param selectedEntities    List of selected entities
     * @param modelId             Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @return
     */
    public static String getSelectedEntityQuery(PeerGroupFacetHelper facetInfo,
                                                String[] selectedEntities,
                                                int modelId) {
        StringBuilder queryField = new StringBuilder();
        if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
            AdFacetHelper ad = (AdFacetHelper) facetInfo;
            queryField.append(" AND cefSignatureId LIKE '%").append(ad.cefSignature().replace("*","_")).append("%'");
        } else if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)){
            WebFacetHelper web = (WebFacetHelper) facetInfo;
            String webQuery = web.query();
            if(webQuery != null && webQuery.length() > 0)
                queryField.append(" AND ").append(web.query());
        }else if(Constants.AdNoveltyDetectorTuple()._1().equals(modelId)){
            AdNoveltyDectectorFacetHelper ad = (AdNoveltyDectectorFacetHelper) facetInfo;
            queryField.append(" AND cefSignatureId LIKE '%").append(ad.cefSignature().replace("*","_")).append("%'");
        }

        queryField.append(" AND ").append(facetInfo.entityLabel()).append(" IN  (").
                append(SearchUtils.getCommaSeparatedString(selectedEntities, modelId)).append(" ) ");
        return queryField.toString();
    }

    /**
     * Form the query based on the query parameters passed in input request
     * @param queryParams     List of query params
     * @return
     */
    public static String populateQueryParams(Map<String, List<String>> queryParams) {
        if(queryParams != null && queryParams.size() > 0) {

            StringBuilder strBuilder = new StringBuilder();

            Set<String> keys = queryParams.keySet();

            for (String param : keys) {
                List<String> paramValues = queryParams.get(param);
                if(paramValues != null && paramValues.size() > 0) {
                    strBuilder.append(" AND ").append(param).append(" IN (");
                    for(String value: paramValues) {
                        strBuilder.append("'").append(ClientUtils.escapeQueryChars(value)).append("',");
                    }
                    strBuilder.deleteCharAt(strBuilder.length() - 1).append(")");
                }
            }
            //strBuilder.deleteCharAt(strBuilder.length() - 1);
            return strBuilder.toString();
        } else {
            return "";
        }
    }

    /**
     * Method to get the facet counts and summary information
     * @param modelId               Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param tableName             Table to be queried
     * @param facetList             List of Summary fields corresponding to model
     * @param whereClause           Where clause of part of the query
     * @param summaryFacetLimit     Max no of records to be fetched
     * @param cache                 FeatureServiceCache
     * @param conf                  FeatureServiceConfiguration
     * @return
     */
    public static List<Map<String, List<Map<String, String>>>> getFacetsAndCountsSummary(int modelId,
                                                                                            String tableName,
                                                                                            List<String> facetList,
                                                                                            String whereClause,
                                                                                            int summaryFacetLimit,
                                                                                            long totalRecords,
                                                                                            FeatureServiceCache cache,
                                                                                            FeatureServiceConfiguration conf)
            throws Exception{
        return getFacetsAndCountsSummary_v1(modelId, tableName, facetList, whereClause, summaryFacetLimit, totalRecords, cache, conf);
    }
    public static List<Map<String, List<Map<String, String>>>> getFacetsAndCountsSummary_v2(int modelId,
                                                                                         String tableName,
                                                                                         List<String> facetList,
                                                                                         String whereClause,
                                                                                         int summaryFacetLimit,
                                                                                         long totalRecords,
                                                                                         FeatureServiceCache cache,
                                                                                         FeatureServiceConfiguration conf)
            throws Exception{
        String sql = getFacetQuery_v1(tableName,facetList, whereClause, summaryFacetLimit);
        List<Map<String, String>> results = executeQuery(sql, conf, tableName, true);
        return computeSummary_v1(modelId, cache, results, facetList);
    }
    public static List<Map<String, List<Map<String, String>>>> getFacetsAndCountsSummary_v1(int modelId,
                                                                                         String tableName,
                                                                                         List<String> facetList,
                                                                                         String whereClause,
                                                                                         int summaryFacetLimit,
                                                                                         long totalRecords,
                                                                                         FeatureServiceCache cache,
                                                                                         FeatureServiceConfiguration conf)
            throws Exception{
        String sql = getFacetQuery_v1(tableName,facetList, whereClause, summaryFacetLimit);
        List<Map<String, String>> results = executeQuery(sql, conf, tableName, true);
        if(results!=null) {
            return computeSummary_v1(modelId, cache, results, facetList);
        }else{
            return Lists.newLinkedList();
        }
    }
    public static List<Map<String,  List<Map<String, String>>>> computeSummary_v1(int modelId, FeatureServiceCache cache,
                                                                                   List<Map<String, String>> rows,
                                                                                   List<String> facetList){
        Map<String, Map<String, Integer>> facetMap = new HashMap<>();
        Map<String, Integer> facetTotal = new HashMap<>();
        for(Map<String, String> row: rows){
            for(String facet: facetList){
                String columnName = facet;
                //count name is lower case here
                String columnCountName = facet.toLowerCase()+"count";
                String columnValue = row.get(columnName);
                String columnCount = row.get(columnCountName);
                if(columnValue!=null && columnCount!=null && !columnCount.equals("0")) {
                    Map<String, Integer> countMap;
                    if (facetMap.containsKey(facet)) {
                        countMap = facetMap.get(facet);
                    } else {
                        countMap = new HashMap<>();
                    }
                    if (countMap.containsKey(columnValue)) {
                        countMap.put(columnValue, Integer.valueOf(columnCount) + countMap.get(columnValue));
                    } else {
                        countMap.put(columnValue, Integer.valueOf(columnCount));
                    }
                    facetMap.put(facet, countMap);
                    if (facetTotal.containsKey(facet)) {
                        facetTotal.put(facet, Integer.valueOf(columnCount) + facetTotal.get(facet));
                    } else {
                        facetTotal.put(facet, Integer.valueOf(columnCount));
                    }
                }
            }
        }
        Map<String, Map<String,  List<Map<String, String>>>> retMap = new HashMap<>();
        List<Map<String,  List<Map<String, String>>>> retList = new LinkedList<>();
        for(String facet : facetMap.keySet()){
            String localizedName = SearchUtils.getLocalizedFacetName(facet, modelId, cache);
            List<Map<String, String>> list;
            if(retMap.containsKey(localizedName)){
                list = retMap.get(localizedName).get(localizedName);
            }else{
                list = new LinkedList<>();
                Map<String,  List<Map<String, String>>> map = new HashMap<>();
                map.put(localizedName, list);
                retList.add(map);
            }
            Map<String, Integer> valueCount = facetMap.get(facet);
            for(String value: valueCount.keySet()){
                int count = valueCount.get(value);
                HashMap<String, String> sm = new HashMap<>();
                sm.put("value", value);
                sm.put("count", String.valueOf(count));
                sm.put("fieldName", facet);
                sm.put("percent", String.valueOf(((double)count)/((double)facetTotal.getOrDefault(facet, count))*100));
                list.add(sm);
            }
        }
        return retList;
    }
    public static String getFacetQuery_v1(String tableName,
                                       List<String> facetList,
                                       String whereClause,
                                       int summaryFacetLimit){
        //This code will run only one query to fetch the data.
        StringBuilder sql = new StringBuilder();
        StringBuilder groupBy = new StringBuilder(" GROUP BY ");
        StringBuilder orderBy = new StringBuilder(" ORDER BY ");

        sql.append("SELECT ");
        boolean appendComa = false;

        for(String facet: facetList){
            if(appendComa){
                sql.append(", ");
                groupBy.append(", ");
                orderBy.append(", ");
            }
            String countName = facet+"Count";
            sql.append(facet).append(", COUNT( ").append(facet).append(") as ").append(countName);
            groupBy.append(facet);
            orderBy.append(countName);
            appendComa=true;
        }
        sql.append(" FROM " )
                .append(tableName)
                .append(" ")
                .append(whereClause)
                .append(groupBy)
                .append(orderBy);
        //.append(" LIMIT ")
        //.append(summaryFacetLimit);
        return sql.toString();
    }
    //this method is public only for testing
    public static String getFacetQuery_v2(String tableName,
                                        List<String> facetList,
                                        String whereClause,
                                        int summaryFacetLimit){
        //This code will run only one query to fetch the data.
        StringBuilder sql = new StringBuilder();
        // The sql we build for facets is something like the following
        /*
         SELECT "requestMethod" as facet, requestMethod as facet_value, COUNT(requestMethod) as facet_count, SUM(COUNT(requestMethod)) OVER() AS total_count, from E8SEC.WEB_PROXY_PARQUET where sourceNameOrIp like "%192.168.12.18%" AND
     STARTTIMEISO >=  '2016-10-01T00:00:00.000Z' AND STARTTIMEISO < '2016-10-29T00:00:00.000Z' GROUP BY requestMethod
 UNION ALL
     SELECT "sourceUserName" as facet, sourceUserName as facet_value, COUNT(sourceUserName) as facet_count,  SUM(COUNT(sourceUserName)) OVER() AS total_count, from E8SEC.WEB_PROXY_PARQUET where sourceNameOrIp like "%192.168.12.18%" AND
         STARTTIMEISO >=  '2016-10-01T00:00:00.000Z' AND STARTTIMEISO < '2016-10-29T00:00:00.000Z'  GROUP BY sourceUserName
 UNION ALL
     SELECT "requestClientApplication" as facet, requestClientApplication as facet_value, COUNT(requestClientApplication) as facet_count,  SUM(COUNT(requestClientApplication)) OVER() AS total_count, from E8SEC.WEB_PROXY_PARQUET where sourceNameOrIp like "%192.168.12.18%" AND
         STARTTIMEISO >=  '2016-10-01T00:00:00.000Z' AND STARTTIMEISO < '2016-10-29T00:00:00.000Z' GROUP BY requestClientApplication
  UNION ALL
     SELECT "responseContentType" as facet, responseContentType as facet_value, COUNT(responseContentType) as facet_count, SUM(COUNT(responseContentType)) OVER() AS total_count, from E8SEC.WEB_PROXY_PARQUET where sourceNameOrIp like "%192.168.12.18%" AND
         STARTTIMEISO >=  '2016-10-01T00:00:00.000Z' AND STARTTIMEISO < '2016-10-29T00:00:00.000Z' GROUP BY responseContentType
 UNION ALL
     SELECT "requestScheme" as facet, requestScheme as facet_value, COUNT(requestScheme) as facet_count, SUM(COUNT(requestScheme)) OVER() AS total_count, from E8SEC.WEB_PROXY_PARQUET where sourceNameOrIp like "%192.168.12.18%" AND
         STARTTIMEISO >=  '2016-10-01T00:00:00.000Z' AND STARTTIMEISO < '2016-10-29T00:00:00.000Z' GROUP BY requestScheme
         */

        boolean appendUnionAll = false;
        for(String facet: facetList){
            if(appendUnionAll){
                sql.append("\n UNION ALL \n");
            }

            sql.append(String.format("SELECT \"%s\" as facet, %s as facet_value, COUNT(%s) as facet_count, SUM(COUNT(%s)) OVER() AS total_count FROM %s %s GROUP BY %s ORDER BY %s LIMIT %s",
                    facet, facet, facet, facet, tableName, whereClause, facet, facet, summaryFacetLimit));
            appendUnionAll =true;
        }
        return sql.toString();
    }
    private static List<Map<String,  List<Map<String, String>>>> computeSummary_v2(int modelId, FeatureServiceCache cache,
                                                                                List<Map<String, String>> rows, List<String> facetList){
        List<Map<String,  List<Map<String, String>>>> list = new LinkedList<>();
        Map<String, Map<String,  List<Map<String, String>>>> map = new HashMap<>();
        for(Map<String, String> row: rows) {
            String facet = row.get("facet");
            int facetCount = Integer.valueOf(row.get("facet_count"));
            String facetValue = row.get("facet_value");
            int total_count = Integer.valueOf(row.get("total_count"));
            if(facetCount>0 && facetValue!=null) {
                String localizedName = SearchUtils.getLocalizedFacetName(facet, modelId, cache);
                Map<String, List<Map<String, String>>> lmap;
                List<Map<String, String>> llist;
                if (map.containsKey(localizedName)) {
                    lmap = map.get(localizedName);
                } else {
                    lmap = new HashMap<>();
                    list.add(lmap);
                    map.put(localizedName, lmap);
                }
                if (lmap.containsKey(localizedName)) {
                    llist = lmap.get(localizedName);
                } else {
                    llist = new LinkedList<>();
                    lmap.put(localizedName, llist);
                }
                HashMap<String, String> sm = new HashMap<>();
                sm.put("value", facetValue);
                sm.put("count", String.valueOf(facetCount));
                sm.put("fieldName", facet);
                sm.put("percent", String.valueOf(((double)facetCount/(double)total_count)*100));
                llist.add(sm);
            }
        }
        return list;
    }

    /**
     * Form the query to fetch the facet counts
     * @param fieldName      Field for which counts to be fetched
     * @param tableName      Table to be queried
     * @param whereClause    Where clause of the query
     * @param facetLimit     Max no of records to be fetched
     * @param conf           FeatureServiceConfiguration
     * @return
     */
    public static List<Map<String, String>> getFacetsAndCounts( String fieldName,
                                                                String tableName,
                                                                String whereClause,
                                                                int facetLimit,
                                                                long totalRecords,
                                                                FeatureServiceConfiguration conf)
            throws Exception{

        List<Map<String, String>> facetList;
        //String tableName = SearchUtils.getImpalaTableName(modelId);
        //String timeFilter = " WHERE STARTTIMEISO >= " + startTime + " AND  STARTTIMEISO<=" + endTime;

        //int totalRecords = getTotalRecordsForFilter(tableName, whereClause,conf);

        String sql = "SELECT " + fieldName +" , COUNT(1) AS COUNT FROM " + tableName +
                whereClause + " GROUP BY " + fieldName + " ORDER BY COUNT DESC " +
                " LIMIT " + facetLimit;

        facetList = executeFacetQuery(sql, fieldName, totalRecords, conf);

        return facetList;
    }

    /**
     * Form the complete impala query to be executed using the
     * @param modelId         Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param startRecord     Start record starting from which data needs to be fetched
     * @param endRows         Max no of records to be fetched
     * @param sortField       Field on which data needs to be sorted
     * @param sortOrder       Sort order (asc/desc)
     * @param whereClause     Where clause of the query
     * @param conf            FeatureServiceConfiguration
     * @return
     */
    public static String getImpalaQuery(int modelId,
                                        int startRecord,
                                        int endRows,
                                        String sortField,
                                        String sortOrder,
                                        String whereClause,
                                        FeatureServiceConfiguration conf) {

        String tableName = getImpalaTableName(modelId, conf);

        return String.format("SELECT * FROM %s %s ORDER BY %s %s LIMIT %s OFFSET %s", tableName, whereClause, sortField, sortOrder, endRows, startRecord);

    }

    /**
     * Utility method to return the impala table names based on model Id
     * @param modelId Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param conf    FeatureServiceConfiguration
     * @return
     */
    public static String getImpalaTableName(int modelId, FeatureServiceConfiguration conf) {
        ImpalaConfiguration impalaConf = conf.getImpalaConfiguration();
        if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) ||
                Constants.WebAnomalyProfileTuple()._1().equals(modelId)) {
            return impalaConf.getDatabaseName()+"."+impalaConf.getWebProxyParquetTable();
        } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) ||
                Constants.AdAnomalyProfileTuple()._1().equals(modelId) ||
                Constants.AdNoveltyDetectorTuple()._1().equals(modelId)) {
            return impalaConf.getDatabaseName()+"."+impalaConf.getIamParquetTable();
        }
        return impalaConf.getDatabaseName()+"."+impalaConf.getWebProxyParquetTable();
    }

    /**
     * Form the query to fetch autocomplete results based on the field, incoming string and date range
     * @param fieldName          Field for which results are to be fetched
     * @param tableName          Table to be queried
     * @param startRecord        start record for fetching (if result spans over multiple pages)
     * @param endRows            Max records to be fetched
     * @param sortField          Field on which results to eb sorted
     * @param sortOrder          Sort order asc or desc
     * @param incomingString     incoming string for which matching results are to be fetched
     * @param startTime          String specifying start Time in the query
     * @param endTime            String specifying end Time in the query
     * @return
     */

    public static String getAutoCompleteQuery(String fieldName,
                                              String tableName,
                                              int startRecord,
                                              int endRows,
                                              String sortField,
                                              String sortOrder,
                                              String incomingString,
                                              String startTime,
                                              String endTime) {

       return String.format("SELECT DISTINCT(%s) FROM %s WHERE %s RLIKE '^%s.*' AND  STARTTIMEISO >= '%s' AND STARTTIMEISO < '%s' ORDER BY %s %s LIMIT %s OFFSET %s",
                fieldName, tableName, fieldName, incomingString,startTime, endTime, fieldName, sortOrder, endRows, startRecord);

    }
}