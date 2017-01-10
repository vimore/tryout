package com.securityx.modelfeature.dao.solr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.MiscUtils;
import com.securityx.modelfeature.utils.SearchUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by harish on 1/27/15.
 */
public class WebAnomalyProfileSolrDao {


    private static final Logger LOGGER = LoggerFactory.getLogger(WebAnomalyProfileSolrDao.class);
    /**
     * Returns the top N Destinations
     *
     * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
     * @param iamSolrClient      CloudSolrServer connection for iam_mef collection
     * @param startTime          String specifying the startTime
     * @param endTime            String specifying the endTime
     * @param queryParams     Map with field name as key and List<String> as value
     * @param facetLimit         Integer facet limit
     * @param startRows     Integer start Row no.
     * @param endRows       Integer end Row no.
     * @return top N DestinationNameOrIps for the input facetKeyValues
     */
    public List<Map<String, Object>> getTopNDestinations(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                                         int modelId,
                                                         Map<String, List<String>> queryParams, String sourceAddress,
                                                         String startTime, String endTime, int facetLimit, int startRows, int endRows, boolean summarize, FeatureServiceCache cache) {
        List<Map<String, Object>> facetList = Lists.newLinkedList();
        SolrQuery solrQuery = new SolrQuery();

        List<String> facetFields = Lists.newLinkedList();
        facetFields.add(SearchUtils.getDestinationIpFieldName());

        SearchUtils.populateSolrQueryParams(solrQuery, startTime, endTime, facetLimit, facetFields, startRows, facetLimit, null, null, summarize, cache);

        populateQuery(solrQuery, queryParams, sourceAddress);
        QueryResponse response = SearchUtils.executeSolrQuery(webProxySolrClient, iamSolrClient, solrQuery, modelId);

        long totalNumRecords = SearchUtils.getTotalNumberOfRecords(webProxySolrClient, iamSolrClient, modelId, startTime, endTime);
        populateFacetFieldsFromResponse(facetList, response, totalNumRecords);
        return facetList;
    }

    /**
     * Runs the Solr Search query
     *
     * @param webProxySolrClient       SolrServerClient for web_proxy_mef collection
     * @param iamSolrClient       SolrServerClient for iam_mef collection
     * @param modelId          Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param queryParams   Map with field name as key and List<String> as value
     * @param sourceAddress    String sourceAddress (ip)
     * @param startTime        String specifying start Time in the query
     * @param endTime          String specifying end Time in the query
     * @param endRows          Integer specifying number of rows in the result
     * @return List of Logs
     */
    public List<Map<String, Object>> getFacetedSearchResults(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient, int modelId,
                                                             Map<String, List<String>> queryParams, String sourceAddress,
                                                             String startTime, String endTime, int endRows, int pageNo, int summaryFacetLimit,String sortField, String sortOrder, boolean summarize, FeatureServiceCache cache) {
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        SolrQuery solrQuery = new SolrQuery();
        int startRecord = endRows * (pageNo - 1);

        SearchUtils.populateSolrQueryParams(solrQuery, startTime, endTime, summaryFacetLimit, SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache), startRecord, endRows, sortField, sortOrder, summarize, cache);

        populateSearchQuery(solrQuery, queryParams, modelId, sourceAddress);
        QueryResponse response = SearchUtils.executeSolrQuery(webProxySolrClient, iamSolrClient, solrQuery, modelId);

