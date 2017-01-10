package com.e8.palam.dao;

import com.e8.palam.TestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
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
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.util.*;

import static org.junit.Assert.assertNotNull;


public class RawLogsOnlinePerfTest extends TestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RawLogsOnlinePerfTest.class);
    public RawLogsOnlinePerfTest() { super();}

    Map<String, Map<String, Long>> data = new HashMap<>();

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
    @After
    public void teardown() throws Exception{
        StringBuilder builder = new StringBuilder();
        builder.append("\"Function Name\",\"Iterations\",\"Solr Time (ms)\",\"Solr Average Time  (ms)\",\"Impala Time (ms)\",\"Impala Average Time (ms)\"");

        for(String name: data.keySet()){
            builder.append("\n");
            Map<String, Long> time = data.get(name);
            builder.append("\"").append(name).append("\"");
            for(String measure : time.keySet()){
                builder.append(",");
                builder.append(time.get(measure));
            }
        }
        System.out.print(builder.toString());
    }
    public interface PerfFunc{
        void run();
    }
    public static long calcTime(PerfFunc func){
        // prime the results
        func.run();

        // now test
        long startTime = System.nanoTime();
        func.run();
        long stopTime = System.nanoTime();
        return  ((stopTime-startTime)/(1000*1000));
    }
    public static long calcAvgTime(PerfFunc func, long iterations){
        long totalTime = 0;
        // prime the query
        func.run();
        // now test
        for(int i=0;i<iterations;i++) {
            long startTime = System.nanoTime();
            func.run();
            long stopTime = System.nanoTime();
            totalTime += (stopTime-startTime);
            LOGGER.debug("Completed iteration #"+i);
        }
        return ((totalTime/iterations)/(1000*1000));
    }
    public void calcPerf(PerfFunc solrFunc, PerfFunc impalaFunc, String methodName){

        long iterations = 10;
        long solrTime = calcTime(solrFunc);
        long solrAvgTime = calcAvgTime(solrFunc, iterations);
        long impalaTime = calcTime(impalaFunc);
        long impalaAvgTime = calcAvgTime(impalaFunc, iterations);
        Map<String, Long> measure = new HashMap<>();
        measure.put("iterations", iterations);
        measure.put("solrTime", solrTime);
        measure.put("solrAvgTime", solrAvgTime);
        measure.put("impalaTime", impalaTime);
        measure.put("impalaAvgTime", impalaAvgTime);
        data.put(methodName,measure);
    }

    @Test
    public void getSearchResultsForSourceDestinationTestWithFacets() throws Exception{
        getSearchResultsForSourceDestinationTest(Thread.currentThread().getStackTrace()[1].getMethodName(), true);
    }
    @Test
    public void getSearchResultsForSourceDestinationTestWithoutFacets() throws Exception{
        getSearchResultsForSourceDestinationTest(Thread.currentThread().getStackTrace()[1].getMethodName(), false);
    }
    public void getSearchResultsForSourceDestinationTest(String methodName, boolean summarize) throws Exception {

        int modelId = Constants.WebPeerAnomaliesModelTuple()._1();
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        List<String> sources = new ArrayList<>();
        int summaryFacetLimit = 5;
        int endRows=50;
        int pageNo=1;
        String sortField ="startTimeISO";
        String sortOrder = "DESC";
        //boolean summarize = false;
        int startRecord = endRows * (pageNo - 1);

        sources.add("192.168.12.30");

        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<String> destinations = new ArrayList<>();
        destinations.add("r20swj13mr.microsoft.com");
        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; rv:11.0) like Gecko"};
        queryParams.put("requestClientApplication", Arrays.asList(paramList));

        // Lambda Expressions
        PerfFunc impalaFunc = () -> {
            List<Map<String, Object>> impalaResults = impalaDao.getSearchResultsForSourceDestination(modelId,  sources,
                    destinations, queryParams, startTime, endTime, summaryFacetLimit, endRows, pageNo,
                    sortField, sortOrder, summarize, cache);
            assertNotNull(impalaResults);
        };
        PerfFunc solrFunc = () -> {
            List<Map<String, Object>> solrResults = solrDao.getSearchResultsForSourceDestination(modelId,  sources,
                    destinations, queryParams, startTime, endTime, summaryFacetLimit, endRows, pageNo,
                    sortField, sortOrder, summarize, cache);
            assertNotNull(solrResults);

        };
        calcPerf(solrFunc, impalaFunc, methodName);
    }

    @Test
    public void getFacetedSearchResultsTestWithFacets() throws Exception{
        getFacetedSearchResultsTest(Thread.currentThread().getStackTrace()[1].getMethodName(), true);
    }
    @Test
    public void getFacetedSearchResultsTestWithoutFacets() throws Exception{
        getFacetedSearchResultsTest(Thread.currentThread().getStackTrace()[1].getMethodName(), false);
    }
    public void getFacetedSearchResultsTest(String methodName, boolean summarize) throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 3;
        int securityEventId =22;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        String[] selectedEntities = new String[]{"-jyria"};
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows=50;
        int pageNo=1;
        String sortField ="startTimeISO";
        String sortOrder = "DESC";
        //boolean summarize = false;
        //int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"E8SEC"};
        queryParams.put("destinationNtDomain", Arrays.asList(paramList));


        PerfFunc solrFunc = () -> {
            List<Map<String, Object>> solrResults = solrDao.getFacetedSearchResults(modelId,  securityEventId,
                    queryParams, selectedEntities, new String[]{},startTime,
                    endTime, summaryFacetLimit, endRows, pageNo, sortField, sortOrder, summarize, cache);
            assertNotNull(solrResults);
        };
        PerfFunc impalaFunc = () -> {
            List<Map<String, Object>> impalaResults = impalaDao.getFacetedSearchResults(modelId,  securityEventId,
                    queryParams, selectedEntities, new String[]{},startTime,
                    endTime, summaryFacetLimit, endRows, pageNo, sortField, sortOrder, summarize, cache);
            assertNotNull(impalaResults);
        };
        calcPerf(solrFunc, impalaFunc, methodName);

        //compareSolrImpalaResults(solrResults, impalaResults);
    }
    @Test
    public void getWebAnomalyFacetedSearchResultsTestWithFacets() throws Exception{
        getWebAnomalyFacetedSearchResultsTest( Thread.currentThread().getStackTrace()[1].getMethodName(), true);
    }
    @Test
    public void getWebAnomalyFacetedSearchResultsTestWithoutFacets() throws Exception{
        getWebAnomalyFacetedSearchResultsTest( Thread.currentThread().getStackTrace()[1].getMethodName(), false);
    }
    public void getWebAnomalyFacetedSearchResultsTest(String methodName, boolean summarize) throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 6;
        String source = "w2k8r2-ad$";
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows=20;
        int pageNo=1;
        String sortField ="startTimeISO";
        String sortOrder = "DESC";
        //boolean summarize = false;
        int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"192.168.12.18"};
        queryParams.put("sourceAddress", Arrays.asList(paramList));

        PerfFunc solrFunc = () -> {
            List<Map<String, Object>> solrResults = new WebAnomalyProfileSolrDao().getFacetedSearchResults(webProxySolrClient, iamSolrClient,
                    modelId, queryParams, source, startTime, endTime, endRows,
                    pageNo, summaryFacetLimit, sortField,
                    sortOrder, summarize, cache);
            assertNotNull(solrResults);
        };
        PerfFunc impalaFunc = () -> {
            List<Map<String, Object>> impalaResults = impalaDao.getWebAnomalyFacetedSearchResults(modelId, queryParams,
                    source, startTime, endTime, endRows, pageNo, summaryFacetLimit, sortField,
                    sortOrder, summarize, cache);
            assertNotNull(impalaResults);
        };
        calcPerf(solrFunc, impalaFunc, methodName);
        //compareSolrImpalaResults(solrResults, impalaResults);
    }

    @Test
    public void getTimeSeriesResultsForHttpTestWithFacets() throws Exception{
        getTimeSeriesResultsForHttpTest( Thread.currentThread().getStackTrace()[1].getMethodName(), true);
    }
    @Test
    public void getTimeSeriesResultsForHttpTestWithoutFacets() throws Exception{
        getTimeSeriesResultsForHttpTest( Thread.currentThread().getStackTrace()[1].getMethodName(), false);
    }
    public void getTimeSeriesResultsForHttpTest(String methodName, boolean summarize) throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(2);
        input.setStartTime("2016-10-17T00:00:00.000Z");
        input.setEndTime("2016-10-18T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("startTimeISO");
        input.setSortOrder("DESC");
        input.setSummarize(summarize);
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

        PerfFunc solrFunc = () -> {
            List<Map<String, Object>> solrResults = (List<Map<String,Object>>)new TimeSeriesDao(configuration).getTimeSeriesSearchResults(webProxySolrClient,
                    iamSolrClient, null, null, null, input, cache);
            assertNotNull(solrResults);
        };
        PerfFunc impalaFunc = () -> {
            List<Map<String, Object>> impalaResults = impalaDao.getTimeSeriesSearchResults(input, cache);
            assertNotNull(impalaResults);
        };
        calcPerf(solrFunc, impalaFunc, methodName);
    }

    @Test
    public void getTimeSeriesresultsForAdTestWithFacets() throws Exception{
        getTimeSeriesresultsForAdTest( Thread.currentThread().getStackTrace()[1].getMethodName(), true);
    }
    @Test
    public void getTimeSeriesresultsForAdTestWithoutFacets() throws Exception{
        getTimeSeriesresultsForAdTest( Thread.currentThread().getStackTrace()[1].getMethodName(), false);
    }
    public void getTimeSeriesresultsForAdTest(String methodName, boolean summarize) throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(3);
        input.setStartTime("2016-10-17T00:00:00.000Z");
        input.setEndTime("2016-10-18T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("startTimeISO");
        input.setSortOrder("DESC");
        input.setSummarize(summarize);
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

        PerfFunc solrFunc = () -> {
            List<Map<String, Object>> solrResults = (List<Map<String,Object>>)new TimeSeriesDao(configuration).getTimeSeriesSearchResults(webProxySolrClient,
                    iamSolrClient, null, null, null, input, cache);
            assertNotNull(solrResults);
        };
        PerfFunc impalaFunc = () -> {
            List<Map<String, Object>> impalaResults = impalaDao.getTimeSeriesSearchResults(input, cache);
            assertNotNull(impalaResults);
        };
        calcPerf(solrFunc, impalaFunc, methodName);

        //compareSolrImpalaResults(solrResults, impalaResults);
    }

    @Test
    public void getAutoCompleteRecordsTest() throws Exception{

        int modelId = 2;
        String fieldType = "destination";
        String incomingString = "r";
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        int startRecord = 0;
        int pageSize = 10;

        PerfFunc solrFunc = () -> {
            Set<String> solrResults = new TimeSeriesDao(configuration).getAutoCompleteRecords(webProxySolrClient, iamSolrClient,
                    null, null, null, modelId, fieldType, incomingString,
                    startTime, endTime, 1, pageSize, configuration);
            assertNotNull(solrResults);
        };
        PerfFunc impalaFunc = () -> {
            Set<String> impalaResults = impalaDao.getAutoCompleteRecords(modelId, fieldType, incomingString,
                    startTime, endTime, startRecord, pageSize);
            assertNotNull(impalaResults);
        };
        calcPerf(solrFunc, impalaFunc, "getAutoCompleteRecords");
    }
}
