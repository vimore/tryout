package com.securityx.modelfeature.dao.solr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.dao.SearchImpalaDao;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.MiscUtils;
import com.securityx.modelfeature.utils.SearchUtils;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.CloudChamberDao;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by harish on 12/26/14.
 */
public class TimeSeriesFacetDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSeriesFacetDao.class);


    /**
     * Returns the top N facet results from the solr query for the specified input "filed"
     *
     * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
     * @param iamSolrClient      CloudSolrServer connection for iam_mef collection
     * @param modelId            Integer model Id
     * @param fieldName          String filed type; eit her "source" or "destination"
     * @param startTime          String specifying the startTime
     * @param endTime            String specifying the endTime
     * @param facetMinCount      Integer specifying min count for facets
     * @param facetLimit         Integer facet limit
     * @param facetStartRows     Integer start Row no.
     * @param facetEndRows       Integer end Row no.
     * @return
     */
    public List<Map<String, Object>> getTopN(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                             CloudSolrServer taniumHostInfoSolrClient,
                                             CloudSolrServer taniumHetSolrClient,
                                             CloudSolrServer taniumUetSolrClient,
                                             int modelId, String fieldName,
                                             String startTime, String endTime,
                                             int facetMinCount, /* topN */int facetLimit, int facetStartRows, int facetEndRows) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(facetLimit);
        solrQuery.setFacetMinCount(facetMinCount);
        solrQuery.setStart(facetStartRows);
        solrQuery.setRows(facetEndRows);

        solrQuery.addFacetField(fieldName);

        solrQuery.setFacetSort(FacetParams.FACET_SORT_COUNT);
        solrQuery.setShowDebugInfo(true);


        //Sorl query  for NOT EQUALS Operator is: -destinationNameOrIp:"<ip address>"
        //fieldName NOT EQUAL to "-"
        solrQuery.setQuery(" -" + fieldName + ":" + "\"-\"" );
        solrQuery.addSort("startTimeISO", SearchUtils.getSolrSortOrder("desc"));

        String filterQueryString = "startTimeISO:[" + startTime + " TO " + endTime + "]";
        solrQuery.setFilterQueries(filterQueryString);

        QueryResponse response = null;
        try {
            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = webProxySolrClient.query(solrQuery);
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = iamSolrClient.query(solrQuery);
            } else if (Constants.TaniumModelTuple()._1().equals(modelId) || Constants.EndpointGlobalModelTuple()._1().equals(modelId) || Constants.EndpointLocalModelTuple()._1().equals(modelId)) {
                response = taniumHostInfoSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to get topN facet results for model: " + modelId + "=> "+ exp.getMessage(), exp);
        }


        List<Map<String, Object>> facetList = Lists.newLinkedList();
        if (response != null) {

            List<FacetField> facetFieldsList = response.getFacetFields();

            long total = response.getResults().getNumFound();
            //int count = 0;
            for (int i = 0; i < facetFieldsList.size(); i++) {

                FacetField facetField = facetFieldsList.get(i);
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count val : values) {
              //      if (!val.getName().equals("-") && val.getCount() > 0) {
                        Map<String, Object> facetKeyValues = Maps.newLinkedHashMap();
                        double count = (double) val.getCount();
                        facetKeyValues.put("value", val.getName());
                        facetKeyValues.put("count", count);
                        facetKeyValues.put("percent", MiscUtils.getPercentage(count, total));
                        facetList.add(facetKeyValues);
               //     }
                }


            }
        }
        return facetList;
    }


    public List<Map<String, Object>> getTopNSourceDestination(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                                              CloudSolrServer taniumHostInfoSolrClient,
                                                              CloudSolrServer taniumHetSolrClient,
                                                              CloudSolrServer taniumUetSolrClient,
                                                              int modelId, String fieldType,
                                                              String startTime, String endTime,
                                                              int facetMinCount, /* topN */int facetLimit, int facetStartRows, int facetEndRows,
                                                              FeatureServiceConfiguration conf) {

        //get fieldname from fieldType
        String fieldName = SearchUtils.getSrcDestFieldName(modelId, fieldType);

        if (fieldName.isEmpty() || fieldName == null) {
            LOGGER.error("Invalid Input: Field Name not found");
            return new LinkedList<Map<String, Object>>();
        }

        int daysElapsed = Days.daysBetween(new DateTime(startTime), DateTime.now()).getDays();
        if(daysElapsed <= 15) {

            return getTopN(webProxySolrClient, iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,
                    modelId, fieldName, startTime, endTime, facetMinCount, facetLimit, facetStartRows, facetEndRows);
        } else {
            return new SearchImpalaDao(conf).getFacetsAndCounts(modelId, fieldName, startTime, endTime, facetLimit, false);
        }
    }

    /**
     * Returns the top N newly observed domains in the last 30 days from the solr query for the specified input Collection
     *
     * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
     * @param iamSolrClient      CloudSolrServer connection for iam_mef collection
     * @param modelId            Integer model Id
     * @param startTime          String specifying the startTime
     * @param endTime            String specifying the endTime
     * @param facetMinCount      Integer specifying min count for facets
     * @param facetLimit         Integer facet limit
     * @param facetStartRows     Integer start Row no.
     * @param facetEndRows       Integer end Row no.
     * @return top N newly observed Domain (NOD) with stats (count and %age)
     */
    public List<Map<String, Object>> getNODs(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                                             int modelId,
                                             String startTime, String endTime,
                                             int facetMinCount, /* topN */int facetLimit, int facetStartRows, int facetEndRows, FeatureServiceConfiguration conf) {

        int daysElapsed = Days.daysBetween(new DateTime(startTime), DateTime.now()).getDays();
        if(daysElapsed <= 15) {
            QueryResponse response = getListOfNODs(webProxySolrClient, iamSolrClient, modelId, startTime, endTime, facetMinCount, facetLimit, facetStartRows, facetEndRows);
            long total = getTotalForDay(webProxySolrClient, iamSolrClient, modelId, startTime, endTime);
            return getNODsWithAge(response, total, modelId, conf);
        } else {
            return new SearchImpalaDao(conf).getNODs(modelId, startTime, endTime,facetLimit, conf);
        }
    }

    /**
     * Iterates over the solr response and returns list of Nods with age
     *
     * @param response
     * @param total
     *
     * @return
     */
    private List<Map<String, Object>> getNODsWithAge(QueryResponse response,long total, int modelId, FeatureServiceConfiguration conf) {
        List<Map<String, Object>> retList = Lists.newLinkedList();

        CloudChamberDao dao = new CloudChamberDao(conf);

        if (response != null) {

            List<FacetField> facetFieldsList = response.getFacetFields();

            for (int i = 0; i < facetFieldsList.size(); i++) {

                List<Map<String, Long>> facetList = Lists.newLinkedList();
                FacetField facetField = facetFieldsList.get(i);
                List<FacetField.Count> values = facetField.getValues();
                for (FacetField.Count val : values) {
                    Map<String, Object> map = Maps.newHashMap();
                    String domainName = val.getName();
                    long count = val.getCount();
                    map.put("name", domainName);
                    map.put("count", count);
                    map.put("percent", MiscUtils.getPercentage(count, total));
                    //No Domain Age for Active Directory
                    if (!Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                        try {
                            String creationDate = dao.getDomainCreationDate(domainName);
                            String diff = "";
                            if (creationDate != null) {
                                map.put("creationTime", creationDate);
                                diff = Integer.toString(Days.daysBetween(new DateTime(creationDate), DateTime.now()).getDays());
                            }
                            map.put("ageInDays", diff);
                        } catch (Exception e) {
                            LOGGER.error("Failed to get domain age for: " + domainName + " => " + e);
                        }
                    }
                    retList.add(map);
                }


            }
        }



        return retList;

    }


    /**
     * Returns a QueryResponse solr object with list of NODs
     *
     * @param webProxySolrClient
     * @param iamSolrClient
     * @param modelId
     * @param startTime
     * @param endTime
     * @param facetMinCount
     * @param facetLimit
     * @param facetStartRows
     * @param facetEndRows
     * @return
     */
    private QueryResponse getListOfNODs(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient, int modelId, String startTime, String endTime, int facetMinCount, int facetLimit, int facetStartRows, int facetEndRows) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(facetLimit);
        solrQuery.setFacetMinCount(facetMinCount);
        solrQuery.setStart(facetStartRows);
        solrQuery.setRows(facetEndRows);

        solrQuery.setFacetSort(FacetParams.FACET_SORT_COUNT);

        solrQuery.setQuery("*:*");
        solrQuery.addSort("startTimeISO", SearchUtils.getSolrSortOrder("desc"));

        String filterQueryString = "startTimeISO:[\"" + startTime + "\" TO \"" + endTime + "\"]";
        solrQuery.add("fq", filterQueryString);

        //going N days back
        DateTime dateTime = new DateTime(startTime, DateTimeZone.UTC);
        String oldEndTime = startTime;

        //TODO: number of days "30", should be parameterized last "N" days.
        DateTime oldStartTime = dateTime.minusDays(30);

        String joinQuery = "-_query_:\"{!join from= %s to=%s }startTimeISO:[" + oldStartTime + " TO " + oldEndTime + "]\"";

        QueryResponse response = null;
        try {
            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
                String fqStr = String.format(joinQuery, "destinationNameOrIp", "destinationNameOrIp");
                solrQuery.add("fq", fqStr);
                solrQuery.addFacetField("destinationNameOrIp");
                response = webProxySolrClient.query(solrQuery);
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                String fqStr = String.format("-_query_:\"{!join from= %s to=%s }startTimeISO:[" + oldStartTime + " TO " + oldEndTime + "]\"", "destinationUserName", "destinationUserName");
                solrQuery.add("fq", fqStr);
                solrQuery.addFacetField("destinationUserName");
                response = iamSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to get facet results for NODs for model id: " + modelId + " => {} ", exp);
        }
        return response;

    }

    /**
     * Straight forward query to solr to get the total number of docs between a given start Time and endTime
     *
     * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
     * @param iamSolrClient      CloudSolrServer connection for iam_mef collection
     * @param modelId            Integer model Id
     * @param startTime          String specifying the startTime
     * @param endTime            String specifying the endTime
     *
     * @return long count of total number of docs found between the input start time and end time
     */
    public long getTotalForDay(CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                               int modelId, String startTime, String endTime){
        long total = 0;
        SolrQuery solrQuery = new SolrQuery();
        //number of rows = 0, as we only need the count of number of documents.
        solrQuery.setStart(0);
        solrQuery.setRows(0);
        solrQuery.setQuery("*:*");
        String filterQueryString = "startTimeISO:[\"" + startTime + "\" TO \"" + endTime + "\"]";
        solrQuery.setFilterQueries(filterQueryString);
        QueryResponse response = null;
        try {
            if (Constants.WebPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = webProxySolrClient.query(solrQuery);
            } else if (Constants.ADPeerAnomaliesModelTuple()._1().equals(modelId)) {
                response = iamSolrClient.query(solrQuery);
            }
        } catch (Exception exp) {
            LOGGER.error("Failed to get the total number of solr documents between " + startTime + " and " + endTime + " for model id => " + modelId + " => {} ", exp);
        }
        if(response != null){
            SolrDocumentList results = response.getResults();
            if(results != null){
             total = results.getNumFound();

            }
        }
        return total;
    }
}
