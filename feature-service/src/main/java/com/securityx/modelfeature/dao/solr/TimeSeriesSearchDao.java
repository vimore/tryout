package com.securityx.modelfeature.dao.solr;

import com.google.common.collect.Sets;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.SearchImpalaDao;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.SearchUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by harish on 12/26/14.
 */
public class TimeSeriesSearchDao {


    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSeriesSearchDao.class);

    /**
     *
     * This is used to support the auto-complete feature for TimeSeries
     *
     * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
     * @param iamSolrClient CloudSolrServer connection for iam_mef collection
     * @param modelId Integer model Id
     * @param fieldType String field type; can be "source" or "destination"
     * @param incomingString String user Input for which we provide auto-complete
     * @param startTime String specifying the startTime
     * @param endTime String specifying the endTime
     * @param pageNo Integer page no.
     * @param pageSize Integer page size
     *
     * @return List of String records as suggestions
     */
    @Deprecated
    public List<String> getAutoCompleteRecords(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                               int modelId, String fieldType,String incomingString,
                                               String startTime, String endTime,
                                               int pageNo, int pageSize ) {
        SolrQuery solrQuery = new SolrQuery();
        int startRecord = 10 * (pageNo - 1);
        solrQuery.setStart(startRecord);
        solrQuery.setRows(pageSize);
        LOGGER.debug("Getting Records from " + startRecord + " to " + (startRecord + pageSize));

        String fieldName = SearchUtils.getAutoFieldName(modelId, fieldType);
        if(fieldName == null || fieldName.isEmpty() ){
            LOGGER.error("Invalid Input: Field Name not found");
            return new LinkedList<String>();
        }

        solrQuery.setQuery(fieldName + ":" + incomingString);
        solrQuery.set("group", true);
        solrQuery.set("group.field", fieldName);
        solrQuery.addSort("startTimeISO", SearchUtils.getSolrSortOrder("desc"));

        String filterQueryString = "startTimeISO:[" + startTime + " TO " + endTime + "]";
        solrQuery.setFilterQueries(filterQueryString);

        QueryResponse response = null;
        try {
            if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = webProxySolrClient.query(solrQuery);
            }else if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = iamSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to get auto complete records for model: " +  modelId + " with => {} ", exp);
        }


        List<String> outputList = new LinkedList<String>();
        if(response != null){

            GroupResponse groupResponse = response.getGroupResponse();
            List<GroupCommand> groupCommands =  groupResponse.getValues();
            if(groupCommands != null){
                for(GroupCommand groupCommand : groupCommands){
                    List<Group> groups = groupCommand.getValues();
                    for(Group g : groups){
                        SolrDocumentList groupList = g.getResult();
                        SolrDocument doc = groupList.get(0);
                        String value = (String)doc.get(fieldName);
                        outputList.add(value);
                    }
                }
            }
        }

        return  outputList;
    }

    /**
     *
     * This is used to support the auto-complete feature for TimeSeries
     *
     * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
     * @param iamSolrClient CloudSolrServer connection for iam_mef collection
     * @param taniumHostInfoSolrClient CloudSolrServer connection for host_info_mef collection
     * @param taniumHetSolrClient CloudSolrServer connection for het_mef collection
     * @param taniumUetSolrClient CloudSolrServer connection for uet_mef collection
     * @param modelId Integer model Id
     * @param fieldType String field type; can be "source" or "destination"
     * @param incomingString String user Input for which we provide auto-complete
     * @param startTime String specifying the startTime
     * @param endTime String specifying the endTime
     * @param pageNo Integer page no.
     * @param pageSize Integer page size
     *
     * @return List of String records as suggestions
     */
    public Set<String> getAutoCompleteRecords(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                              CloudSolrServer taniumHostInfoSolrClient, CloudSolrServer taniumHetSolrClient,
                                              CloudSolrServer taniumUetSolrClient,
                                              int modelId, String fieldType, String incomingString,
                                              String startTime, String endTime,
                                              int pageNo, int pageSize, FeatureServiceConfiguration conf) {
        SolrQuery solrQuery = new SolrQuery();
        int startRecord = 10 * (pageNo - 1);
        solrQuery.setStart(startRecord);
        solrQuery.setRows(pageSize);
        LOGGER.debug("Getting Records from " + startRecord + " to " + (startRecord + pageSize));

        String fieldName = SearchUtils.getAutoFieldName(modelId, fieldType);
        if(fieldName == null || fieldName.isEmpty() ){
            LOGGER.error("Invalid Input: Field Name not found");
            return Sets.newHashSet();
        }

        int daysElapsed = Days.daysBetween(new DateTime(startTime), DateTime.now()).getDays();
        Set<String> outputSet = Sets.newHashSet();
        if(daysElapsed <= 15) {
            solrQuery.setQuery(fieldName + ":" + incomingString);
            solrQuery.set("group", true);
            solrQuery.set("group.field", fieldName);
            solrQuery.addSort("startTimeISO", SearchUtils.getSolrSortOrder("desc"));

            String filterQueryString = "startTimeISO:[" + startTime + " TO " + endTime + "]";
            solrQuery.setFilterQueries(filterQueryString);
            LOGGER.debug("Executing Solr Query: {}", solrQuery.toString());
            QueryResponse response = null;
            try {
                if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
                    response = webProxySolrClient.query(solrQuery);
                } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                    response = iamSolrClient.query(solrQuery);
                } else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
                    response = taniumHostInfoSolrClient.query(solrQuery);
                }
                LOGGER.debug("Response data for query: {} is {}", solrQuery.toString(), response != null ? response.toString() : "");
            } catch (Exception exp) {
                LOGGER.error("Failed to get auto complete records for model: " + modelId + " with => {} ", exp);
            }
            if (response != null) {

                GroupResponse groupResponse = response.getGroupResponse();
                List<GroupCommand> groupCommands = groupResponse.getValues();
                if (groupCommands != null) {
                    for (GroupCommand groupCommand : groupCommands) {
                        List<Group> groups = groupCommand.getValues();
                        for (Group g : groups) {
                            SolrDocumentList groupList = g.getResult();
                            SolrDocument doc = groupList.get(0);
                            String value = (String) doc.get(fieldName);
                            outputSet.add(value);
                        }
                    }
                }
            }
        } else {
            outputSet = new SearchImpalaDao(conf).getAutoCompleteRecords(modelId, fieldType, incomingString, startTime, endTime, startRecord, pageSize);
        }
        return  outputSet;
    }


    public List<Map<String, Object>> searchFromQuery(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                                     CloudSolrServer taniumHostInfoSolrClient, CloudSolrServer taniumHetSolrClient,
                                                     CloudSolrServer taniumUetSolrClient,
                                                     int modelId, String solrQueryString, Map<String, List<String>> queryParams, String startTime, String endTime,
                                                     int endRows, int pageNo, int summaryFacetLimit, String sortField, String sortOrder, boolean summarize, FeatureServiceCache cache){
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        SolrQuery solrQuery = new SolrQuery();
        int startRecord = endRows * (pageNo - 1);
        SearchUtils.populateSolrQueryParams(solrQuery, startTime, endTime, summaryFacetLimit, SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache), startRecord, endRows, sortField, sortOrder, summarize, cache);
        String q = getTimeSeriesSearchQueryString(solrQueryString, queryParams);
        solrQuery.setQuery(q);
        QueryResponse response = SearchUtils.executeSolrQuery(webProxySolrClient, iamSolrClient,
                taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, solrQuery, modelId);
        SearchUtils.populateSearchResult(response, results, modelId, summarize, cache);
        return results;
    }

    /**
     * Time series search query consists of:
     * 1. search on the Graphs that are selected by the user on the explorer home page
     * 2. AND searching on the queryParams that the user selects from the Search Summary
     *
     * @param solrQueryString query string formed from all the graphs/facets selected on the explorer page
     * @param queryParams queryParams coming from the search Summary
     *
     * @return String solrQuery String to be used for searching
     */
    private String getTimeSeriesSearchQueryString(String solrQueryString, Map<String, List<String>> queryParams) {
        //iterate over query Params
        if(queryParams != null && !queryParams.isEmpty()) {
            String query = "";
            for (Object facet : queryParams.keySet()) {
                StringBuffer facetBuffer = new StringBuffer();
                List<String> facetValues = queryParams.get(facet.toString());
                if (facetValues != null) {
                    for (String facetH : facetValues) {
                        if (!facetH.isEmpty()) {
                            facetH = ClientUtils.escapeQueryChars(facetH);
                            if (facetBuffer.length() == 0) {
                                facetBuffer.append("\"").append(facetH).append("\"");
                            } else {
                                facetBuffer.append(" OR " + "\"").append(facetH).append("\"");
                            }
                        }
                    }

                    if (!facetBuffer.toString().isEmpty()) {
                        String key = facet.toString() + ":" + "(" + facetBuffer.toString() + ")";
                        if (query.isEmpty()) {
                            query = key;
                        } else {
                            query = key + " AND " + query;
                        }
                    }
                }
            }

            if (!query.isEmpty()) {
                //"AND" query formed from queryParam to the original queryString
                return solrQueryString + " AND " + " (" + query + ")";
            }
        }
        return solrQueryString;
    }
}
