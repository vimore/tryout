package com.e8.palam.dao;

import com.e8.palam.TestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.SolrServerClient;
import com.securityx.modelfeature.common.inputs.TimeSeriesInput;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.SearchImpalaDao;
import com.securityx.modelfeature.dao.TimeSeriesDao;
import com.securityx.modelfeature.dao.solr.SearchSolrDao;
import com.securityx.modelfeature.dao.solr.WebAnomalyProfileSolrDao;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.PeerGroupFacetHelper;
import com.securityx.modelfeature.utils.SearchUtils;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class SolrImpalaRawLogsComparisonOnlineTest extends TestBase {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    public SolrImpalaRawLogsComparisonOnlineTest()  {
        super();
    }

    //private SearchDao searchDao = null;

    Map<String, List<String>> emptyQueryParams = new HashMap<>();

    private static FeatureServiceConfiguration configuration = new FeatureServiceConfiguration();
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static SearchSolrDao solrDao = null;
    private static SearchImpalaDao impalaDao = null;
    private static CloudSolrServer webProxySolrClient = null;
    private static CloudSolrServer iamSolrClient;

    @Before
    public void setup() throws Exception{
        super.setup();
        String confFile = System.getProperty("user.dir")+"/src/main/config/dev_cfg.yml";
        configuration = new ConfigurationFactory<>(FeatureServiceConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));

        //configuration.setZkQuorum("cluster6-srv1");
        //configuration.setSolrQuorum("cluster6-srv1:2181,cluster6-srv2:2181,cluster6-srv3:2181,cluster6-srv4:2181,cluster6-srv5:2181/solr");

        SolrServerClient solrClient = new SolrServerClient(configuration);
        webProxySolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.WebPeerAnomaliesModelTuple()._1()));
        iamSolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.ADPeerAnomaliesModelTuple()._1()));

        solrDao = new SearchSolrDao(webProxySolrClient,iamSolrClient);
        impalaDao = new SearchImpalaDao(configuration);
    }

    @Test
    public void getSearchResultsForSourceDestinationTest() throws Exception {

        int modelId = Constants.WebPeerAnomaliesModelTuple()._1();
        String startTime = "2016-10-26T00:00:00.000Z";
        String endTime = "2016-10-27T00:00:00.000Z";
        List<String> sources = new ArrayList<>();
        int summaryFacetLimit = 5;
        int endRows=50;
        int pageNo=1;
        String sortField ="startTimeISO";
        String sortOrder = "DESC";
        boolean summarize = true;
        int startRecord = endRows * (pageNo - 1);

        sources.add("192.168.12.30");

        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<String> destinations = new ArrayList<>();
        destinations.add("r20swj13mr.microsoft.com");
        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; rv:11.0) like Gecko"};
        queryParams.put("requestClientApplication", Arrays.asList(paramList));

        List<Map<String, Object>> impalaResults = impalaDao.getSearchResultsForSourceDestination(modelId,  sources,
                destinations, queryParams, startTime, endTime, summaryFacetLimit, endRows, pageNo,
                sortField, sortOrder, summarize, cache);
        assertNotNull(impalaResults);

        List<Map<String, Object>> solrResults = solrDao.getSearchResultsForSourceDestination(modelId,  sources,
                destinations, queryParams, startTime, endTime, summaryFacetLimit, endRows, pageNo,
                sortField, sortOrder, summarize, cache);
        assertNotNull(solrResults);

        compareSolrImpalaResults(solrResults, impalaResults, true);

        if(summarize)
            compareSolrImpalaSummary(solrResults.get(0),impalaResults.get(0));

    }

    @Test
    public void getFacetedSearchResultsTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 3;
        int securityEventId =22;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        String[] selectedEntities = new String[]{"-jyria"};
        String startTime = "2016-10-26T00:00:00.000Z";
        String endTime = "2016-10-27T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows=50;
        int pageNo=1;
        String sortField ="startTimeISO";
        String sortOrder = "DESC";
        boolean summarize = true;
        //int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"E8SEC"};
        queryParams.put("destinationNtDomain", Arrays.asList(paramList));

        List<Map<String, Object>> impalaResults = impalaDao.getFacetedSearchResults(modelId,  securityEventId,
                queryParams, selectedEntities, new String[]{},startTime,
                endTime, summaryFacetLimit, endRows, pageNo, sortField, sortOrder, summarize, cache);
        assertNotNull(impalaResults);

        List<Map<String, Object>> solrResults = solrDao.getFacetedSearchResults(modelId,  securityEventId,
                queryParams, selectedEntities, new String[]{},startTime,
                endTime, summaryFacetLimit, endRows, pageNo, sortField, sortOrder, summarize, cache);
        assertNotNull(solrResults);
        compareSolrImpalaResults(solrResults, impalaResults, true);

        if(summarize)
            compareSolrImpalaSummary(solrResults.get(0),impalaResults.get(0));
    }

    @Test
    public void getWebAnomalyFacetedSearchResultsTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 6;
        String source = "w2k8r2-ad$";
        String startTime = "2016-10-26T00:00:00.000Z";
        String endTime = "2016-10-27T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows=20;
        int pageNo=1;
        String sortField ="startTimeISO";
        String sortOrder = "DESC";
        boolean summarize = true;
        int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"192.168.12.18"};
        queryParams.put("sourceAddress", Arrays.asList(paramList));

        List<Map<String, Object>> impalaResults = impalaDao.getWebAnomalyFacetedSearchResults(modelId, queryParams,
                source, startTime, endTime, endRows, pageNo, summaryFacetLimit, sortField,
                sortOrder, summarize, cache);
        assertNotNull(impalaResults);

        List<Map<String, Object>> solrResults = new WebAnomalyProfileSolrDao().getFacetedSearchResults(webProxySolrClient, iamSolrClient,
                                                        modelId, queryParams, source, startTime, endTime, endRows,
                                                        pageNo, summaryFacetLimit, sortField,
                                                        sortOrder, summarize, cache);
        assertNotNull(solrResults);
        compareSolrImpalaResults(solrResults, impalaResults, false);

        if(summarize)
            compareSolrImpalaSummary(solrResults.get(0),impalaResults.get(0));

    }

    @Test
    public void getTimeSeriesResultsForHttpTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(2);
        input.setStartTime("2016-10-26T00:00:00.000Z");
        input.setEndTime("2016-10-27T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("startTimeISO");
        input.setSortOrder("DESC");
        input.setSummarize(true);
        int startRecord = input.getNumRows() * (input.getPageNo() - 1);

        List<Map<String,String>> facetList = new LinkedList<>();
        Map<String,String> facetMap = new HashMap<>();
        facetMap.put("typeField","cefSignatureId");
        facetMap.put("group","2xx OK");
        //facetMap.put("groupId","200");
        //"facets":[{"typeField":"cefSignatureId","group":"2xx OK","groupId":"200"},{"typeField":"cefSignatureId","group":"2xx OK","groupId":null},{"typeField":"topSource","groupId":"10.10.7.2"},{"typeField":"topDestination","groupId":"10.10.30.224"}]
        facetList.add(facetMap);

        facetMap = new HashMap<>();
        facetMap.put("typeField","cefSignatureId");
        facetMap.put("group","2xx OK");
        facetMap.put("groupId","200");

        facetList.add(facetMap);


        facetMap = new HashMap<>();
        facetMap.put("typeField","topSource");
        facetMap.put("groupId","192.168.12.30");
        //facetMap.put("groupId","200");
        facetList.add(facetMap);

        facetMap = new HashMap<>();
        facetMap.put("typeField","topDestination");
        facetMap.put("groupId","r20swj13mr.microsoft.com");
        //facetMap.put("groupId","200");
        facetList.add(facetMap);

        /*facetMap = new HashMap<>();
        facetMap.put("typeField","requestScheme");
        facetMap.put("group","HTTP");
        //facetMap.put("groupId","200");
        facetList.add(facetMap);*/

        input.setFacets(facetList);
        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"192.168.12.30"};
        queryParams.put("sourceNameOrIp", Arrays.asList(paramList));
        input.setQueryParams(queryParams);

        List<Map<String, Object>> impalaResults = impalaDao.getTimeSeriesSearchResults(input, cache);
        assertNotNull(impalaResults);

        List<Map<String, Object>> solrResults = (List<Map<String,Object>>)new TimeSeriesDao(configuration).getTimeSeriesSearchResults(webProxySolrClient,
                                                    iamSolrClient, null, null, null, input, cache);
        assertNotNull(solrResults);

        compareSolrImpalaResults(solrResults, impalaResults, true);
        if(input.isSummarize())
            compareSolrImpalaSummary(solrResults.get(0),impalaResults.get(0));
    }

    @Test
    public void getTimeSeriesresultsForAdTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(3);
        input.setStartTime("2016-10-26T00:00:00.000Z");
        input.setEndTime("2016-10-27T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("startTimeISO");
        input.setSortOrder("DESC");
        input.setSummarize(true);
        int startRecord = input.getNumRows() * (input.getPageNo() - 1);

        List<Map<String,String>> facetList = new LinkedList<>();
        Map<String,String> facetMap = new HashMap<>();
        facetMap.put("typeField","event");
        facetMap.put("group","Logon/Logoff");
        facetMap.put("groupId","4624");
        //"facets":[{"typeField":"cefSignatureId","group":"2xx OK","groupId":"200"},{"typeField":"cefSignatureId","group":"2xx OK","groupId":null},{"typeField":"topSource","groupId":"10.10.7.2"},{"typeField":"topDestination","groupId":"10.10.30.224"}]
        facetList.add(facetMap);

        input.setFacets(facetList);
        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"E8SEC"};
        queryParams.put("sourceNtDomain", Arrays.asList(paramList));
        input.setQueryParams(queryParams);

        List<Map<String, Object>> impalaResults = impalaDao.getTimeSeriesSearchResults(input, cache);
        assertNotNull(impalaResults);

        List<Map<String, Object>> solrResults = (List<Map<String,Object>>)new TimeSeriesDao(configuration).getTimeSeriesSearchResults(webProxySolrClient,
                iamSolrClient, null, null, null, input, cache);
        assertNotNull(solrResults);

        compareSolrImpalaResults(solrResults, impalaResults, true);

        if(input.isSummarize())
            compareSolrImpalaSummary(solrResults.get(0),impalaResults.get(0));
    }

    @Test
    public void getAutoCompleteRecordsTest() throws Exception{

        int modelId = 2;
        String fieldType = "destination";
        String incomingString = "r";
        String startTime = "2016-10-26T00:00:00.000Z";
        String endTime = "2016-10-27T00:00:00.000Z";
        int startRecord = 0;
        int pageSize = 10;

        Set<String> impalaResults = impalaDao.getAutoCompleteRecords(modelId, fieldType, incomingString,
                startTime, endTime, startRecord, pageSize);
        assertNotNull(impalaResults);
        //assertNotEquals(0,impalaResults.size());
        /*for(String values : results) {
            if(values.startsWith(incomingString)) {
                assertTrue("Got expected entity [" + values + "]", true);
            } else {
                assertTrue("Got unexpected entity [" + values + "]", false);
            }
        }*/
        Set<String> solrResults = new TimeSeriesDao(configuration).getAutoCompleteRecords(webProxySolrClient, iamSolrClient,
                                                                        null, null, null, modelId, fieldType, incomingString,
                                                                        startTime, endTime, 1, pageSize, configuration);
        assertNotNull(solrResults);
        assertEquals(String.format("Number of results returned by Solr: %d is not equal to  Number of results returned by Impala: %d",solrResults.size(), impalaResults.size()), solrResults.size(), impalaResults.size());

        //assertNotEquals(0,solrResults.size());
        if(solrResults.size()  > 0 && impalaResults.size() > 0)
            assertTrue(solrResults.containsAll(impalaResults));
    }

    @Test
    public void test_E82649(){
        //curl 'http://cluster5-e8:9080/service/search'  -H 'Content-Type: application/json' -H 'Accept: application/json' --data-binary '{"startTime":"2016-10-16T00:00:00.000Z","endTime":"2016-10-29T00:00:00.000Z","numRows":50,"pageNo":1,"summarize":true,"modelId":2,"securityEventId":10,"selectedEntities":["192.168.12.18"],"queryParams":{},"sortField":"startTimeISO","sortOrder":"desc"}' --compressed --insecure
        int modelId = 2;
        int securityEventId = 10;
        Map<String, List<String>> queryParams = new HashMap<>();
        String[] selectedEntities = new String[]{"192.168.12.18"};
        String[] keywords = null;
        String startTime = "2016-10-16T00:00:00.000Z";
        String endTime = "2016-10-29T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows = 50;
        int pageNo = 1;
        String sortField = "startTimeISO";
        String sortOrder = "desc";
        boolean summarize = true;
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<Map<String, Object>> impalaResults = impalaDao.getFacetedSearchResults(modelId, securityEventId,
                queryParams, selectedEntities, keywords, startTime, endTime,
                summaryFacetLimit, endRows, pageNo, sortField, sortOrder,summarize, cache);

        List<Map<String, Object>> solrResults = solrDao.getFacetedSearchResults(modelId, securityEventId,
                queryParams, selectedEntities, keywords, startTime, endTime,
                summaryFacetLimit, endRows, pageNo, sortField, sortOrder,summarize, cache);

        assertNotNull(impalaResults);
        assertNotNull(solrResults);
        assertEquals(String.format("Number of results returned by Solr: %d is not equal to  Number of results returned by Impala: %d",solrResults.size(), impalaResults.size()), solrResults.size(), impalaResults.size());

        compareSolrImpalaSummary(solrResults.get(0),impalaResults.get(0));

        //if(solrResults.size()  > 0 && impalaResults.size() > 0)
        //    compareSolrImpalaResults(solrResults,impalaResults, true);
    }

    @SuppressWarnings({"unchecked"})
    private void compareSolrImpalaResults(List<Map<String, Object>> solrResults,
                                          List<Map<String, Object>> impalaResults,
                                          boolean compareTotals) {

        //assertEquals(solrResults.size(), impalaResults.size());

        List<Map<String, Object>> solrResultList = null;
        List<Map<String, Object>> impalaResultList = null;
        long solrTotalCounts = 0;
        long impalaTotalCounts = 0;
        List<Map<String, Object>> solrFacetSummary = null;
        List<Map<String, Object>> impalaFacetSummary = null;
        if(!solrResults.isEmpty()) {
            Map<String, Object> resultMap =  solrResults.get(0);
            solrResultList = (List<Map<String, Object>>)resultMap.get("result");
            solrTotalCounts = (long)resultMap.get("total");
            if(resultMap.containsKey("summary"))
                solrFacetSummary = (List<Map<String, Object>>) resultMap.get("summary");
        }
        if(!impalaResults.isEmpty()) {
            Map resultMap = (Map) impalaResults.get(0);
            impalaResultList = (List<Map<String, Object>>)resultMap.get("result");
            impalaTotalCounts = (long)resultMap.get("total");
            if(resultMap.containsKey("summary"))
                impalaFacetSummary = (List<Map<String, Object>>) resultMap.get("summary");
        }

        assertNotNull(solrResultList);
        assertNotNull(impalaResultList);

        if(solrFacetSummary!=null && impalaFacetSummary!= null)
            assertEquals(solrFacetSummary.size(), impalaFacetSummary.size());

        //ensure that the columnNames are the same
        if(!solrResultList.isEmpty() && !impalaResultList.isEmpty()) {
            Set<String> solrColumnNames = solrResultList.get(0).keySet();
            Map<String, Object> impalaRow = impalaResultList.get(0);
            solrColumnNames.forEach(key -> {
                if (key.equals("_version_") ||
                        key.equals("sourceUserNameAuto") ||
                        key.equals("sourceNameOrIpAuto") ||
                        key.equals("destinationUserNameAuto") ||
                        key.equals("destinationNameOrIpAuto") ||
                        key.equals("destinationUrl") ||
                        key.equals("destinationHostNameAuto"))
                    return;
                collector.checkThat("Impala does not contain key: " + key, true, equalTo(impalaRow.containsKey(key)));
            });
        }
        if(!solrResultList.isEmpty() && !impalaResultList.isEmpty()) {
            Map<String, Map<String, Object>> solrResultsMap = getUUIDMap(solrResultList);
            Map<String, Map<String, Object>> impalaResultsMap = getUUIDMap(impalaResultList);
            assertNotNull(solrResultsMap);
            assertNotNull(impalaResultsMap);
            assertNotEquals(0, solrResultsMap.size());
            assertNotEquals(0, impalaResultsMap.size());
            //using a lambda expression
            solrResultsMap.keySet().forEach(uuid -> {
                // this is the most important comparison. If the UUIDs are same, we knwo that the results are correct!
                Map<String,Object> impalaValueMap =  impalaResultsMap.get(uuid);
                Map<String,Object> solrValueMap =  solrResultsMap.get(uuid);
                collector.checkThat(solrValueMap, notNullValue());
                collector.checkThat("Couldn't find uuid: "+uuid+" in Impala", impalaValueMap, notNullValue());

                if(impalaValueMap == null) return;
                // the impala data returns more fields than the solr data
                //assertEquals(impalaValueMap.size(), solrValueMap.size());

                Date solrDate = (Date)solrValueMap.get("startTimeISO");
                String solrValue = getISODate(solrDate);
                String impalaValue = (String)impalaValueMap.get("startTimeISO");
                collector.checkThat("startTimeISO: Solr value not equal to Impala value", solrValue, equalTo(impalaValue));

                solrValue = (String)solrValueMap.get("cefSignatureId");
                impalaValue = (String)impalaValueMap.get("cefSignatureId");
                assertEquals(solrValue, impalaValue);
                collector.checkThat("cefSignatureId: Solr value not equal to Impala value", solrValue, equalTo(impalaValue));

                solrValue = (String)solrValueMap.get("outputFormat");
                impalaValue = (String)impalaValueMap.get("outputFormat");
                collector.checkThat("outputFormat: Solr value not equal to Impala value", solrValue, equalTo(impalaValue));

            });
            // the below code assumes that the returned data from Solr is in the same order
            // as the data returned from Impala. I don't think that is going to be true
            /*
            for(int i=0; i<solrResultList.size(); i++) {
                Map<String,Object> solrValueMap = solrResultList.get(i);
                Map<String, Object> impalaValueMap = impalaResultList.get(i);
                assertNotNull(solrValueMap);
                assertNotNull(impalaValueMap);
                String solrValue  = (String)solrValueMap.get("uuid");
                String impalaValue = (String)impalaValueMap.get("uuid");
                assertNotNull(solrValue);
                assertNotNull(impalaValue);
                assertEquals(solrValue, impalaValue);

                Date solrDate = (Date)solrValueMap.get("startTimeISO");
                solrValue = getISODate(solrDate);
                impalaValue = (String)impalaValueMap.get("starttimeiso");
                assertEquals(solrValue, impalaValue);

                solrValue = (String)solrValueMap.get("rawLog");
                impalaValue = (String)impalaValueMap.get("rawlog");
                //assertEquals(solrValue, impalaValue);

                solrValue = (String)solrValueMap.get("cefSignatureId");
                impalaValue = (String)impalaValueMap.get("cefsignatureid");
                assertEquals(solrValue, impalaValue);

                solrValue = (String)solrValueMap.get("outputFormat");
                impalaValue = (String)impalaValueMap.get("outputformat");
                assertEquals(solrValue, impalaValue);

            }*/
        }
        collector.checkThat(String.format("Number of results returned by Solr: %d is not equal to  Number of results returned by Impala: %d",solrResultList.size(), impalaResultList.size()), solrResultList.size(), equalTo(impalaResultList.size()));
        if(compareTotals) collector.checkThat(String.format("Total Number of results returned by Solr: %d is not equal to Total Number of results returned by Impala: %d", solrTotalCounts, impalaTotalCounts),solrTotalCounts, equalTo(impalaTotalCounts));
    }

    @SuppressWarnings({"unchecked"})
    private void compareSolrImpalaSummary(Map<String, Object> solrResultsMap,
                                          Map<String, Object> impalaResultsMap) {


        List<Map<String, Object>> solrList = null;
        List<Map<String, Object>> impalaList = null;

        if(solrResultsMap.containsKey("summary"))
            solrList = (List<Map<String, Object>>) solrResultsMap.get("summary");

        if(impalaResultsMap.containsKey("summary"))
            impalaList = (List<Map<String, Object>>) impalaResultsMap.get("summary");

        assertNotNull(solrList);
        assertNotNull(impalaList);
        if(solrList.isEmpty())
            assertTrue("Summary not available in Solr",solrList.isEmpty());
        if(impalaList.isEmpty())
            assertTrue("Summary not available in Impala",impalaList.isEmpty());

        assertEquals(solrList.size(), impalaList.size());
        Map<String, Object> solrSummaryMap = getSummaryMap(solrList);
        Map<String, Object> impalaSummaryMap = getSummaryMap(impalaList);

        assertNotNull(solrSummaryMap);
        assertNotNull(impalaSummaryMap);

        collector.checkThat(String.format("Solr Summary contains %d fields whereas Impala summary contains %d fields",solrSummaryMap.size(),impalaSummaryMap.size()), solrSummaryMap.size(), equalTo(impalaSummaryMap.size()));

        for(String key: solrSummaryMap.keySet()){
            Object solrVal = solrSummaryMap.get(key);
            Object impalaVal = impalaSummaryMap.get(key);
            assertEquals(solrVal.getClass(), impalaVal.getClass());
            if(solrVal instanceof List && impalaVal instanceof List){
                List<Map<String, String>> sl = ( List<Map<String, String>> ) solrVal;
                List<Map<String, String>> il = ( List<Map<String, String>> ) impalaVal;
                collector.checkThat(sl.size(), equalTo(il.size()));
                for(int i=0; i<sl.size();i++){
                    Map<String, String> sm = sl.get(i);
                    String sfn = sm.get("fieldName");
                    String sfv = sm.get("value");
                    boolean found = false;
                    for(int j=0;j<il.size();j++){
                        Map<String, String> im = il.get(j);
                        String ifn = im.get("fieldName");
                        String ifv = im.get("value");
                        if(sfn.equals(ifn) && sfv.equals(ifv)){
                            found = true;
                            for(String sk: sm.keySet()){
                                Object sv = sm.get(sk);
                                String iv = im.get(sk);
                                if(sv.getClass().equals(Double.class)) {
                                    collector.checkThat(String.format("Solr val not equal to Impala val for key %s and property %s", key, sk), sv, equalTo(Double.valueOf(iv)));
                                }else{
                                    collector.checkThat(String.format("Solr val not equal to Impala val for key %s and property %s", key, sk), sv, equalTo(iv));
                                }
                            }
                        }
                    }
                    collector.checkThat(String.format("Solr key: %s field: %s and value: %s not found in Impala", key, sfn, sfv), found, is(true));

                }
            }
        }
    }
    private Map<String, Map<String, Object>> getUUIDMap(List<Map<String, Object>> list){
        Map<String, Map<String, Object>> ret = new HashMap<>();
        list.forEach(map -> {
            String uuid = (String)map.get("uuid");
            ret.put(uuid, map);
        });
        return ret;
    }
    private String getISODate(Date date) {
        String formattedDate = "";
        if(date != null){
            DateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                formattedDate = isoFormatter.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return formattedDate;
    }
    private Map<String, Object> getSummaryMap(List<Map<String, Object>> summaryList) {
        Map<String, Object> ret = new HashMap<>();
        summaryList.forEach(map -> {
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            for(Map.Entry<String, Object> entry: entrySet) {
                ret.put(entry.getKey(), entry.getValue());
            }
        });
        return ret;
    }
    //@Test
    /*
    public void testParsing() throws Exception{
        String[] header = "requestclientapplication,requestclientapplicationcount,sourceusername,sourceusernamecount,responsecontenttype,responsecontenttypecount,requestscheme,requestschemecount,requestmethod,requestmethodcount,devicepolicyaction,devicepolicyactioncount,sourcenameorip,sourcenameoripcount,cefsignatureid,cefsignatureidcount,destinationnameorip,destinationnameoripcount".split(",");
        String csv =
                "jupdate,1,NULL,0,NULL,0,NULL,0,CONNECT,1,TCP_MISS,1,192.168.12.18,1,200,1,javadl-esd-secure.oracle.com,1\n" +
                "Microsoft-CryptoAPI/6.1,1,NULL,0,NULL,0,http,1,GET,1,TCP_MISS,1,192.168.12.18,1,200,1,g2.symcb.com,1\n" +
                "Microsoft-CryptoAPI/6.1,1,NULL,0,NULL,0,http,1,GET,1,TCP_MISS,1,192.168.12.18,1,200,1,gn.symcd.com,1\n" +
                "Microsoft-CryptoAPI/6.1,2,NULL,0,NULL,0,http,2,GET,2,TCP_MISS,2,192.168.12.18,2,304,2,ctldl.windowsupdate.com,2";

        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<String> facetList = Lists.newLinkedList(cache.getWebSummaryFieldMap().keySet());
        List<Map<String, String>> rows = parseCsv(header, csv);
        Map<String,  List<Map<String, String>>> map = computeSummary(2, cache, rows, facetList);
        System.out.print(map.toString());
    }
    private static List<Map<String,String>> parseCsv(String[] header, String csv) throws Exception{
        List<Map<String, String>> results = new LinkedList<>();
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(new StringReader(csv));
        for(CSVRecord record : records){
            Map<String, String> row = new HashMap<>();
            for(String fieldName: header){
                row.put(fieldName, record.get(fieldName));
            }
            results.add(row);
        }
        return results;
    }
    private static Map<String,  List<Map<String, String>>> computeSummary(int modelId, FeatureServiceCache cache,
                                                                          List<Map<String, String>> rows, List<String> facetList){
        Map<String, Map<String, Integer>> facetMap = new HashMap<>();
        Map<String, Integer> facetTotal = new HashMap<>();
        for(Map<String, String> row: rows){
            for(String facet: facetList){
                //count name is lower case here
                String columnName = facet.toLowerCase();
                String columnCountName = facet.toLowerCase()+"count";
                String columnValue = row.get(columnName);
                String columnCount = row.get(columnCountName);
                if(columnValue!=null && !columnCount.equals("0")) {
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
        Map<String,  List<Map<String, String>>> retMap = new HashMap<>();
        for(String facet : facetMap.keySet()){
            String localizedName = SearchUtils.getLocalizedFacetName(facet, modelId, cache);
            List<Map<String, String>> list;
            if(retMap.containsKey(localizedName)){
                list = retMap.get(localizedName);
            }else{
                list = new LinkedList<>();
                retMap.put(localizedName, list);
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
        return retMap;
    }
    */
}