        SearchUtils.populateSearchResult(response, results, modelId, summarize, cache);
        return results;
    }


    /**
     * Forms a query string using keyValues and sourceIp
     *
     * @param queryParams Map of fieldNames to values
     * @param sourceAddress String sourceNameOrIp
     *
     * @return String QueryString for solr query.
     */
    private void populateSearchQuery(SolrQuery solrQuery, Map<String, List<String>> queryParams, int modelId, String sourceAddress) {
        String facetQuery="";
        if(queryParams != null && !queryParams.isEmpty()) {
            for (Object facet : queryParams.keySet()) {
                StringBuffer queryBuffer = new StringBuffer();
                List<String> queryValues = queryParams.get(facet);
                if(queryValues != null) {
                    for (String value : queryValues) {
                        if (value != null && !value.isEmpty()) {
                            String str = getQueryString(value);
                            if (queryBuffer.length() == 0) {
                                queryBuffer.append(str);
                            } else {
                                //for a given field, if multiple values are present, we use "OR" operator
                                queryBuffer.append(" OR ").append(str);
                            }
                        }
                    }
                }


                if (!queryBuffer.toString().isEmpty()) {
                    String field = SearchUtils.getInternalFieldName(facet.toString());
                    String key = field + ":" + "(" + queryBuffer.toString() + ")";
                    if (facetQuery.isEmpty()) {
                        facetQuery = key;
                    } else {
                        facetQuery = key + " AND " + facetQuery;
                    }
                }
            }
        }

        String q = "";
        if(sourceAddress != null && !sourceAddress.isEmpty()){

            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.WebAnomalyProfileTuple()._1().equals(modelId)) {
                q = SearchUtils.getSourceIpFieldName() + ":" + "\"" + sourceAddress + "\"";
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.AdAnomalyProfileTuple()._1().equals(modelId)) {
                q = "(( " + SearchUtils.getSourceUserNameFieldName() + ":" + "\"" + sourceAddress + "\"" + " ) " +
                        " OR " + "( " + SearchUtils.getDestinationUserNameFieldName() + ":" + "\"" + sourceAddress + "\"" + " ) )" ;
            }

        }

        if(!facetQuery.isEmpty()){
            q = q + " AND " + facetQuery;
        }

        LOGGER.debug("Solr Query String: " + q);
        solrQuery.setQuery(q);
    }


    /**
     * Forms a query string using keyValues and sourceIp
     *
     * @param queryParams Map of fieldNames to values
     * @param sourceAddress String sourceNameOrIp
     *
     * @return String QueryString for solr query.
     */
    @Deprecated
    private void populateQuery(SolrQuery solrQuery, Map<String, List<String>> queryParams, String sourceAddress) {
        String facetQuery="";
        if(queryParams != null && !queryParams.isEmpty()) {
            for (Object facet : queryParams.keySet()) {
                StringBuffer queryBuffer = new StringBuffer();
                List<String> queryValues = queryParams.get(facet);
                if(queryValues != null) {
                    for (String value : queryValues) {
                        if (value != null && !value.isEmpty()) {
                            String str = getQueryString(value);
                            if (queryBuffer.length() == 0) {
                                queryBuffer.append(str);
                            } else {
                                //for a given field, if multiple values are present, we use "OR" operator
                                queryBuffer.append(" OR ").append(str);
                            }
                        }
                    }
                }


                if (!queryBuffer.toString().isEmpty()) {
                    String field = SearchUtils.getInternalFieldName(facet.toString());
                    String key = field + ":" + "(" + queryBuffer.toString() + ")";
                    if (facetQuery.isEmpty()) {
                        facetQuery = key;
                    } else {
                        facetQuery = key + " AND " + facetQuery;
                    }
                }
            }
        }

        String q = "";
        if(sourceAddress != null && !sourceAddress.isEmpty()){
            q = SearchUtils.getSourceIpFieldName() + ":" + "\"" + sourceAddress + "\"";
        }

        if(!facetQuery.isEmpty()){
            q = q + " AND " + facetQuery;
        }

        LOGGER.debug("Solr Query String: " + q);
        solrQuery.setQuery(q);
    }


    /**
     *This method basically takes in the values for Query params and handles special cases (eg: tld = "No TLD"
     *
     * @param queryParamValue String value of a queryParam (coming from the input to the api)
     *
     * @return String value to be used in SolrQuery
     */
    private String getQueryString(String queryParamValue) {
        String value = "*";
        if(queryParamValue.trim().equalsIgnoreCase("No TLD") || queryParamValue.trim().equalsIgnoreCase("No SLD")){
            //There are some records for which TLD has value as "NO TLD".
            //For such cases, we need to search destinationNameOrIp for range 0.0.0.0 to 99.99.99.99.
            //We use 99.99.99.99 as upper limit (and not 255.255.255.255) because, destinationNameOrIp is stored as String and will follow lexicographical ordering.
            value = "[0.0.0.0 TO 99.99.99.99]";
        } else if(queryParamValue.contains(":")){
            //if we use wildcard here (*), Solr query will throw an error.
            // So we need to put this string in quotes and search.
            // For the rest of the cases, we should do a wildcard search.
            value =  "\"" + ClientUtils.escapeQueryChars(queryParamValue) + "\"";
        }else {
            // For the rest of the cases, we should do a wildcard search.
            //queryParamValue = queryParamValue.replace(" ", "*").replace("(","*").replace(")", "*");
            value = "\"" + ClientUtils.escapeQueryChars(queryParamValue) + "\"";
        }

        return value;
    }

    /**
     * Populates the count, percent of total number of records for all facetFields in the Solr  QueryResponse
     *
     * @param facetList List of Map of Facet field to the count, percent
     *
     * @param response
     */
    private void populateFacetFieldsFromResponse(List<Map<String, Object>> facetList, QueryResponse response, long total) {
        if (response != null) {

            List<FacetField> facetFieldsList = response.getFacetFields();

            //int count = 0;
            for (int i = 0; i < facetFieldsList.size(); i++) {

                FacetField facetField = facetFieldsList.get(i);
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count val : values) {
                    Map<String, Object> facetKeyValue = Maps.newLinkedHashMap();
                    double count = (double) val.getCount();
                    facetKeyValue.put("value", val.getName());
                    facetKeyValue.put("count", count);
                    facetKeyValue.put("percent", MiscUtils.getPercentage(count, total));
                    facetList.add(facetKeyValue);
                }
            }
        }
    }

}
