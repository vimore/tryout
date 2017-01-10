package com.securityx.modelfeature.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.utils.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Solr Facet queries
 */
public class FacetDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacetDao.class);

    /**
     *
     * Runs the Solr Facet query
     *
     * @param webProxySolrClient SolrServerClient for collection: web_proxy_mef
     * @param  iamSolrClient SolrServerClient for collection: iam_mef
     * @param modelId Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param securityEventId Integer specifying securityEventId/Feature Id
     * @param selectedEntities String This is a list of entities (ip/usernames)
     * @param startTime String specifying start Time in the query
     * @param endTime String specifying end Time in the query
     * @param facetMinCount Integer Min count in facet query result
     * @param facetLimit Integer limit in the results returned by the face query
     * @param facetStartRows Integer
     * @param facetEndRows Integer
     * @param cache FeatureServiceCache
     *
     * @return Map of facet field name String to list of Counts for entities
     */
    public Map<String, List<Map<String, Long>>> getFacetResults(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                                                int modelId, int securityEventId,
                                                                String selectedEntities, String startTime, String endTime,
                                                                int facetMinCount, int facetLimit, int facetStartRows, int facetEndRows,
                                                                FeatureServiceCache cache) {
        PeerGroupFacetHelper facetInfo = cache.getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        SolrQuery.ORDER sortOrder = SearchUtils.getSolrSortOrder(facetInfo.sortOrder());
        Map<String, List<Map<String, Long>>> resultMap = Maps.newHashMap();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(facetLimit);
        solrQuery.setFacetMinCount(facetMinCount);
        solrQuery.setStart(facetStartRows);
        solrQuery.setRows(facetEndRows);

        String entitiesLabel = facetInfo.entityLabel();

        addFacetFeilds(solrQuery, facetInfo);

        solrQuery.setFacetSort(FacetParams.FACET_SORT_COUNT);
        solrQuery.setShowDebugInfo(true);
        if(selectedEntities == null || selectedEntities.isEmpty()) {
            LOGGER.error("selectedEntities is null or empty!");
            return resultMap;
        }
        //query is of the form key:"value"
        String parsedEntities = selectedEntities.substring(1, selectedEntities.length() - 1);
        String[] entities = parsedEntities.split(",");
        StringBuilder entityValues = new StringBuilder();
        for (String entity : entities) {
            entity = entity.trim();
            if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                //Ad Peer group entities format: "NtDomain-username"
                //So we have to strip of the "NtDomain" and only take the username
                entity = entity.substring(entity.indexOf("-") + 1);
            }
            //ToDo: remove this hack and fix the solr indexing
            if ((entity.contains(":") || entity.contains(","))) {
                int index;
                if (entity.contains(":")) {
                    index = entity.indexOf(":");
                } else {
                    index = entity.indexOf(",");
                }
                entity = "*" + entity.substring(index + 1);
            }

            entity = entity.replace("\"", "");
            if (entityValues.length() == 0) {
                entityValues.append(entity).append("*");
            } else {
                entityValues.append(" OR ").append(entity).append("*");
            }
        }

        String entityQuery = entitiesLabel + ":(" +  entityValues.toString() + ")";

        String queryField = null;
        if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
            AdFacetHelper ad = (AdFacetHelper) facetInfo;
            queryField = "cefSignatureId:*" + ad.cefSignature() + "*";
        } else if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
            WebFacetHelper web = (WebFacetHelper) facetInfo;
            queryField = web.query();
        } else if (Constants.AdNoveltyDetectorTuple()._1().equals(modelId)) {
            AdNoveltyDectectorFacetHelper ad = (AdNoveltyDectectorFacetHelper) facetInfo;
            queryField = "cefSignatureId:*" + ad.cefSignature() + "*";
        }


        String queryString = queryField != null ? queryField + " AND " + "(" + entityQuery + ")" : "" + "(" + entityQuery + ")";
        LOGGER.debug("Solr Facet Query String: " + queryString);
        solrQuery.setQuery(queryString);
        solrQuery.addSort("startTimeISO", sortOrder);

        String filterQueryString = "startTimeISO:[" + startTime + " TO " + endTime + "]";
        solrQuery.setFilterQueries(filterQueryString);

        QueryResponse response = null;
        try {
            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = webProxySolrClient.query(solrQuery);
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = iamSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to get facet results => {} ", exp);
        }

        populateFacetFieldsFromResponse(resultMap, response, sortOrder);
        populateFacetPivotsFromResponse(resultMap, response, sortOrder);
        return resultMap;
    }


    private void populateFacetFieldsFromResponse(Map<String, List<Map<String, Long>>> facetFieldsMap, QueryResponse response, SolrQuery.ORDER sortOrder) {
        if (response != null) {

            List<FacetField> facetFieldsList = response.getFacetFields();

            for (FacetField facetField : facetFieldsList) {
                List<Map<String, Long>> facetList = Lists.newLinkedList();
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count val : values) {
                    Map<String, Long> facetKeyValues = Maps.newLinkedHashMap();
                    facetKeyValues.put(val.getName(), val.getCount());
                    //Solr Facets, if sorted, are always sorted in Descending order.
                    //So for security events which require ascending order, we have to do this.
                    if (sortOrder.equals(SolrQuery.ORDER.asc)) {
                        facetList.add(0, facetKeyValues);
                    } else {
                        facetList.add(facetKeyValues);
                    }
                }

                if (!facetList.isEmpty()) {
                    facetFieldsMap.put(facetField.getName(), facetList);
                }

            }

        }
    }

    private void populateFacetPivotsFromResponse(Map<String, List<Map<String, Long>>> facetFieldsMap, QueryResponse response, SolrQuery.ORDER sortOrder) {
        if (response != null) {

            NamedList<List<PivotField>> facetPivotsList = response.getFacetPivot();

            if(facetPivotsList != null) {
                String facetName = facetPivotsList.getName(0);
                List<PivotField> pivots = facetPivotsList.getVal(0);
                List<Map<String, Long>> facetList = Lists.newLinkedList();
                for (int i = 0; i < pivots.size(); i++) {
                    Map<String, Object> map = Maps.newHashMap();
                    PivotField pivotField = pivots.get(i);
                    String val = (String) pivotField.getValue();
                    long count = pivotField.getCount();
                    Map<String, Long> facetKeyValues = Maps.newLinkedHashMap();
                    facetKeyValues.put(val, count);
                    //Solr Facets, if sorted, are always sorted in Descending order.
                    //So for security events which require ascending order, we have to do this.
                    if (sortOrder.equals(SolrQuery.ORDER.asc)) {
                        facetList.add(0, facetKeyValues);
                    } else {
                        facetList.add(facetKeyValues);
                    }
                }
                if(!facetList.isEmpty()) {
                    facetFieldsMap.put(facetName, facetList);
                }
            }

        }
    }

    /**
     * Adding Facets related information in the SolrQuery.
     * The facetting is different for Ad Peer Group compared to Web Peer Group.
     * This method provides an abstraction to add the facetting
     *
     * @param solrQuery SolrQuery
     * @param facetInfo PeerGroupFacetHelper
     */
    private void addFacetFeilds(SolrQuery solrQuery, PeerGroupFacetHelper facetInfo) {
        if(facetInfo.sumfields() != null){
            solrQuery.setGetFieldStatistics(true);
            for(String facet: facetInfo.facetFields()){
                solrQuery.addFacetPivotField(facet);
                for(String sumField : facetInfo.sumfields()){
                    solrQuery.setGetFieldStatistics(sumField);
                    solrQuery.addStatsFieldFacets(sumField, facet);
                }
            }

        }else{
            for(String facet: facetInfo.facetFields()){
                solrQuery.addFacetField(facet);
            }
        }
    }


    /**
     * This method gives Daily aggregates of the count for given (urls, domains, ips)
     *
     * @param solrClient
     * @param endTime
     * @param urls
     * @param domains
     * @param ips
     * @param sourceAddress
     * @return
     */
    public  NamedList<Map<String, Integer>> getEventCounts(CloudSolrServer solrClient, String endTime, String urls,
                                                           String domains, String ips, String sourceAddress, int lastNDays){
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setShowDebugInfo(true);
        solrQuery.setStart(0);
        solrQuery.setRows(0);
        solrQuery.setFacet(true);

        LOGGER.debug("Incoming ips: " + ips);
        String ipsQueryString = getOrSeparatedString(ips);
        if (ipsQueryString.isEmpty()) {
            ipsQueryString = "\" \"";
        }
        LOGGER.debug("Incoming domains: " + domains);
        String domainsQueryString = getOrSeparatedString(domains);
        if (domainsQueryString.isEmpty())
            domainsQueryString = "\" \"";
        String queryString = "destinationNameOrIp:(" + ipsQueryString + " OR " + domainsQueryString + ")";
        //get url query String:
        String urlString = "";
        if(urls != null && !urls.isEmpty()){
            urlString = getSolrQueryStringForUrl(urls);
        }
        if(!urlString.isEmpty()) {
            queryString = queryString + " OR destinationUrl:" + urlString;
        }

        String sourceAddressQuery = "";
        if(sourceAddress != null && !sourceAddress.isEmpty()){
            sourceAddressQuery = "sourceNameOrIp:" + sourceAddress;
        }
        if(!sourceAddressQuery.isEmpty()){
            queryString = queryString + " AND " + sourceAddressQuery;
        }

        solrQuery.setQuery(queryString);
        LOGGER.debug("Solr query string: " + queryString);
        DateTime end= new DateTime(endTime, DateTimeZone.UTC);
        DateTime start= new DateTime(end.minusDays(lastNDays), DateTimeZone.UTC);
        solrQuery.addDateRangeFacet("startTimeISO", start.toDate(), end.toDate(), "+1DAY");
        solrQuery.setQuery(queryString);

        QueryResponse response = null;
        try {
            response = solrClient.query(solrQuery);
        } catch (SolrServerException exp) {
            LOGGER.error("Failed to get facet results => {} ", exp);
        }

        SimpleOrderedMap<Object> facetCounts  = (SimpleOrderedMap<Object>) response.getResponse().get("facet_counts");
        SimpleOrderedMap<Object> facetRanges = (SimpleOrderedMap<Object>) facetCounts.get("facet_ranges");
        SimpleOrderedMap<Object> startTimeMap = (SimpleOrderedMap<Object>)facetRanges.get("startTimeISO");
        return (NamedList<Map<String, Integer>>) startTimeMap.get("counts");

    }

    private SolrQuery.ORDER getSolrSortOrder(String sortOrder){
        if(sortOrder.equalsIgnoreCase("asc")){
            return SolrQuery.ORDER.asc;
        }
        return SolrQuery.ORDER.desc;
    }

    /**
     * Converts a comma-separated string into OR-separated string for using it in the solr query
     * eg: for input string "value1,value2,value3"
     * output: ""value1" OR "value2" OR "value3""
     *
     * @param values String comma-separated
     *
     * @return comma-separated string converted into OR-separated string for using it in the solr query
     */
    private String getOrSeparatedString(String values){
        StringBuffer buf = new StringBuffer();
        if(values != null) {
            String[] arr = values.split(",");
            for (String value : arr) {
                if (buf.length() == 0) {
                    buf.append("\"").append(value).append("\"");
                } else {
                    buf.append(" OR " + "\"").append(value).append("\"");
                }
            }
        }
        return buf.toString();
    }


    /**
     *
     * Converts a comma-separated String of urls into Solr Query String.
     * The method parses every url and gets the Hostname and requestPath.
     * eg: For urls = "http://www.yahoo.com/asdfsg, https://www.bit.ly.com/kjadhf",
     * output will be "(destinationNameOrIp:"www.yahoo.com" AND requestPath:*asdfsg*) AND (destinationNameOrIp:"www.bit.ly.com" AND requestPath:*kjadhf*)"
     * @param urls
     * @return
     */
    private String getSolrQueryStringForUrl(String urls) {

        StringBuffer buf = new StringBuffer();
        if (urls != null) {
            //Splitting on "," because that is expected for field "destinationUrl" in web_proxy_mef.
            //splitting on * because while creating solr query we will be wildcarding using "*".
            String[] arr = urls.split(",|\\*");
            for (String urlString : arr) {
                StringBuffer urlBuf = new StringBuffer();
                try {
                    URL url = new URL(urlString);
                    String host = url.getHost();

                    urlBuf.append("(" + "*").append(host).append("*").append(" AND ");
                    String path = url.getPath();

                    urlBuf.append("*").append(path).append("*").append(" AND ");
                    String requestScheme = url.getProtocol();

                    if(host != null && path != null && requestScheme != null) {
                        if (host == null || host.isEmpty()) {
                            host = " ";
                        }
                        if (path == null || path.isEmpty()) {
                            path = " ";
                        }
                        if (requestScheme == null || requestScheme.isEmpty()) {
                            requestScheme = " ";
                        }
                        urlBuf.append("*").append(requestScheme).append("*").append(")");


                        //append to result string
                        if (buf.length() == 0) {
                            buf.append(urlBuf.toString());
                        } else {
                            buf.append(" OR ").append(urlBuf.toString());
                        }
                    }
                } catch (MalformedURLException e) {
                    LOGGER.error("Url Malformed: " + urlString + " : " + e);
                }
            }
        }
        return buf.toString();
    }
}
