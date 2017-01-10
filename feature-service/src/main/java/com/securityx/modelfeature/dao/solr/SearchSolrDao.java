package com.securityx.modelfeature.dao.solr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.FacetDao;
import com.securityx.modelfeature.dao.PhoenixUtils;
import com.securityx.modelfeature.dao.SearchDao;
import com.securityx.modelfeature.utils.*;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Solr Search queries
 */
public class SearchSolrDao extends SearchDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchSolrDao.class);
    public FacetDao facetDao = new FacetDao();
    private CloudSolrServer webProxySolrClient = null;
    private CloudSolrServer iamSolrClient = null;

    public SearchSolrDao() {

    }
    /**
     *
     * @param webProxySolrClient SolrServerClient for web_proxy_mef
     * @param iamSolrClient SolrServerClient for iam_mef
     */
    public SearchSolrDao(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient) {
        this.webProxySolrClient = webProxySolrClient;
        this.iamSolrClient = iamSolrClient;
    }

    /**
     * Runs the Solr Search query for Source and Destination
     *
     * @param modelId          Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param sources  List of source user names or Ips
     * @param destinations   List of destination user names or ips
     * @param startTime        String specifying start Time in the query
     * @param endTime          String specifying end Time in the query
     * @param endRows          Integer specifying number of rows in the result
     * @param cache            FeatureServiceCache
     * @return
     */
    public List<Map<String, Object>> getSearchResultsForSourceDestination(int modelId, List<String> sources, List<String> destinations,
                                                                          Map<String, List<String>> queryParams,
                                                                          String startTime, String endTime, int summaryFacetLimit, int endRows, int pageNo,
                                                                         String sortField, String sortOrder, boolean summarize,
                                                                         FeatureServiceCache cache) {
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        SolrQuery solrQuery = new SolrQuery();
        int startRecord = endRows * (pageNo - 1);
        SearchUtils.populateSolrQueryParams(solrQuery, startTime, endTime, summaryFacetLimit, SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache), startRecord, endRows, sortField, sortOrder, summarize, cache);
        String solrQueryString = "";

        String sourceQuery = "";
        if(sources != null){
            sourceQuery = getSourceDestinationQuery(sources, SearchUtils.getSourceIpFieldName());
        }

        if(destinations != null) {
            String destQuery = getSourceDestinationQuery(destinations, SearchUtils.getDestinationIpFieldName());
            if (!sourceQuery.isEmpty()) {
                destQuery = sourceQuery + " AND " + destQuery;
            }
            solrQueryString = destQuery;
        }

        if(solrQueryString != null && !solrQueryString.isEmpty()){
            solrQueryString = "(" + solrQueryString + ")";
        }

        String facetQuery = getQueryStringFromFacetQueryParams(queryParams);
        //now add the form the facet query depending on whether solrQueryString exits or not.
        String q = "";
        if(facetQuery != null && !facetQuery.isEmpty()){
            String str = "(" + facetQuery + ")";
            q = solrQueryString.isEmpty()? str : " AND " + str;
        }

        //merging the two queries
        q = solrQueryString + q;

        LOGGER.debug("Solr Query used for getting Search Results For Source-Destination: " + q);
        if(!q.isEmpty()) {
            solrQuery.setQuery(q);
            QueryResponse response = SearchUtils.executeSolrQuery(webProxySolrClient, iamSolrClient, solrQuery, modelId);
            SearchUtils.populateSearchResult(response, results, modelId, summarize, cache);
        }
        return results;
    }


    /**
     * iterates over the list of source(or destination) ips and returns a solr query string
     * @param sources
     * @return
     */
    private String getSourceDestinationQuery(List<String> sources, String fieldName){
        String query = "";
        if (sources != null && !sources.isEmpty()) {

            StringBuffer sourceQuery = new StringBuffer();
            for (String source : sources) {
                source = source.replace("\"", "");
                String q = "*" + source + "*";
                if(sourceQuery.length() == 0){
                    sourceQuery.append(q);
                }else{
                    sourceQuery.append(" OR ").append(q);
                }
            }
            if(!sourceQuery.toString().isEmpty()){
                query = fieldName + ":" + "(" + sourceQuery.toString() + ")";
            }
        }else{
            query = "( " + fieldName + ":* )";
        }
        return query;
    }

    /**
     * Runs the Solr Search query
     *
     * @param modelId          Integer specifying the Model Id (eg: model ID: 3 for AD)
     * @param securityEventId  Integer specifying securityEventId/Feature Id
     * @param queryParams      JSONObject format {facetFieldName: comma separated values}
     * @param selectedEntities String[] This is a list of entities (ip/usernames)
     * @param keywords         String[] This is a list of keywords that contain values in targerId
     * @param startTime        String specifying start Time in the query
     * @param endTime          String specifying end Time in the query
     * @param endRows          Integer specifying number of rows in the result
     * @param cache            FeatureServiceCache
     * @return Map of facet field name String to list of Counts for entities
     */
    public List<Map<String, Object>> getFacetedSearchResults(int modelId, int securityEventId,
                                                      Map<String, List<String>> queryParams, String[] selectedEntities, String[] keywords,
                                                      String startTime, String endTime, int summaryFacetLimit, int endRows, int pageNo,
                                                      String sortField, String sortOrder, boolean summarize,
                                                      FeatureServiceCache cache) {
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        PeerGroupFacetHelper facetInfo = cache.getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        SolrQuery solrQuery = new SolrQuery();
        int startRecord = endRows * (pageNo - 1);
        SearchUtils.populateSolrQueryParams(solrQuery, startTime, endTime, summaryFacetLimit, SearchUtils.getFacetFieldMapForSearchSummary(modelId, cache), startRecord, endRows, sortField, sortOrder, summarize, cache );

        String query = populateSolrQueryStringForPeerGroup(modelId, securityEventId, queryParams, selectedEntities, keywords, facetInfo);
        if(query == null){
            return results;
        }
        solrQuery.setQuery(query);

        QueryResponse response = SearchUtils.executeSolrQuery(webProxySolrClient, iamSolrClient, solrQuery, modelId);
        SearchUtils.populateSearchResult(response, results, modelId, summarize, cache);
        return results;
    }

    public static String populateSolrQueryStringForPeerGroup(int modelId, int securityEventId,
                                                             Map<String, List<String>> queryParams,
                                                             String[] selectedEntities,
                                                             String[] keywords, PeerGroupFacetHelper facetInfo  ){
        String facetQuery = getQueryStringFromFacetQueryParams(queryParams);

        //query is of the form key:"value"
        StringBuilder entityValues = new StringBuilder();
        for (String entity : selectedEntities) {
            if(Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                //Ad Peer group entities format: "NtDomain-username"
                //So we have to strip of the "NtDomain" and only take the username
                entity = entity.substring(entity.indexOf("-") + 1);
            }

            // mlarsen: This code is somewhat suspicious - it was inherited from
            // Harish, and it is not clear why it is needed.  The : character is
            // a solr reserved character, and the code originally did not include
            // the ClientUtils.escapeQueryChars() call below, which might take
            // care of the problem.  But , is not a reserved character, so it may
            // be that it really is important to remove portions of the
            if (entity != null && (entity.contains(":") || entity.contains(","))) {
                int index = -1;
                if(entity.contains(":")) {
                    index = entity.indexOf(":");
                } else{
                    index = entity.indexOf(",");
                }

                entity = "*" + entity.substring(index + 1);
            }
            if (entity != null) {
                entity = ClientUtils.escapeQueryChars(entity);
            }

            if (entityValues.length() == 0) {
                entityValues.append( entity);
            } else {
                entityValues.append(" OR ").append(entity).append("*");
            }
        }
        String entityQuery = entityValues.toString();
        entityQuery = facetInfo.entityLabel() + ":(" + entityQuery + ")";

        String queryField = null;
        if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
            AdFacetHelper ad = (AdFacetHelper) facetInfo;
            queryField = "cefSignatureId:*" + ad.cefSignature() + "*";
        }else if(Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)){
            WebFacetHelper web = (WebFacetHelper) facetInfo;
            queryField = web.query();
        }else if(Constants.AdNoveltyDetectorTuple()._1().equals(modelId)){
            AdNoveltyDectectorFacetHelper ad = (AdNoveltyDectectorFacetHelper) facetInfo;
            queryField = "cefSignatureId:*" + ad.cefSignature() + "*";
        }


        //get the entity query first
        String solrEntityQueryString = "";
        if(!entityQuery.isEmpty()){
            solrEntityQueryString = "(" + entityQuery + ")";
        }

        //now add the form the facet query depending on whether solrEntityQueryString exits or not.
        String solrFacetQueryString = "";
        if(facetQuery != null && !facetQuery.isEmpty()){
            String str = "(" + facetQuery + ")";
            solrFacetQueryString = solrEntityQueryString.isEmpty()? str : " AND " + str;
        }

        StringBuilder solrKeywordQueryString = new StringBuilder();
        if(keywords!=null && keywords.length>0){
            boolean addKeywordTerm = false;
            solrKeywordQueryString.append("(");
            for(String keyword: keywords) {
                // Short term fix: we want to exclude terms that are composite terms, or terms like Security-4648*, since
                // those will not appear in the rawlog field. So only add a term if it does not contain either of those
                // characters.
                if (keyword.indexOf('-') == -1 && keyword.indexOf(',') == -1) {
                    if (solrKeywordQueryString.length() > 1) {
                        solrKeywordQueryString.append(" OR ");
                    }
                    keyword = keyword.replace("(", "");
                    keyword = keyword.replace(")", "");
                    keyword = ClientUtils.escapeQueryChars(keyword);
                    solrKeywordQueryString.append("rawLog:");
                    solrKeywordQueryString.append("*" + keyword + "*");
                    addKeywordTerm = true;
                }
            }
            solrKeywordQueryString.append(")");
            if (addKeywordTerm) {
                solrEntityQueryString = solrEntityQueryString + " AND " + solrKeywordQueryString;
            }
        }

        //merge entityQuery and facetQuery
        String q = solrEntityQueryString + solrFacetQueryString;

        //Finally, we can now form the query String for Solr
        String queryString = "";
        if(queryField == null && q.isEmpty()){
            //if we are not able to find any query, return empty result.
            LOGGER.error("Cannot form a valid Solr query from facetKeyValues => " + queryParams + " and selectedEntities =>" + selectedEntities + " for security event Id => " + securityEventId + " and model Id => " + modelId );
            return null;
        }else if(q.isEmpty()){
            queryString = queryField;
        }else if(queryField == null){
            queryString = q;
        }else{
            queryString = queryField + " AND "+ q;
        }

        LOGGER.debug("Solr Facet Query String: " + queryString);
        return queryString;
    }


    /**
     * Creates filter string for searching on the TOPN facets for each search result.
     *
     * @param queryParams
     * @return
     */
    public static String getQueryStringFromFacetQueryParams(Map<String, List<String>> queryParams){
        String facetQuery = "";
        if(queryParams != null && !queryParams.isEmpty()) {
            for (Object facet : queryParams.keySet()) {
                StringBuffer facetBuffer = new StringBuffer();
                List<String> facetValues = queryParams.get(facet.toString());
                if (facetValues != null) {
                    for (String facetH : facetValues) {
                        if (!facetH.isEmpty()) {
                            if (facetBuffer.length() == 0) {
                                facetBuffer.append("\"").append(ClientUtils.escapeQueryChars(facetH)).append("\"");
                            } else {
                                facetBuffer.append(" OR " + "\"").append(ClientUtils.escapeQueryChars(facetH)).append("\"");
                            }
                        }
                    }

                    if (!facetBuffer.toString().isEmpty()) {
                        String key = facet.toString() + ":" + "(" + facetBuffer.toString() + ")";
                        if (facetQuery.isEmpty()) {
                            facetQuery = key;
                        } else {
                            facetQuery = key + " AND " + facetQuery;
                        }
                    }
                }
            }
        }

        return facetQuery;
    }


    /**
     * Lists all the logs associated with an IP or Domain from since 30 days.
     * @param ipOrDomain String DestinationIp or Domain Name
     * @param endTime String specifying endTime for solr Query. Start Time will be (endTime - 30) days
     *
     * @return Listfos all the logs associated with an IP from since 30 days.
     */
    public List<SolrDocument> getWebLogsForDestinationIpOrDomain(CloudSolrServer solrClient, String ipOrDomain, String endTime,
                                                                 int lastNDays) {
        return getLogsForIPsDomains(solrClient, ipOrDomain, null, null,null, endTime, lastNDays);
    }


    /**
     * Searches the web_proxy_mef and returns the logs for matching Ips and domains
     * TODO: Enable search for URLs
     * @param solrClient SolrServerClient
     * @param ips       String comma-separated urls
     * @param domains    String comma-separated domains
     * @param urls String comma-separated urls
     * @param endTime    String specifying end Time in the query
     *
     * @return List of SolrDocument which are web_proxy_mef logs
     */
    @Deprecated
    public List<SolrDocument> getLogsForIPsDomains(CloudSolrServer solrClient, String ips, String domains, String urls, String sourceAddress,
                                                   String endTime, int lastNDays) {
        List<SolrDocument> results = new LinkedList<SolrDocument>();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setShowDebugInfo(true);
        solrQuery.setStart(DEFAULT_START_ROWS);
        solrQuery.setRows(100);

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

        solrQuery.addSort("startTimeISO", getSolrSortOrder("asc"));

        DateTime dateTime = new DateTime(endTime, DateTimeZone.UTC);
        DateTime startTime = dateTime.minusDays(lastNDays);

        String filterQueryString = "startTimeISO:[\"" + startTime + "\" TO \"" + dateTime + "\"]";
        solrQuery.setFilterQueries(filterQueryString);


        QueryResponse response = null;
        try {
            response = solrClient.query(solrQuery);
        } catch (SolrServerException exp) {
            LOGGER.error("Failed to get Search results from web_proxy_mef for ips: " + ips + ", domains: " + domains +" and urls: " + urls + "  => {} ", exp);
        }

        if (response != null) {
            results = response.getResults();
        }

        return results;
    }

    /**
     * TODO Search Impala for logs more than last 15 days old
     * @param solrClient
     * @param blob
     * @param endTime
     * @param lastNDays
     * @return
     */
    @Deprecated
    public List<Map<String, Object>> getLogsFromBlob(CloudSolrServer solrClient, String blob, String endTime, int lastNDays,
                                                     FeatureServiceConfiguration conf){
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        StringBuffer urls = new StringBuffer();
        StringBuffer ips = new StringBuffer();
        StringBuffer domains = new StringBuffer();


        if(blob == null || blob.isEmpty()){
            LOGGER.debug("Empty Input");
            return results;
        }

        // split a string on a comma, \n, \t, space.
        String [] inputArray = blob.split("[\\s,;\\n\\t]+");

        //iterate over each line
        for( String part : inputArray) {

                //check valid url
                if (isValidUrl(part)) {
                    if (urls.length() == 0) {
                        urls.append(part);
                    } else {
                        urls.append(",").append(part);
                    }
                }else if (isValidDomain(part)) {
                    //if valid domain
                    if (domains.length() == 0) {
                        domains.append(part);
                    } else {
                        domains.append(",").append(part);
                    }
                }else if (isValidIP(part)) {
                    //if valid ip
                    if (ips.length() == 0) {
                        ips.append(part);
                    } else {
                        ips.append(",").append(part);
                    }
                }
        }

        if(ips.length() == 0 && domains.length() == 0 && urls.length() == 0) {
            LOGGER.debug("no url or domain or ip found");
            return results;
        }

        return getIocSearchData(solrClient, ips.toString(), domains.toString(), urls.toString(), endTime, lastNDays, conf);
    }

    public List<Map<String, Object>> getIocSearchData(CloudSolrServer solrClient, String ips, String domains, String urls,
                                                      String endTime, int lastNDays, FeatureServiceConfiguration conf) {

        LOGGER.debug((" Input ips: " + ips));
        LOGGER.debug((" Input domains: " + domains));
        LOGGER.debug((" Input urls: " + urls));
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setShowDebugInfo(true);
        solrQuery.setStart(DEFAULT_START_ROWS);
        //TODO: change the end Rows
        solrQuery.setRows(100);

        solrQuery.addField("destinationUrl");
        solrQuery.addField("requestScheme");
        solrQuery.addField("destinationNameOrIp");
        solrQuery.addField("requestPath");
        solrQuery.addField("sourceNameOrIp");
        solrQuery.addField("sourceAddress");
        solrQuery.addField("bytesIn");
        solrQuery.addField("bytesOut");
        solrQuery.addField("startTimeISO");


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
        solrQuery.setQuery(queryString);
        LOGGER.debug("Solr query string: " + queryString);

        DateTime end= new DateTime(endTime, DateTimeZone.UTC);
        DateTime start= new DateTime(end.minusDays(30), DateTimeZone.UTC);
        solrQuery.addDateRangeFacet("startTimeISO", start.toDate(), end.toDate(), "+1DAY");

        solrQuery.addSort("startTimeISO", getSolrSortOrder("desc"));

        DateTime dateTime = new DateTime(endTime, DateTimeZone.UTC);
        DateTime startTime = dateTime.minusDays(lastNDays);

        String filterQueryString = "startTimeISO:[\"" + startTime + "\" TO \"" + dateTime + "\"]";
        solrQuery.setFilterQueries(filterQueryString);

        QueryResponse response = null;
        try {
            response = solrClient.query(solrQuery);
        } catch (SolrServerException exp) {
            LOGGER.error("Failed to get IOC Search results from web_proxy_mef for ips: " + ips + ", domains: " + domains +" and urls: " + urls + "  => {} ", exp);
        }

        if(response != null){

            List<SolrDocument> results = new LinkedList<SolrDocument>();
            if (response != null) {
                results = response.getResults();
            }


            Map<SolrDocHostInfo, SolrDocInfo> solrMap = new HashMap<SolrDocHostInfo, SolrDocInfo>();
            for( SolrDocument doc : results){
                //find threat type:

                String destinationNameOrIp = doc.get("destinationNameOrIp") == null ? "" : (String)doc.get("destinationNameOrIp");
                String requestScheme = doc.get("requestScheme") == null ? "" : (String)doc.get("requestScheme");
                String requestPath = doc.get("requestPath") == null ? "" : (String)doc.get("requestPath");
                String sourceNameOrIp = doc.get("sourceNameOrIp") == null ? "" : (String)doc.get("sourceNameOrIp");
                String sourceAddress = doc.get("sourceAddress") == null ? "" : (String)doc.get("sourceAddress");

                ArrayList<String> destinationUrl = new ArrayList<String>();
                destinationUrl.add(requestScheme);
                destinationUrl.add(destinationNameOrIp);
                destinationUrl.add(requestPath);

                //find the threat type
                String threatType = null;
                if(domains != null && !domains.isEmpty() && domains.contains(destinationNameOrIp)){
                        threatType = IocThreatType.DOMAIN().toString();
                }else if(ips != null && !ips.isEmpty() && ips.contains(destinationNameOrIp)){
                        threatType = IocThreatType.IP().toString();
                }else{
                    threatType = IocThreatType.URL().toString();
                }


                SolrDocHostInfo hostInfo = new SolrDocHostInfo(destinationUrl,destinationNameOrIp,sourceNameOrIp,sourceAddress,threatType);

                long bitsIn = doc.get("bytesIn") == null? 0L : (Long) doc.get("bytesIn") * 8 ;
                long bitsOut = doc.get("bytesOut") == null? 0L : (Long) doc.get("bytesOut") * 8 ;
                Date is = (Date)doc.get("startTimeISO");
                 DateTime d = new DateTime(is, DateTimeZone.UTC);
                String time = d.toDateTimeISO().toString();

                if(solrMap.containsKey(hostInfo)){
                    SolrDocInfo obj = solrMap.get(hostInfo);
                    obj.setConnections(obj.getConnections() + 1);
                    obj.setBitsIn(bitsIn + obj.getBitsIn());
                    obj.setBitsOut(bitsOut + obj.getBitsOut());
                    if(time.compareTo(obj.getFirstSeen()) < 0){
                        obj.setFirstSeen(time);
                    }else if(time.compareTo(obj.getLastSeen()) > 0){
                        obj.setLastSeen(time);
                    }

                }else{
                    SolrDocInfo obj = new SolrDocInfo(bitsIn, bitsOut, 1, time, time);
                    solrMap.put(hostInfo, obj);
                }
            }

            return getEntityFusedResultSet(solrClient, solrMap, endTime,lastNDays, conf);
        }


        return new LinkedList<Map<String, Object>>();

    }

    /**
     * Takes the sourceIpAddress from solr result and converts it into corresponding user name based on time
     *
     * @param solrClient SolrServerClient
     * @param solrMap Map<SolrDocHostInfo, SolrDocInfo>
     * @param endTime
     * @param conf
     * @return
     */
    private List<Map<String, Object>> getEntityFusedResultSet(CloudSolrServer solrClient, Map<SolrDocHostInfo,
            SolrDocInfo> solrMap, String endTime, int lastNDays, FeatureServiceConfiguration conf) {

        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        Connection conn = null;
            try {
                conn = PhoenixUtils.getPhoenixConnection(conf);
                String tableName = EntityFusionHourlyRollUp.getName(conf);
                for (Map.Entry<SolrDocHostInfo, SolrDocInfo> entry : solrMap.entrySet()) {
                    //going back 48 hours. This time can be changed in configuration file as "backoffPeriodHours"
                    DateTime time = new DateTime(entry.getValue().getLastSeen(), DateTimeZone.UTC);
                    DateTime newStartTime = time.minusHours(conf.getEntityFusionConfiguration().getBackoffPeriodHours());
                    String sqlStr = "SELECT " + EntityFusionHourlyRollUp.FUSION_TIME() + "," +
                            EntityFusionHourlyRollUp.IP_ADDRESS() + "," +
                            EntityFusionHourlyRollUp.USER_NAME() + "," +
                            EntityFusionHourlyRollUp.HOST_NAME() +
                            " FROM " + tableName +
                            " WHERE " + EntityFusionHourlyRollUp.IP_ADDRESS() + " = ? " +
                            " AND " + EntityFusionHourlyRollUp.FUSION_TIME() + " >= ? " +
                            " AND " + EntityFusionHourlyRollUp.FUSION_TIME() + " <= ? " +
                            " ORDER BY " + EntityFusionHourlyRollUp.FUSION_TIME() +  " ASC ";
                    PreparedStatement pstmt = conn.prepareStatement(sqlStr);
                    pstmt.setString(1, entry.getKey().sourceNameOrIp);
                    pstmt.setString(2, newStartTime.toString());
                    pstmt.setString(3, entry.getValue().getLastSeen());
                    ResultSet rs = pstmt.executeQuery();
                    Map<String, Object> output = new HashMap<String, Object>();
                    String threatType = entry.getKey().iocThreatType;

                    output.put("sourceAddress", entry.getKey().sourceNameOrIp );
                    output.put("threatValue", entry.getKey().destinationNameOrIp );
                    output.put("destinationIp", entry.getKey().destinationNameOrIp);
                    output.put("threatType", threatType);
                    output.put("bitsIn", entry.getValue().bitsIn);
                    output.put("bitsOut", entry.getValue().bitsOut);
                    output.put("firstSeen", entry.getValue().firstSeen);
                    output.put("lastSeen", entry.getValue().lastSeen);
                    output.put("connections", entry.getValue().connections);
                    output.put("history", facetDao.getEventCounts(solrClient,endTime,entry.getKey().getUrlString(),
                            entry.getKey().getDomain(), entry.getKey().getIp(), entry.getKey().sourceNameOrIp, lastNDays));
                    String userName = "";
                    String hostName = "";

                    while (rs.next()) {
                        String user = rs.getString(EntityFusionHourlyRollUp.USER_NAME().toString());
                        if(user != null && !user.isEmpty()){
                            userName = user;
                        }

                        String host = rs.getString(EntityFusionHourlyRollUp.HOST_NAME().toString());
                        if(host != null && !host.isEmpty()){
                            hostName = host;
                        }
                    }
                    output.put("userName", userName);
                    output.put("hostName", hostName);

                    resultList.add(output);
                }
            } catch (Exception e) {
               LOGGER.error("Failed to get user name, host name from entity fusion : " + e);
            } finally {
                try {
                    if(conn != null) {
                        conn.close();
                    }
                } catch (SQLException sql) {
                    LOGGER.error("Failed to close connection while querying table" + EntityFusionHourlyRollUp.getName(conf) + " with exception " + sql );
                }
            }

        return resultList;

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

    public void getEventCounts(CloudSolrServer solrClient, String endTime){
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setShowDebugInfo(true);
            solrQuery.setStart(DEFAULT_START_ROWS);
        solrQuery.setFacet(true);

            //TODO: change the end Rows
            solrQuery.setRows(100);
            solrQuery.addField("destinationUrl");
            solrQuery.addField("destinationNameOrIp");
            solrQuery.addField("sourceNameOrIp");
            solrQuery.addField("sourceAddress");
            solrQuery.addField("bytesIn");
            solrQuery.addField("bytesOut");
            solrQuery.addField("startTimeISO");

        //facet=true&facet.date=startTimeISO&facet.date.start=NOW/DAY-30DAYS&facet.date.end=NOW/DAY%2B1DAY&facet.date.gap=%2B1DAY
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("facet", "true");
        params.set("facet.date", "startTimeISO");
        params.set("facet.date.start", "NOW/DAY-30DAYS");
        params.set("facet.date.end", "NOW/DAY%2B1DAY");
        params.set("facet.date.gap", "%2B1DAY");

        String queryString = "destinationUrl:*google*";
        DateTime date= new DateTime("2014-11-14T14:37:30.00", DateTimeZone.UTC);
        DateTime date1= new DateTime("2014-12-14T14:37:30.00", DateTimeZone.UTC);

        solrQuery.addDateRangeFacet("startTimeISO", date.toDate(), date1.toDate(), "+1DAY");

        solrQuery.setQuery(queryString);

        QueryResponse response = null;
        try {
            response = solrClient.query(solrQuery);
        } catch (SolrServerException exp) {
            LOGGER.error("Failed to get facet results => {} ", exp);
        }

        SimpleOrderedMap<Object> io  = (SimpleOrderedMap<Object>) response.getResponse().get("facet_counts");
        SimpleOrderedMap<Object> ii = (SimpleOrderedMap<Object>) io.get("facet_ranges");
        SimpleOrderedMap<Object> i2 = (SimpleOrderedMap<Object>)ii.get("startTimeISO");
        NamedList<Map<String, Integer>> obj = (NamedList<Map<String, Integer>>) i2.get("counts");

        Map<String, List<Map<String, Long>>> facetFieldsMap = Maps.newHashMap();

        if (response != null) {

            List<FacetField> facetFieldsList = response.getFacetFields();

            for (int i = 0; i < facetFieldsList.size(); i++) {


                List<Map<String, Long>> facetList = Lists.newLinkedList();
                FacetField facetField = facetFieldsList.get(i);
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count val : values) {
                    Map<String, Long> facetKeyValues = Maps.newLinkedHashMap();
                    facetKeyValues.put(val.getName(), val.getCount());
                    facetList.add(facetKeyValues);
                }

                facetFieldsMap.put(facetField.getName(), facetList);

            }
        }
    }

    /**
     * @param sortOrder Sort String "asc" or "desc"
     * @return SolrQuery.ORDER
     */
    private SolrQuery.ORDER getSolrSortOrder(String sortOrder) {
        if (sortOrder.equalsIgnoreCase("asc")) {
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
                if(!value.isEmpty()) {

                    if (buf.length() == 0) {
                        buf.append("*").append(value).append("*");
                    } else {
                        buf.append(" OR " + "*").append(value).append("*");
                    }

                }
            }
        }
        return buf.toString();
    }


    /**
     * checks if a String is a valid url
     * @param str String
     * @return java.lang.Boolean true is the input string is a valid url, false otherwise.
     */
    private boolean isValidUrl(String str) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(str);
    }

    /**
     * checks if a String is a valid domain
     * @param str String
     * @return java.lang.Boolean true is the input string is a valid domain, false otherwise.
     */
    private boolean isValidDomain(String str) {
        DomainValidator domainValidator = DomainValidator.getInstance();
        return domainValidator.isValid(str);
    }

    /**
     * checks if a String is a valid IP
     * @param str String
     * @return java.lang.Boolean true is the input string is a valid IP, false otherwise.
     */
    private boolean isValidIP(String str) {
        InetAddressValidator ipValidator = InetAddressValidator.getInstance();
        return ipValidator.isValid(str);
    }
    /**
     * Class that wraps in the Solr info for Source and Destination info
     */
    class SolrDocHostInfo{
        public List<String> destinationUrl;
        public String destinationNameOrIp;
        public String sourceNameOrIp;
        public String sourceAddress;
        public String iocThreatType;

        SolrDocHostInfo(List<String> destinationUrl, String destinationNameOrIp, String sourceNameOrIp, String sourceAddress, String iocThreatType){
            this.destinationUrl = destinationUrl;
            this.destinationNameOrIp = destinationNameOrIp;
            this.sourceNameOrIp = sourceNameOrIp;
            this.sourceAddress = sourceAddress;
            this.iocThreatType = iocThreatType;
        }

        /**
         * Solr indexing stores filed "destinationUrl" as ArrayList<String>
         * eg:
         * "destinationUrl": [
         *                  "http",
         *                  "www.yahoohoo.com",
         *                  "/afgev"
         *                    ]
         * Format: destinationUrl: [ "requestScheme", "destinationNameOrIp", "requestPath" ]
         *
         * The method converts this field into a url String.
         * For the example provided above, the url string will be "http://www.yahoohoo.com/afgev"
         *
         * @return String representing the Url
         */
        public String getUrlString(){
            StringBuffer resultBuf = new StringBuffer();
            if(destinationUrl != null && !destinationUrl.isEmpty()){
                for(int i = 0; i < destinationUrl.size(); i++){
                    if(i == 0){
                        resultBuf = resultBuf.append(destinationUrl.get(i)).append("://");
                    }else{
                        resultBuf = resultBuf.append(destinationUrl.get(i));
                    }
                }
            }
            return resultBuf.toString();
        }

        /**
         * If threatType is "domain", this method returns the value of field "destinationNameOrIp"
         *
         * @return String representing domain
         */
        public String getDomain(){
            if(iocThreatType.equalsIgnoreCase(IocThreatType.DOMAIN().toString()))
                return destinationNameOrIp;

            return null;
        }

        /**
         * If threatType is "ip", this method returns the value of field "destinationNameOrIp"
         *
         * @return String representing Ip
         */
        public String getIp(){
            if(iocThreatType.equalsIgnoreCase(IocThreatType.IP().toString()))
                return destinationNameOrIp;

            return null;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SolrDocHostInfo that = (SolrDocHostInfo) o;

            if (!destinationNameOrIp.equals(that.destinationNameOrIp)) return false;
            if (!destinationUrl.equals(that.destinationUrl)) return false;
            if (!iocThreatType.equals(that.iocThreatType)) return false;
            if (!sourceAddress.equals(that.sourceAddress)) return false;
            if (!sourceNameOrIp.equals(that.sourceNameOrIp)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = destinationUrl.hashCode();
            result = 31 * result + destinationNameOrIp.hashCode();
            result = 31 * result + sourceNameOrIp.hashCode();
            result = 31 * result + sourceAddress.hashCode();
            result = 31 * result + iocThreatType.hashCode();
            return result;
        }
    }

    /**
     * Class to wrap all the properties (eg. connections, bytesIn, bytesOut etc) in the result-set of the SolrDocument
     */
    class SolrDocInfo{
        public long bitsIn;
        public long bitsOut;
        public long connections;
        public String firstSeen;
        public String lastSeen;

        SolrDocInfo(long bitsIn, long bitsOut, long connections, String lastSeen, String firstSeen) {
            this.bitsIn = bitsIn;
            this.bitsOut = bitsOut;
            this.connections = connections;
            this.lastSeen = lastSeen;
            this.firstSeen = firstSeen;
        }

        public long getBitsIn() {
            return bitsIn;
        }

        public void setBitsIn(long bitsIn) {
            this.bitsIn = bitsIn;
        }

        public long getBitsOut() {
            return bitsOut;
        }

        public void setBitsOut(long bitsOut) {
            this.bitsOut = bitsOut;
        }

        public long getConnections() {
            return connections;
        }

        public void setConnections(long connections) {
            this.connections = connections;
        }

        public String getFirstSeen() {
            return firstSeen;
        }

        public void setFirstSeen(String firstSeen) {
            this.firstSeen = firstSeen;
        }

        public String getLastSeen() {
            return lastSeen;
        }

        public void setLastSeen(String lastSeen) {
            this.lastSeen = lastSeen;
        }
    }
}
