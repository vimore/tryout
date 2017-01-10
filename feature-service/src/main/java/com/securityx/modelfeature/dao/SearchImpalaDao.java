package com.securityx.modelfeature.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.ImpalaClient;
import com.securityx.modelfeature.common.inputs.TimeSeriesInput;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.utils.*;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Recreates the search queries from Solr in Impala.
 * Created by preetitambakhe on 27/09/16.
 */
public class SearchImpalaDao extends SearchDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchImpalaDao.class);
    private FeatureServiceConfiguration conf = null;
    //private FeatureServiceCache cache = null;

    public SearchImpalaDao(FeatureServiceConfiguration conf) {
        //this.cache = cache;
        this.conf = conf;
    }

    /** Method to Fetch raw logs data from impala for sources and destinations
     * @param modelId            Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param sources            List of source user names or Ips
     * @param destinations       List of destination user names or ips
     * @param queryParams        List of query parameters corresponding to which results need to be fetched
     * @param startTime          String specifying start Time in the query
     * @param endTime            String specifying end Time in the query
     * @param summaryFacetLimit  Integer to specify the max no facet values
     * @param endRows            No of records per page
     * @param pageNo             Page no for which data needs to fetched/displayed
     * @param sortField          String to specify the field on which the results need to be sorted
     * @param sortOrder          String to specify sort order (asc or desc)
     * @param summarize          Boolean to indicate whether facet summary is to be fetched or not
     * @param cache              FeatureServiceCache
     * @return
     */
    public List<Map<String, Object>> getSearchResultsForSourceDestination(int modelId, List<String> sources, List<String> destinations,
                                                                          Map<String, List<String>> queryParams,
                                                                          String startTime, String endTime, int summaryFacetLimit, int endRows, int pageNo,
                                                                          String sortField, String sortOrder, boolean summarize,
                                                                          FeatureServiceCache cache) {

        List<Map<String, Object>> results = new LinkedList<>();
        try {
            List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache);
            String tableName = ImpalaUtils.getImpalaTableName(modelId, conf);

            int startRecord = endRows * (pageNo - 1);

            String whereClause = populateSourceDestinationQueryParams(startTime, endTime, sources, destinations, queryParams);

            String query = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder, whereClause, conf);

            if (query.length() == 0) {
                return results;
            }
            Map<String, Object> resultMap = new HashMap<>();

            resultMap.put("result", ImpalaUtils.executeQuery(query, tableName, conf));
            long totalRecords = ImpalaUtils.getTotalRecordsForFilter(tableName, whereClause, conf);
            resultMap.put("total", totalRecords);
            if (summarize)  {
                if(totalRecords > 0)
                    resultMap.put("summary", ImpalaUtils.getFacetsAndCountsSummary(modelId, tableName, facetList, whereClause, summaryFacetLimit, totalRecords, cache, conf));
                else
                    resultMap.put("summary", Lists.newLinkedList());
            }
            results.add(resultMap);

        } catch (Exception ex) {
            LOGGER.error("Could not load the data for query: {}", ex.getMessage(), ex);
        }
        return results;
    }

    /** Method to build the where clause for fetching raw logs data for the given sources/destinations
     *  based on input query parameters
     * @param startTime     String specifying start Time in the query
     * @param endTime       String specifying end Time in the query
     * @param sources       List of source usernames/Ips for which logs to be fetched
     * @param destinations  List of destination usernames/Ips for which logs need to be fetched
     * @param queryParams   List of additional query/filter parameters for filtering results
     * @return
     */
    public static String populateSourceDestinationQueryParams(String startTime, String endTime, List<String> sources,
                                                              List<String> destinations, Map<String, List<String>> queryParams) {

        StringBuilder whereClauseBuilder = new StringBuilder();
        whereClauseBuilder.append(" WHERE STARTTIMEISO >= '").
                append(startTime).append("' AND STARTTIMEISO < '").append(endTime).append("'");
        if (sources != null) {
            String sourceQuery = getSourceDestinationQuery(sources, SearchUtils.getSourceIpFieldName());
            if (sourceQuery != null && !sourceQuery.isEmpty()) {
                whereClauseBuilder.append(" AND ").append(sourceQuery);
            }
        }

        if (destinations != null) {
            String destQuery = getSourceDestinationQuery(destinations, SearchUtils.getDestinationIpFieldName());
            if (destQuery != null && !destQuery.isEmpty()) {
                whereClauseBuilder.append(" AND ").append(destQuery);
            }
        }
        whereClauseBuilder.append(ImpalaUtils.populateQueryParams(queryParams));
        return whereClauseBuilder.toString();

    }

    /** Method to Form the sources/destinations part of query based on input parameters to fetch raw logs
     * @param sources     List of source/destination users/IPs
     * @param fieldName   FieldName corresponding to which the filter query is to be formed
     * @return
     */
    private static String getSourceDestinationQuery(List<String> sources, String fieldName) {
        String query = "";
        if (sources != null && !sources.isEmpty()) {
            StringBuilder sourceQuery = new StringBuilder();
            for (String source : sources) {
                source = source.replace("\"", "");
                String q = "'" + source + "'";
                if (sourceQuery.length() == 0) {
                    sourceQuery.append(q);
                } else {
                    sourceQuery.append(" , ").append(q);
                }
            }
            if (!sourceQuery.toString().isEmpty()) {
                query = fieldName + " IN " + "(" + sourceQuery.toString() + ")";
            }
        }
        return query;
    }

    /** Method to fetch raw logs from impala for selected entities and behaviors
     * @param modelId            Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param securityEventId    Integer specifying securityEventId/Feature Id
     * @param queryParams        JSONObject format {facetFieldName: comma separated values}
     * @param selectedEntities   String[] This is a list of entities (ip/usernames)
     * @param keywords           String[] This is a list of keywords that contain values in targerId
     * @param startTime          String specifying start Time in the query
     * @param endTime            String specifying end Time in the query
     * @param summaryFacetLimit  Integer to specify the max no facet values
     * @param endRows            Integer specifying number of rows in the result
     * @param pageNo             Page no for which data needs to fetched/displayed
     * @param sortField          String to specify the field on which the results need to be sorted
     * @param sortOrder          String to specify sort order (asc or desc)
     * @param summarize          Boolean to indicate whether facet summary is to be fetched or not
     * @param cache              FeatureServiceCache
     * @return
     */
    public List<Map<String, Object>> getFacetedSearchResults(int modelId, int securityEventId,
                                                             Map<String, List<String>> queryParams, String[] selectedEntities, String[] keywords,
                                                             String startTime, String endTime, int summaryFacetLimit, int endRows, int pageNo,
                                                             String sortField, String sortOrder, boolean summarize,
                                                             FeatureServiceCache cache) {
        List<Map<String, Object>> results = new LinkedList<>();
        try {
            List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache);
            String tableName = ImpalaUtils.getImpalaTableName(modelId, conf);

            int startRecord = endRows * (pageNo - 1);

            String whereClause = populateFacetSearchQueryParams(startTime, endTime, modelId, securityEventId, queryParams, selectedEntities, cache);

            String query = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder, whereClause, conf);

            if (query.length() == 0) {
                return results;
            }
            Map<String, Object> resultMap = new HashMap<>();

            resultMap.put("result", ImpalaUtils.executeQuery(query, tableName, conf));
            long totalRecords = ImpalaUtils.getTotalRecordsForFilter(tableName, whereClause, conf);
            resultMap.put("total", totalRecords);
            if (summarize) {
                if(totalRecords > 0)
                    resultMap.put("summary", ImpalaUtils.getFacetsAndCountsSummary(modelId, tableName, facetList, whereClause, summaryFacetLimit, totalRecords, cache, conf));
                else
                    resultMap.put("summary", Lists.newLinkedList());
            }
            results.add(resultMap);

        } catch (Exception ex) {
            LOGGER.error("Could not load the data for query: {}", ex.getMessage(), ex);
        }
        return results;
    }

    /** Method to build the where clause for fetching raw logs data for the selected entities and/or behaviours
     *  based on input query parameters
     * @param startTime          String specifying start Time in the query
     * @param endTime            String specifying end Time in the query
     * @param modelId            Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param securityEventId    Integer specifying security event id
     * @param queryParams        JSONObject format {facetFieldName: comma separated values}
     * @param selectedEntities   List of selected entities
     * @param cache              FeatureServiceCache
     * @return
     */
    public static String populateFacetSearchQueryParams(String startTime, String endTime, int modelId, int securityEventId,
                                                        Map<String, List<String>> queryParams, String[] selectedEntities, FeatureServiceCache cache) {

        StringBuilder query = new StringBuilder();
        //String tableName = SearchUtils.getImpalaTableName(modelId);
        PeerGroupFacetHelper facetInfo = cache.getPeerFacetHelperFromModelIdSecEventIdForImpala(modelId, securityEventId);
        //AdFacetHelper ad = (AdFacetHelper) facetInfo;

        query.append(" WHERE STARTTIMEISO >= '").append(startTime).append("' AND STARTTIMEISO <'").append(endTime).append("' ")
                .append(ImpalaUtils.getSelectedEntityQuery(facetInfo, selectedEntities, modelId))
                .append(ImpalaUtils.populateQueryParams(queryParams));

        return query.toString();
    }

    /** Method to fetch raw logs from impala for a given model Id and filter parameters
     * @param modelId            Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param queryParams        JSONObject format {facetFieldName: comma separated values}
     * @param sourceAddress      Source Address
     * @param startTime          String specifying start Time in the query
     * @param endTime            String specifying end Time in the query
     * @param summaryFacetLimit  Integer to specify the max no facet values
     * @param endRows            Integer specifying number of rows in the result
     * @param pageNo             Page no for which data needs to fetched/displayed
     * @param sortField          String to specify the field on which the results need to be sorted
     * @param sortOrder          String to specify sort order (asc or desc)
     * @param summarize          Boolean to indicate whether facet summary is to be fetched or not
     * @param cache              FeatureServiceCache
     * @return
     */
    public List<Map<String, Object>> getWebAnomalyFacetedSearchResults(int modelId, Map<String, List<String>> queryParams, String sourceAddress,
                                                                       String startTime, String endTime, int endRows, int pageNo, int summaryFacetLimit,
                                                                       String sortField, String sortOrder, boolean summarize, FeatureServiceCache cache) {
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        try {
            List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache);
            String tableName = ImpalaUtils.getImpalaTableName(modelId, conf);

            int startRecord = endRows * (pageNo - 1);
            StringBuilder queryBuilder = new StringBuilder();

            String whereClause = populateWebAnomalyQueryParams(startTime, endTime, modelId, queryParams, sourceAddress);

            String query = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder, whereClause, conf);
            if (query.length() == 0) {
                return results;
            }
            Map<String, Object> resultMap = new HashMap<String, Object>();

            resultMap.put("result", ImpalaUtils.executeQuery(query, tableName, conf));
            long totalRecords = ImpalaUtils.getTotalRecordsForFilter(tableName, whereClause, conf);
            resultMap.put("total", totalRecords);
            if (summarize) {
                if(totalRecords > 0)
                    resultMap.put("summary", ImpalaUtils.getFacetsAndCountsSummary(modelId, tableName, facetList, whereClause, summaryFacetLimit, totalRecords, cache, conf));
                else
                    resultMap.put("summary", Lists.newLinkedList());
            }
            results.add(resultMap);
        } catch (Exception ex) {
            LOGGER.error("Could not load the data for query: {}", ex.getMessage(), ex);
        }

        return results;
    }

    /** Method to build the where clause for fetching raw logs data for the source and query parameters
     * @param startTime       String specifying start Time in the query
     * @param endTime         String specifying end Time in the query
     * @param modelId         Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param queryParams     JSONObject format {facetFieldName: comma separated values}
     * @param sourceAddress   Source Address
     * @return
     */
    public static String populateWebAnomalyQueryParams(String startTime, String endTime, int modelId, Map<String, List<String>> queryParams, String sourceAddress) {

        StringBuilder queryBuffer = new StringBuilder();
        queryBuffer.append(" WHERE STARTTIMEISO >= '").append(startTime).append("' AND STARTTIMEISO < '").append(endTime).append("' ");
        //String facetQuery="";
        if (queryParams != null && !queryParams.isEmpty()) {
            for (String facet : queryParams.keySet()) {
                StringBuilder valueList = new StringBuilder();
                List<String> queryValues = queryParams.get(facet);
                if (queryValues != null && queryValues.size() > 0) {
                    for (String value : queryValues) {
                        if (value != null && !value.isEmpty()) {
                            valueList.append("'").append(value).append("'").append(",");
                        }
                    }
                }
                if (!valueList.toString().isEmpty()) {
                    String field = SearchUtils.getInternalFieldName(facet.toString());
                    String key = field + " IN (" + valueList.deleteCharAt(valueList.length() - 1).toString() + ")";
                    if (queryBuffer.length() == 0) {
                        queryBuffer.append(key);
                    } else {
                        queryBuffer.append(" AND ").append(key);
                    }
                }
            }
        }
        String q = "";
        if (sourceAddress != null && !sourceAddress.isEmpty()) {

            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.WebAnomalyProfileTuple()._1().equals(modelId)) {
                q = SearchUtils.getSourceIpFieldName() + "=" + "\"" + sourceAddress + "\"";
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.AdAnomalyProfileTuple()._1().equals(modelId)) {
                q = "(( " + SearchUtils.getSourceUserNameFieldName() + "=" + "\"" + sourceAddress + "\"" + " ) " +
                        " OR " + "( " + SearchUtils.getDestinationUserNameFieldName() + "=" + "\"" + sourceAddress + "\"" + " ) )";
            }

        }

        if (queryBuffer.length() == 0) {
            queryBuffer.append(q);
        } else {
            queryBuffer.append(" AND ").append(q);
        }

        LOGGER.debug("Impala Query String: " + queryBuffer.toString());
        return queryBuffer.toString();
    }

    /** Method to form the search query to fetch raw logs for TimeSeries/Access
     * @param input    TimeSeriesInput with input parameters from user request
     * @param cache    FeatureServiceCache
     * @return
     */
    public static String getTimeSeriesSearchQueryForAd(TimeSeriesInput input, FeatureServiceCache cache) {
        List<Map<String, Object>> results = null;

        StringBuilder impalaQueryString = new StringBuilder();

        String startTime = input.getStartTime();
        String endTime = input.getEndTime();
        int modelId = input.getModelId();
        List<Map<String, String>> searchForList = input.getFacets();

        for (int i = 0; i < searchForList.size(); i++) {
            Map<String, String> map = searchForList.get(i);
            String typeField = map.get("typeField");
            String group = map.get("group");
            String groupField = map.get("groupId");
            //val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
            String queryString = "";
            Map<Integer, Set<Integer>> errorStatusMap = cache.getDescribeLoader().errorStatusMap;
            if (typeField.equalsIgnoreCase("error") || typeField.equalsIgnoreCase("Error Status") ||
                    typeField.equalsIgnoreCase("error_description")) {
                if (group != null && group.equalsIgnoreCase("success")) {
                    queryString = "eventLogType = " + "\"Success Audit\"";
                } else {
                    if (groupField == null) {
                        if (group != null) {
                            if (group.equalsIgnoreCase("failure")) {
                                //if failure, look at the event id
                                queryString = "eventLogType = " + "\"Failure Audit\"";
                            }
                        } else {
                            queryString = "eventLogType IN " + "(\"Success Audit\"" + " , " + "\"Failure Audit\")";
                        }
                    } else {
                        if (group != null) {
                            if (group.equalsIgnoreCase("failure")) {
                                //if failure, look at the event id
                                boolean isInt = NumberUtils.isNumber(groupField);
                                if (isInt) {
                                    StringBuilder buffer = new StringBuilder();
                                    Set<Integer> eventSet = cache.getDescribeLoader().errorStatusMap.get(Integer.valueOf(groupField));
                                    Iterator<Integer> iter = eventSet.iterator();
                                    StringBuilder queryBuf = new StringBuilder();
                                    while (iter.hasNext()) {
                                        Integer event = iter.next();
                                        String evtString = event.toString();
                                        if (queryBuf.length() == 0) {
                                            queryBuf.append(evtString);
                                        } else {
                                            queryBuf.append("|" + evtString);
                                        }
                                    }
                                    queryString = "cefSignatureId RLIKE '.*(" + queryBuf.toString() + ").*'";
                                }
                            }
                        } else {
                            queryString = "eventLogType IN " + "(\"Success Audit\"" + " , " + "\"Failure Audit\")";
                        }
                    }
                }

            } else if (typeField.equalsIgnoreCase("Events") || typeField.equalsIgnoreCase("Event") ||
                    typeField.equalsIgnoreCase("event_id")) {
                Map<String, List<Integer>> catToEvent = cache.getDescribeLoader().categoryToEvent;
                if (groupField == null) {
                    if (group != null) {
                        //if we know the group, we will find the valid cefSignatureIds
                        if (catToEvent.containsKey(group)) {
                            List<Integer> list = catToEvent.get(group);
                            StringBuilder queryBuf = new StringBuilder();
                            for (i = 0; i < list.size(); i++) {
                                String evtString = list.get(i).toString();
                                if (queryBuf.length() == 0) {
                                    queryBuf.append(evtString);
                                } else {
                                    queryBuf.append("|").append(evtString);
                                }
                            }
                            queryString = "cefSignatureId RLIKE '.*(" + queryBuf.toString() + ").*'";
                        }
                    } else {
                        //we will query for all the cer signature ids.
                        Set<Integer> eventSet = cache.getDescribeLoader().eventIdToNameMap.keySet();
                        Iterator<Integer> iter = eventSet.iterator();
                        StringBuffer queryBuf = new StringBuffer();
                        while (iter.hasNext()) {
                            Integer event = iter.next();
                            String evtString = event.toString();
                            if (queryBuf.length() == 0) {
                                queryBuf.append(queryString);
                            } else {
                                queryBuf.append(" | ").append(queryString);
                            }
                        }
                        queryString = "cefSignatureId RLIKE '.*(" + queryBuf.toString() + ").*'";
                    }
                } else {
                    //groupField = event id. So use the group field directly for cefSignatureQuery
                    queryString = "cefSignatureId LIKE " + "'%" + groupField + "%'";
                }

            } else if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
                String searchField = SearchUtils.getSrcDestFieldName(Constants.ADPeerAnomaliesModelTuple()._1, typeField.toLowerCase());
                queryString = searchField + " LIKE '%" + groupField + "%'";
            } /*else {
                queryString = "*:*";
            } */

            if (!queryString.isEmpty()) {
                if (impalaQueryString.length() == 0) {
                    impalaQueryString.append("( ").append(queryString).append(" )");
                } else {
                    impalaQueryString.append(" OR ( ").append(queryString).append(" )");
                }
            }

        }

        /*results = getTimeSeriesSearchResults(modelId, impalaQueryString.toString(), input.getQueryParams(), startTime, endTime,
                        input.getNumRows(), input.getPageNo(), input.getFacetLimit(), input.getSortField(), input.getSortOrder(),
                        input.isSummarize(), cache);


        return results;*/

        String whereClause = " WHERE STARTTIMEISO >= '" + startTime + "' AND STARTTIMEISO < '" + endTime + "' " +
                ImpalaUtils.populateQueryParams(input.getQueryParams()) + " AND " + impalaQueryString;

        return whereClause;
    }

    /** Method to form the search query to fetch raw logs for TimeSeries/Http
     * @param input    TimeSeriesInput with input parameters from user request
     * @param cache    FeatureServiceCache
     * @return
     */

    public static String getTimeSeriesSearchQueryForHttpSeries(TimeSeriesInput input, FeatureServiceCache cache) {

        //List<Map<String, Object>> results = null;

        //Map buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
        StringBuilder impalaQueryString = new StringBuilder();
        String startTime = input.getStartTime();
        String endTime = input.getEndTime();
        int modelId = input.getModelId();
        List<Map<String, String>> searchForList = input.getFacets();
        for (int i = 0; i < searchForList.size(); i++) {
            Map<String, String> map = searchForList.get(i);
            String typeField = map.get("typeField");
            String group = map.get("group");
            String groupField = map.get("groupId");
            String queryString = "";
            if (typeField != null) {
                //String typeString = typeField;
                if (typeField.equalsIgnoreCase("responseContentType") || typeField.equalsIgnoreCase("requestScheme")) {
                    if (groupField != null) {
                        queryString = typeField + " LIKE  '%" + groupField + "%' ";
                    }
                } else if (groupField == null) {
                    if (group != null) {
                        //get all the corresponding groupIds from given group
                        Map<Integer, String> localization = cache.getDescribeLoader().localizationMap;
                        Map<String, Map<String, Integer>> typeToGroupFieldToGroup = cache.getDescribeLoader().httpFieldToGroupToSubcategory;
                        StringBuilder orOperatorString = new StringBuilder();
                        //get map of groupID to groupfield
                        Map<String, Integer> grpMap = typeToGroupFieldToGroup.get(typeField);

                        for (Map.Entry<String, Integer> entry : grpMap.entrySet()) {
                            if (localization.containsKey(entry.getValue())) {
                                String groupName = localization.get(entry.getValue());
                                if (groupName.equals(group)) {
                                    String str = "\"" + entry.getKey() + "\"";
                                    if (orOperatorString.length() == 0) {
                                        orOperatorString.append(str);
                                    } else {
                                        orOperatorString.append(" , ").append(str);
                                    }
                                }
                            }
                        }
                        queryString = typeField + " IN (" + orOperatorString + ") ";
                    } /*else {
                        queryString = typeField + ":*";
                    }*/
                } else {
                    if (typeField.equalsIgnoreCase("all") && groupField.equalsIgnoreCase("all")) {
                        queryString = ""; //"*:*";
                    } else if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
                        String searchfield = SearchUtils.getSrcDestFieldName(Constants.WebPeerAnomaliesModelTuple()._1, typeField.toLowerCase());
                        queryString = searchfield + " LIKE '%" + groupField + "%' ";
                    } else {
                        queryString = typeField + " = " + "\"" + groupField + "\" ";
                    }
                }
                if (!queryString.isEmpty()) {
                    if (impalaQueryString.length() == 0) {
                        impalaQueryString.append(queryString);
                    } else {
                        impalaQueryString.append(" AND (").append(queryString).append(") ");
                    }
                }

            }
        }

        LOGGER.debug(" IMPALA QUERY is: " + impalaQueryString);
        /*results = getTimeSeriesSearchResults(modelId, impalaQueryString.toString(), input.getQueryParams(), startTime, endTime,
                        input.getNumRows(), input.getPageNo(), input.getFacetLimit(), input.getSortField(), input.getSortOrder(),
                        input.isSummarize(), cache);



        return results;*/
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(" WHERE STARTTIMEISO >= '" )
                .append(startTime)
                .append( "' AND STARTTIMEISO < '")
                .append(endTime)
                .append("' ")
                .append(ImpalaUtils.populateQueryParams(input.getQueryParams()));
        if(impalaQueryString.length()>0){
            whereClause.append(" AND ").append(impalaQueryString);
        }

        LOGGER.debug(" IMPALA WHERE CLAUSE is: {} ", whereClause);
        return whereClause.toString();

    }

    /** Method to fetch raw logs from Impala for TimeSeries Access/Network
     * @param input
     * @param cache
     * @return
     */
    public List<Map<String, Object>> getTimeSeriesSearchResults(TimeSeriesInput input, FeatureServiceCache cache) {
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        try {
            int endRows = input.getNumRows();
            int pageNo = input.getPageNo();
            int modelId = input.getModelId();
            int startRecord = endRows * (pageNo - 1);
            String tableName = ImpalaUtils.getImpalaTableName(modelId, conf);
            List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache);

            String whereClause = "";

            if (Constants.ADPeerAnomaliesModelTuple()._1.equals(input.getModelId())) {
                whereClause = getTimeSeriesSearchQueryForAd(input, cache);
            } else if (Constants.WebPeerAnomaliesModelTuple()._1.equals(input.getModelId())) {
                whereClause = getTimeSeriesSearchQueryForHttpSeries(input, cache);
            }

            String query = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, input.getSortField(), input.getSortOrder(), whereClause, conf);
            if (query == null || query.length() == 0) {
                return results;
            }

            Map<String, Object> resultMap = new HashMap<String, Object>();

            resultMap.put("result", ImpalaUtils.executeQuery(query, tableName, conf));
            long totalRecords = ImpalaUtils.getTotalRecordsForFilter(tableName, whereClause, conf);
            resultMap.put("total", totalRecords);
            if (input.isSummarize()) {
                if(totalRecords > 0)
                    resultMap.put("summary", ImpalaUtils.getFacetsAndCountsSummary(modelId, tableName, facetList, whereClause,
                        input.getFacetLimit(), totalRecords, cache, conf));
                else
                    resultMap.put("summary", Lists.newLinkedList());
            }
            results.add(resultMap);
            //results.add(resultMap);
        } catch (Exception ex) {
            LOGGER.error("Could not load the data for query: {}", ex.getMessage(), ex);
        }
        return results;
    }

    /** Get the matching source/destination Users/Ips/hosts corresponding to given input string
     * @param modelId          Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param fieldType        FieldType source/destination
     * @param incomingString   String for which matching results are to be searched
     * @param startTime        String specifying start Time in the query
     * @param endTime          String specifying end Time in the query
     * @param startRecord      Start record no in case search results span across multiple pages
     * @param pageSize         No of results to be displayed on one page
     * @return
     */
    public Set<String> getAutoCompleteRecords(int modelId, String fieldType, String incomingString, String startTime,
                                              String endTime, int startRecord, int pageSize) {
        Set<String> outputSet = Sets.newHashSet();
        String fieldName = SearchUtils.getSrcDestFieldName(modelId, fieldType);
        String tableName = ImpalaUtils.getImpalaTableName(modelId, conf);

        String sql = ImpalaUtils.getAutoCompleteQuery(fieldName, tableName, startRecord, pageSize,
                fieldName, "ASC", incomingString, startTime, endTime);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            LOGGER.debug("Executing Impala Query: {}", sql);
            conn = ImpalaClient.getImpalaConnection(conf);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                outputSet.add(rs.getString(1));
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to get logs data from Impala " + ex);
        } finally {
            ImpalaUtils.closeConnections(conn, pstmt, rs);
        }
        LOGGER.debug("Impala returned {} rows for query {}", outputSet.size(), sql);
        return outputSet;
    }

    /** Get the distinct values and counts corresponding to the distinct values for the specified field and search criteria
     * @param modelId         Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param fieldName       Field Name for which distinct values and counts are required (Populated based on model Id)
     * @param startTime       String specifying start Time in the query
     * @param endTime         String specifying end Time in the query
     * @param facetLimit      Max values to be fetched and displayed
     * @param nodFlag         flag to indicate if the method should fetch details for newly observed domains/users
     * @return
     */
    public List<Map<String, Object>> getFacetsAndCounts(int modelId, String fieldName, String startTime, String endTime,
                                                        int facetLimit, boolean nodFlag) {
        List<Map<String, Object>> facetList = Lists.newLinkedList();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String tableName = ImpalaUtils.getImpalaTableName(modelId, conf);
            StringBuilder timeFilter = new StringBuilder(" WHERE STARTTIMEISO >= '" + startTime + "' AND STARTTIMEISO < '" + endTime + "' ");
            if (nodFlag) {
                timeFilter.append(getUserOrDomainForPreviousMonth(startTime, endTime, fieldName, tableName));
            }

            long totalRecords = ImpalaUtils.getTotalRecordsTimeFilter(modelId, startTime, endTime, conf);

            String sql = "SELECT " + fieldName + " , COUNT(1) AS TOTAL FROM " + tableName +
                    timeFilter + " GROUP BY " + fieldName + " ORDER BY TOTAL DESC " +
                    " LIMIT " + facetLimit;
            conn = ImpalaClient.getImpalaConnection(conf);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> facetKeyValues = Maps.newLinkedHashMap();
                String fieldValue = rs.getString(fieldName);
                double fieldCount = rs.getDouble("TOTAL");
                facetKeyValues.put("value", fieldValue);
                facetKeyValues.put("count", fieldCount);
                facetKeyValues.put("percent", MiscUtils.getPercentage(fieldCount, totalRecords));
                if (!Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                    try {
                        CloudChamberDao dao = new CloudChamberDao(conf);
                        String creationDate = dao.getDomainCreationDate(fieldValue);
                        String diff = "";
                        if (creationDate != null) {
                            facetKeyValues.put("creationTime", creationDate);
                            diff = Integer.toString(Days.daysBetween(new DateTime(creationDate), DateTime.now()).getDays());
                        }
                        facetKeyValues.put("ageInDays", diff);
                    } catch (Exception e) {
                        LOGGER.error("Failed to get domain age for: " + fieldValue + " => " + e);
                    }
                }
                facetList.add(facetKeyValues);
            }
        } catch (Exception ex) {
            LOGGER.error("Could not load the data for query: {}", ex.getMessage(), ex);
        }finally {
            ImpalaUtils.closeConnections(conn, pstmt, rs);
        }
        return facetList;
    }

    /** Method to retrieve the users/domains observed in the last month
     * @param startTime   String specifying start Time in the query
     * @param endTime     String specifying end Time in the query
     * @param fieldName   Field name for which data needs to be fetched (populated based on model Id)
     * @param tableName   Impala table name
     * @return
     */
    private String getUserOrDomainForPreviousMonth(String startTime, String endTime, String fieldName, String tableName) {
        String sqlClause = "";

        StringBuilder userOrDomainList = new StringBuilder();
        DateTime dateTime = new DateTime(startTime, DateTimeZone.UTC);
        String oldEndTime = startTime;

        //TODO: number of days "30", should be parameterized last "N" days.
        DateTime oldStartTime = dateTime.minusDays(30);

        String sql = "SELECT DISTINCT(" + fieldName + ") FROM " + tableName + " WHERE STARTTIMEISO >= '" + oldStartTime +
                "' AND STARTTIMEISO < '" + oldEndTime + "' ";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = ImpalaClient.getImpalaConnection(conf);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                userOrDomainList.append("'").append(rs.getString(1)).append("'").append(",");
            }
            userOrDomainList.deleteCharAt(userOrDomainList.length() - 1);

            if (userOrDomainList.length() > 0) {
                sqlClause = " AND " + fieldName + " NOT IN (" + userOrDomainList.toString() + ") ";
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to get logs data from Impala " + ex);
        } finally {
            ImpalaUtils.closeConnections(conn, pstmt, rs);
        }

        return sqlClause;

    }

    /** Get newly observed domains/users over a period of time
     * @param modelId      Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param startTime    String specifying start Time in the query
     * @param endTime      String specifying end Time in the query
     * @param facetLimit   Max no of records to be fetched
     * @param conf         FeatureServiceConfiguration
     * @return
     */
    public List<Map<String, Object>> getNODs(int modelId, String startTime, String endTime,
                                             int facetLimit, FeatureServiceConfiguration conf) {
        String fieldName = "";
        if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
            fieldName = "destinationNameOrIp";
        } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
            fieldName = "destinationUserName";
        }
        return getFacetsAndCounts(modelId, fieldName, startTime, endTime, facetLimit, true);
    }

}
