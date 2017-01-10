package com.securityx.modelfeature.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.FeatureServiceCache;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(SearchUtils.class);

    public static final int FACET_MIN_COUNT = 1;


    public static long getTotalNumberOfRecords(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient, int modelId, String startTime, String endTime){

        long totalNumRecords = 0;
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        //We only care about the total number of records. We don't need the result rows. so better to filter them out.
        solrQuery.setStart(0);
        solrQuery.setRows(0);

        populateFilterQueryTimeParam(solrQuery, startTime, endTime);

        QueryResponse response = executeSolrQuery(webProxySolrClient, iamSolrClient, solrQuery, modelId);

        if(response != null){
            SolrDocumentList results = response.getResults();
            if(results != null){
                totalNumRecords = results.getNumFound();
            }
        }

        return totalNumRecords;
    }

    /**
     * Executes the SolrQuery on a collection.
     * The method uses, model Id to find out which collection to use.
     *
     * @param webProxySolrClient CloudSolrServer for web_proxy_mef
     * @param iamSolrClient CloudSolrServer for iam_mef
     * @param solrQuery SolrQuery
     * @param modelId Integer specifying modelId which is used to determine the collection to be used.
     *
     * @return QueryResponse
     */
    @Deprecated
    public static QueryResponse executeSolrQuery(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient, SolrQuery solrQuery, int modelId){
        QueryResponse response = null;
        LOGGER.debug("Executing Solr Query: {}", solrQuery.toString());
        try {
            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) ||
                    Constants.WebAnomalyProfileTuple()._1().equals(modelId)) {
                response = webProxySolrClient.query(solrQuery);
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) ||
                    Constants.AdAnomalyProfileTuple()._1().equals(modelId) ||
                    Constants.AdNoveltyDetectorTuple()._1().equals(modelId)) {
                response = iamSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to execute SolrQuery => ", exp);
        }
        LOGGER.debug("Response data for query: {} is {}", solrQuery.toString(), response != null ? response.toString() : "");
        return response;
    }

    /**
     * Executes the SolrQuery on a collection.
     * The method uses, model Id to find out which collection to use.
     *
     * @param webProxySolrClient CloudSolrServer for web_proxy_mef
     * @param iamSolrClient CloudSolrServer for iam_mef
     * @param taniumHostInfoSolrClient  CloudSolrServer for host_info_mef
     * @param taniumHetSolrClient  CloudSolrServer for het_mef
     * @param taniumUetSolrClient CloudSolrServer for uet_mef
     * @param solrQuery SolrQuery
     * @param modelId Integer specifying modelId which is used to determine the collection to be used.
     *
     * @return QueryResponse
     */
    public static QueryResponse executeSolrQuery(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                                 CloudSolrServer taniumHostInfoSolrClient, CloudSolrServer taniumHetSolrClient,
                                                 CloudSolrServer taniumUetSolrClient,
                                                 SolrQuery solrQuery, int modelId){
        LOGGER.debug("Executing Solr Query: {} for modelID {}", solrQuery.toString(), modelId);
        QueryResponse response = null;
        try {
            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.WebAnomalyProfileTuple()._1().equals(modelId)) {
                response = webProxySolrClient.query(solrQuery);
            } else if  (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) ||
                    Constants.AdAnomalyProfileTuple()._1().equals(modelId) ||
                    Constants.AdNoveltyDetectorTuple()._1().equals(modelId)) {
                response = iamSolrClient.query(solrQuery);
            } else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
                response = taniumHostInfoSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to execute SolrQuery => ", exp);
        }
        LOGGER.debug("Response data for query: {} is {}", solrQuery.toString(), response != null ? response.toString() : "");
        return response;
    }

    /**
     * Populating the result of the Search Query.
     * This will be a generic method and should be able to support all the searches we have in our apis
     *
     * @param response QueryResponse
     * @param results List of results
     * @param cache FeatureServiceCache
     */
    public static void populateSearchResult(QueryResponse response, List<Map<String, Object>> results, int modelId, boolean summarize, FeatureServiceCache cache) {
        if (response != null) {
            SolrDocumentList solrResult = response.getResults();
            long total = solrResult.getNumFound();
            Map<String, Object> map = new HashMap<>();
            map.put("result", solrResult);
            map.put("total", total);
            if(summarize) {
                map.put("summary", getSearchSummary(response, total, modelId, cache));
            }
            results.add(map);
        }
    }

    /**
     * Iterates over the facetFields in the QueryResponse and populates the "summary" info in the api response.
     *
     * @param response QueryResponse
     * @param cache FeatureServiceCache
     * @return
     */
    private static List<Object> getSearchSummary(QueryResponse response, long totalRecords, int modelId, FeatureServiceCache cache) {
       List<Object> result = Lists.newLinkedList();


        List<FacetField> facetFieldsList = response.getFacetFields();

        if(facetFieldsList != null && !facetFieldsList.isEmpty()) {

            for (FacetField aFacetFieldsList : facetFieldsList) {

                Map<String, List<Map<String, Object>>> map = Maps.newHashMap();

                FacetField facetField = aFacetFieldsList;
                String facetFieldName = facetField.getName();
                String localizedName = getLocalizedFacetName(facetFieldName, modelId, cache);
                List<Map<String, Object>> facetList = Lists.newLinkedList();
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count val : values) {
                    Map<String, Object> facetKeyValue = Maps.newLinkedHashMap();
                    double count = (double) val.getCount();
                    facetKeyValue.put("value", getFormattedFacetValue(val.getName()));
                    facetKeyValue.put("count", count);
                    facetKeyValue.put("percent", MiscUtils.getPercentage(count, totalRecords));
                    facetKeyValue.put("fieldName", facetFieldName);
                    facetList.add(facetKeyValue);
                }

                if (!facetList.isEmpty()) {
                    map.put(localizedName, facetList);
                    result.add(map);
                }
            }
        }
        return result;
    }

    /**
     * Looks at the Search Configuration yml and returns the localized name for the facetField.
     *
     * @param facetFieldName String fieldName
     * @param modelId Integer specifying modelId which is used to determine the collection to be used.
     * @param cache FeatureServiceCache to get SearchConfiguration
     *
     * @return
     */
    public static String getLocalizedFacetName(String facetFieldName, int modelId, FeatureServiceCache cache) {
        if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.WebAnomalyProfileTuple()._1().equals(modelId)){
           return cache.getWebSummaryFieldMap().get(facetFieldName);
        }else if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) ||
                Constants.AdAnomalyProfileTuple()._1().equals(modelId) ||
                Constants.AdNoveltyDetectorTuple()._1().equals(modelId)){
            return cache.getAdSummaryFieldMap().get(facetFieldName);
        } else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
            return cache.getTaniumHostInfoMefSummaryFieldMap().get(facetFieldName);
        }
        return "";
    }


    /**
     *  Some of the field values has url encoding. This method basically decodes those string.
     *  There may be other special characters coming into the string. All the formatting can be done here.
     * @param str
     * @return
     */
    private static String getFormattedFacetValue(String str) {
        String result = str;
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            LOGGER.error("Error while formatting string =>" + str + " => {}" + e);
        }
        return result;
    }

    /**
     * Sets the parameters of a SolrQuery
     *
     * @param solrQuery SolrQuery
     * @param startTime String specifying startTime
     * @param endTime String specifying endTime
     * @param facetLimit Integer facetLimit
     * @param facetFields List<String> specifying facet Fields
     * @param startRows Integer specifying start row number
     * @param endRows Integer specifying number of rows
     */
    public static void populateSolrQueryParams(SolrQuery solrQuery, String startTime, String endTime, int facetLimit, List<String> facetFields, int startRows, int endRows, String sortField, String sortOrder, boolean isFacetQuery, FeatureServiceCache cache ) {
        populateFacetParamaters(solrQuery, facetLimit,facetFields, isFacetQuery);
        populateRows(solrQuery, startRows, endRows);
        populateFilterQueryTimeParam(solrQuery, startTime, endTime);
        populateSortParams(solrQuery, sortField, sortOrder, cache);
    }

    /**
     * Setting sorting for solr query
     * @param solrQuery SolrQuery
     * @param sortField String specifying the field to which sorting is to be done
     * @param sortOrder String "asc" or "desc"
     * @param cache FeatureServiceCache to get SearchConfiguration
     */
    private static void populateSortParams(SolrQuery solrQuery, String sortField, String sortOrder, FeatureServiceCache cache) {
        if(sortOrder == null) {
            sortOrder = cache.getSearchConfiguration().getSearchDefault().getSortOrder();
        }

        if(sortField == null){
            sortField = cache.getSearchConfiguration().getSearchDefault().getSortField();
        }
        solrQuery.addSort(sortField, getSolrSortOrder(sortOrder));
    }

    /**
     * Sets faceting parameters and facet fields for a SolrQuery
     *
     * @param solrQuery SolrQuery
     * @param facetLimit Integer facet limit
     * @param facetFields List<String> specifying facet Fields
     */
    public static void populateFacetParamaters(SolrQuery solrQuery, int facetLimit, List<String> facetFields, boolean isFacet) {
        if(isFacet) {
            solrQuery.setFacet(isFacet);
            solrQuery.setFacetLimit(facetLimit);
            solrQuery.setFacetMinCount(FACET_MIN_COUNT);
            solrQuery.setFacetSort(FacetParams.FACET_SORT_COUNT);
            if (facetFields != null) {
                for (String facetField : facetFields) {
                    solrQuery.addFacetField(facetField);
                }
            }
        }
    }

    /**
     * Sets the time range over which the SolrQuery is executed
     * Sorting order is set Desc by default.
     *
     * @param solrQuery SolrQuery
     * @param startTime String specifying startTime
     * @param endTime String specifying endTime
     */
    public static void populateFilterQueryTimeParam(SolrQuery solrQuery, String startTime, String endTime) {
        String startTimeField = getStartTimeISOFieldName();
        String filterQueryString = startTimeField + ":[" + startTime + " TO " + endTime + "]";
        solrQuery.setFilterQueries(filterQueryString);
    }

    /**
     * Sets the start row number and the total number of rows in the result of SolrQuery
     * @param solrQuery SolrQuery
     * @param startRows Integer specifying start row number
     * @param endRows Integer specifying number of rows in the result
     */
    public static void populateRows(SolrQuery solrQuery, int startRows, int endRows) {
        solrQuery.setStart(startRows);
        solrQuery.setRows(endRows);
    }


    /**
     * This method would return only those facets "fields" which are shown in the HttpTime Series.
     * To add/change "fields", simply change this method to fetch the updated fields, either from a new configuration or existing.
     *
     * @param modelId Integer specifying modelId which is used to determine the fields to be used for summary
     * @param cache FeatureServiceCache to get HttpTimeSeries "typeFields"
     *
     * @return list of fields to be shown in the summary section of Search
     *
     */
    public static List<String> getFacetFieldMapForSearchSummary(int modelId, FeatureServiceCache cache){
        if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) || Constants.WebAnomalyProfileTuple()._1().equals(modelId)) {
            return Lists.newLinkedList(cache.getWebSummaryFieldMap().keySet());
        }else if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) ||
                Constants.AdAnomalyProfileTuple()._1().equals(modelId) ||
                Constants.AdNoveltyDetectorTuple()._1().equals(modelId) ) {
            return Lists.newLinkedList(cache.getAdSummaryFieldMap().keySet());
        } else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
            return Lists.newLinkedList(cache.getTaniumHostInfoMefSummaryFieldMap().keySet());
        }
        return null;
    }

    public static String getCollectionName(int modelId) {

        String collectionName = null;
        switch (modelId) {
            case 2:
                collectionName = Constants.WEB_PROXY_MEF_COLLECTION();
                break;
            case 3:
                collectionName = Constants.IAM_MEF_COLLECTION();
                break;
            default:
                collectionName = Constants.WEB_PROXY_MEF_COLLECTION();
                break;
        }
        return  collectionName;
    }

    /**
     * Returs a SolrQuery.ORDER depending on whether the input string is "asc" or
     * @param sortOrder String "asc" or "desc"
     *
     * @return
     */
    public static SolrQuery.ORDER getSolrSortOrder(String sortOrder){
        if(sortOrder.equalsIgnoreCase("asc")){
            return SolrQuery.ORDER.asc;
        }
        return SolrQuery.ORDER.desc;
    }

    /**
     * Returns the filed name based on whether the field type is source or destination, for a given model.
     * @param modelId java.lang.Integer modelId
     * @param fieldType String filed type; either "source" or "destination"
     *
     * @return String a valid fieldName if filedType is valid, else null
     */
    public static String getAutoFieldName(int modelId, String fieldType){
        String fieldName = null;
        fieldType = fieldType.toLowerCase();
        //get fieldname from fieldType
        if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)||
                Constants.AdAnomalyProfileTuple()._1().equals(modelId)||
                Constants.AdNoveltyDetectorTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "sourceUserNameAuto";
            }else{
                fieldName = "destinationUserNameAuto";
            }
        }else if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "sourceNameOrIpAuto";
            }else{
                fieldName = "destinationNameOrIpAuto";
            }
        } else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
            if(fieldType.contains("source")){
                fieldName = "deviceHostNameAuto";
            }else if(fieldType.toLowerCase().contains("md5")){
                fieldName = "processFileMd5Auto";
            }else if(fieldType.toLowerCase().contains("port")){
                fieldName = "processListenPortAuto";
            }
        }
        return fieldName;
    }

    /*public static String getAutoFieldNameImpala(int modelId, String fieldType){
        String fieldName = null;
        fieldType = fieldType.toLowerCase();
        //get fieldname from fieldType
        if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)||
                Constants.AdAnomalyProfileTuple()._1().equals(modelId)||
                Constants.AdNoveltyDetectorTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "sourceUserName";
            }else{
                fieldName = "destinationUserName";
            }
        }else if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "sourceNameOrIp";
            }else{
                fieldName = "destinationNameOrIp";
            }
        }
//            else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
//            if(fieldType.contains("source")){
//                fieldName = "deviceHostNameAuto";
//            }else if(fieldType.toLowerCase().contains("md5")){
//                fieldName = "processFileMd5Auto";
//            }else if(fieldType.toLowerCase().contains("port")){
//                fieldName = "processListenPortAuto";
//            }
//        }
//        return fieldName;
    }*/

    /**
     * Returns the filed name based on whether the field type is source or destination, for a given model.
     * @param modelId java.lang.Integer modelId
     * @param fieldType String filed type; either "source" or "destination"
     *
     * @return String a valid fieldName if filedType is valid, else null
     */
    public static String getSrcDestFieldName(int modelId, String fieldType){
        String fieldName = null;
        fieldType = fieldType.toLowerCase();
        //get fieldname from fieldType
        if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId) ||
                Constants.AdAnomalyProfileTuple()._1().equals(modelId) ||
                Constants.AdNoveltyDetectorTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "sourceUserName";
            }else{
                fieldName = "destinationUserName";
            }
        }else  if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId) ||
                Constants.WebAnomalyProfileTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "sourceNameOrIp";
            }else{
                fieldName = "destinationNameOrIp";
            }
        } else  if(Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)){
            if(fieldType.contains("source")){
                fieldName = "deviceNameOrIp";
            }
        }
        return fieldName;
    }


    /**
     *
     * @param str
     * @return
     */
    public static String getInternalFieldName(String str){
        if(str.equalsIgnoreCase("sld") || str.equals("tld")){
            // This Method can be used to handle all cases
            return "destinationNameOrIp";
        }
        return str;
    }

    /**
     *
     * @return String fieldname to be used as sourceNameOrIp
     */
    public static String getSourceIpFieldName(){
        return "sourceNameOrIp";
    }

    /**
     *
     * @return String fieldname to be used as destinationIp
     */
    public static String getDestinationIpFieldName(){
        return "destinationNameOrIp";
    }

    /**
     *
     * @return String fieldname to be used as sourceUserName
     */
    public static String getSourceUserNameFieldName(){
        return "sourceUserName";
    }

    /**
     *
     * @return String fieldname to be used as destinationUserName
     */
    public static String getDestinationUserNameFieldName(){
        return "destinationUserName";
    }

    /**
     *
     * @return String fieldname to be used for "time"
     */
    public static String getStartTimeISOFieldName(){
        return "startTimeISO";
    }

    public static String getCommaSeparatedString(String[] selectedEntities, int modelId) {

        if(selectedEntities != null && selectedEntities.length > 0) {
            StringBuilder strBuilder = new StringBuilder();

            for (String entity : selectedEntities) {
                if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                    //Ad Peer group entities format: "NtDomain-username"
                    //So we have to strip of the "NtDomain" and only take the username
                    entity = entity.substring(entity.indexOf("-") + 1);
                }
                strBuilder.append("'").append(entity.replace("'", "\\'")).append("',");
            }

            strBuilder.deleteCharAt(strBuilder.length() - 1);

            return strBuilder.toString();
        } else {
            return "";
        }
    }
}
