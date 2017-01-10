package com.e8.palam.dao;

import com.e8.palam.TestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.common.inputs.TimeSeriesInput;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.SearchImpalaDao;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.ImpalaUtils;
import com.securityx.modelfeature.utils.PeerGroupFacetHelper;
import com.securityx.modelfeature.utils.SearchUtils;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class SearchImpalaDaoOfflineTest extends TestBase {

    public SearchImpalaDaoOfflineTest() {
        super();
    }

    //private SearchDao searchDao = null;

    Map<String, List<String>> emptyQueryParams = new HashMap<>();

    @Before
    public void setup() throws Exception {
        super.setup();
        String confFile = System.getProperty("user.dir")+"/src/main/config/dev_cfg.yml";
        configuration = new ConfigurationFactory<>(FeatureServiceConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));

    }

    private static FeatureServiceConfiguration configuration = new FeatureServiceConfiguration();
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    //////////////////////////////////////////////////////////////////////////////////
    //                  Tests for model 2                                           //
    //////////////////////////////////////////////////////////////////////////////////

    /*@Test
    public void model2SecEvent0Test() throws Exception{
        int modelId = 2;
        int securityEventId = 0;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        String startTime = "2016-09-30T12:12:12.123Z";
        String endTime = "2016-10-30T12:12:12.123Z";
        List<String> sources = new ArrayList<>();
        int summaryFacetLimit = 1;
        int endRows=10;
        int pageNo=1;
        String sortField ="RISK_SCORE";
        String sortOrder = "DESC";
        boolean summarize = true;
        int startRecord = endRows * (pageNo - 1);
        String confFile = System.getProperty("user.dir")+"/src/main/config/test_cfg.yml";

        sources.add("192.168.1.64");
        configuration = new ConfigurationFactory<FeatureServiceConfiguration>(FeatureServiceConfiguration.class,
                validator,
                mapper, "dw")
                .build(new File(confFile));
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<String> destinations = new ArrayList<>();
        Map<String, List<String>>  queryParams = new HashMap<>();
        //String queryString = SearchImpalaDao.populateSourceDestinationQueryParams(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        String queryString = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,
                                SearchImpalaDao.populateSourceDestinationQueryParams(startTime, endTime, sources, destinations, queryParams));
        assertEquals("SELECT *  FROM e8sec.WEB_PROXY_PARQUET where STARTTIMEISO BETWEEN '2016-09-30T12:12:12.123Z' AND '2016-10-30T12:12:12.123Z' AND sourceNameOrIp IN ('192.168.1.64') ORDER BY  RISK_SCORE DESC LIMIT 10 OFFSET 0", queryString);
    }*/

    @Test
    public void getSearchResultsForSourceDestinationTest() throws Exception {

        int modelId = Constants.WebPeerAnomaliesModelTuple()._1();
        String startTime = "2016-08-01T00:00:00.000Z";
        String endTime = "2016-09-24T00:00:00.000Z";
        List<String> sources = new ArrayList<>();
        int summaryFacetLimit = 5;
        int endRows = 50;
        int pageNo = 1;
        String sortField = "STARTTIMEISO";
        String sortOrder = "DESC";
        //boolean summarize = true;
        int startRecord = endRows * (pageNo - 1);

        sources.add("192.168.12.30");

        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<String> destinations = new ArrayList<>();
        destinations.add("r20swj13mr.microsoft.com");
        Map<String, List<String>> queryParams = new HashMap<>();

        //String queryString = SearchImpalaDao.populateSourceDestinationQueryParams(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        String queryStringWithoutQueryParams = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,
                SearchImpalaDao.populateSourceDestinationQueryParams(startTime, endTime, sources, destinations, queryParams),configuration);
        assertEquals("SELECT * FROM E8SEC.WEB_PROXY_PARQUET  WHERE STARTTIMEISO >= " +
                "'2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z' AND " +
                "sourceNameOrIp IN ('192.168.12.30') AND destinationNameOrIp IN " +
                "('r20swj13mr.microsoft.com') ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithoutQueryParams);

        String[] paramList = new String[]{"Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; rv:11.0) like Gecko"};
        queryParams.put("requestClientApplication", Arrays.asList(paramList));
        //summarize = false;
        String queryStringWithQueryParams = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,
                SearchImpalaDao.populateSourceDestinationQueryParams(startTime, endTime, sources, destinations, queryParams),configuration);
        assertEquals("SELECT * FROM E8SEC.WEB_PROXY_PARQUET  WHERE STARTTIMEISO >= " +
                "'2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z' AND " +
                "sourceNameOrIp IN ('192.168.12.30') AND destinationNameOrIp IN " +
                "('r20swj13mr.microsoft.com') AND requestClientApplication IN " +
                "('Mozilla\\/5.0\\ \\(Windows\\ NT\\ 6.1\\;\\ Win64\\;\\ x64\\;\\ Trident\\/7.0\\;\\ " +
                "rv\\:11.0\\)\\ like\\ Gecko') ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithQueryParams);

    }

    @Test
    public void getFacetedSearchResultsTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 3;
        int securityEventId = 22;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        String[] selectedEntities = new String[]{"-jyria"};
        String startTime = "2016-08-01T00:00:00.000Z";
        String endTime = "2016-09-24T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows = 50;
        int pageNo = 1;
        String sortField = "STARTTIMEISO";
        String sortOrder = "DESC";

        int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>> queryParams = new HashMap<>();

        //String queryString = SearchImpalaDao.populateSourceDestinationQueryParams(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        String queryStringWithoutQueryParams = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,
                SearchImpalaDao.populateFacetSearchQueryParams(startTime, endTime, modelId, securityEventId, queryParams, selectedEntities, cache),configuration);
        assertEquals("SELECT * FROM E8SEC.IAM_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO <'2016-09-24T00:00:00.000Z'  AND cefSignatureId LIKE '%Security_4672%' AND destinationUserName IN  ('jyria' )  ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithoutQueryParams);


        String[] paramList = new String[]{"E8SEC"};
        queryParams.put("destinationNtDomain", Arrays.asList(paramList));
        String queryStringWithQueryParams = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,
                SearchImpalaDao.populateFacetSearchQueryParams(startTime, endTime, modelId, securityEventId, queryParams, selectedEntities, cache),configuration);
        assertEquals("SELECT * FROM E8SEC.IAM_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO <'2016-09-24T00:00:00.000Z'  AND cefSignatureId LIKE '%Security_4672%' AND destinationUserName IN  ('jyria' )  AND destinationNtDomain IN ('E8SEC') ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithQueryParams);
    }
    @Test
    public void getTimeSeriesSearchQueryForHttpSeriesTest_E82655() throws Exception {
        // curl 'http://localhost:9080/service/timeseries/search' -H 'Content-Type: application/json' -H 'Accept: application/json'
        // --data-binary '{"startTime":"2016-10-16T00:00:00.000Z","endTime":"2016-10-17T00:00:00.000Z","numRows":50,"pageNo":1,"summarize":true,"facets":[{"typeField":"all","group":"all","groupId":"all"}],"modelId":2,"queryParams":{},"sortField":"startTimeISO","sortOrder":"desc"}' --compressed --insecure
        String json = "{\"startTime\":\"2016-10-16T00:00:00.000Z\",\"endTime\":\"2016-10-17T00:00:00.000Z\",\"numRows\":50,\"pageNo\":1,\"summarize\":true,\"facets\":[{\"typeField\":\"all\",\"group\":\"all\",\"groupId\":\"all\"}],\"modelId\":2,\"queryParams\":{},\"sortField\":\"startTimeISO\",\"sortOrder\":\"desc\"}";
        ObjectMapper mapper = Jackson.newObjectMapper();
        TimeSeriesInput tsi = mapper.readValue(json, TimeSeriesInput.class);
        String actual = SearchImpalaDao.getTimeSeriesSearchQueryForHttpSeries(tsi, getCache());
        String expected = " WHERE STARTTIMEISO >= '2016-10-16T00:00:00.000Z' AND STARTTIMEISO < '2016-10-17T00:00:00.000Z' ";
        assertEquals(expected,actual);

        json = "{\"startTime\":\"2016-10-16T00:00:00.000Z\",\"endTime\":\"2016-10-17T00:00:00.000Z\",\"numRows\":50,\"pageNo\":1,\"summarize\":true,\"facets\":[{\"typeField\":\"responseContentType\",\"group\":\"all\",\"groupId\":\"XML\"}],\"modelId\":2,\"queryParams\":{},\"sortField\":\"startTimeISO\",\"sortOrder\":\"desc\"}";
        tsi = mapper.readValue(json, TimeSeriesInput.class);
        actual = SearchImpalaDao.getTimeSeriesSearchQueryForHttpSeries(tsi, getCache());
        expected = " WHERE STARTTIMEISO >= '2016-10-16T00:00:00.000Z' AND STARTTIMEISO < '2016-10-17T00:00:00.000Z'  AND responseContentType LIKE  '%XML%' ";
        assertEquals(expected,actual);

        json = "{\"startTime\":\"2016-10-16T00:00:00.000Z\",\"endTime\":\"2016-10-17T00:00:00.000Z\",\"numRows\":50,\"pageNo\":1,\"summarize\":true,\"facets\":[{\"typeField\":\"topSource\",\"group\":\"all\",\"groupId\":\"google.com\"}],\"modelId\":2,\"queryParams\":{},\"sortField\":\"startTimeISO\",\"sortOrder\":\"desc\"}";
        tsi = mapper.readValue(json, TimeSeriesInput.class);
        actual = SearchImpalaDao.getTimeSeriesSearchQueryForHttpSeries(tsi, getCache());
        expected = " WHERE STARTTIMEISO >= '2016-10-16T00:00:00.000Z' AND STARTTIMEISO < '2016-10-17T00:00:00.000Z'  AND sourceNameOrIp LIKE '%google.com%' ";
        assertEquals(expected,actual);
    }

    @Test
    public void getWebAnomalyFacetedSearchResultsTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 6;
        String source = "w2k8r2-ad$";
        String startTime = "2016-08-01T00:00:00.000Z";
        String endTime = "2016-09-24T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows = 50;
        int pageNo = 1;
        String sortField = "STARTTIMEISO";
        String sortOrder = "DESC";
        //boolean summarize = true;
        int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>> queryParams = new HashMap<>();

        //String queryString = SearchImpalaDao.populateSourceDestinationQueryParams(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        String queryStringWithoutQueryParams = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,
                SearchImpalaDao.populateWebAnomalyQueryParams(startTime, endTime, modelId, queryParams, source),configuration);
        assertEquals("SELECT * FROM E8SEC.IAM_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND (( sourceUserName=\"w2k8r2-ad$\" )  OR ( destinationUserName=\"w2k8r2-ad$\" ) ) ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithoutQueryParams);

        String[] paramList = new String[]{"192.168.12.18"};
        queryParams.put("sourceAddress", Arrays.asList(paramList));
        //summarize = false;
        String queryStringWithQueryParams = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder,

                SearchImpalaDao.populateWebAnomalyQueryParams(startTime, endTime, modelId, queryParams, source),configuration);
        assertEquals("SELECT * FROM E8SEC.IAM_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceAddress IN ('192.168.12.18') AND (( sourceUserName=\"w2k8r2-ad$\" )  OR ( destinationUserName=\"w2k8r2-ad$\" ) ) ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithQueryParams);
    }

    @Test
    public void getTimeSeriesResultsForHttpTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(2);
        input.setStartTime("2016-08-01T00:00:00.000Z");
        input.setEndTime("2016-09-24T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("STARTTIMEISO");
        input.setSortOrder("DESC");
        //input.setSummarize(true);
        int startRecord = input.getNumRows() * (input.getPageNo() - 1);

        List<Map<String, String>> facetList = new LinkedList<>();
        Map<String, String> facetMap = new HashMap<>();
        facetMap.put("typeField", "cefSignatureId");
        facetMap.put("group", "2xx OK");
        facetMap.put("groupId", "200");
        //"facets":[{"typeField":"cefSignatureId","group":"2xx OK","groupId":"200"},{"typeField":"cefSignatureId","group":"2xx OK","groupId":null},{"typeField":"topSource","groupId":"10.10.7.2"},{"typeField":"topDestination","groupId":"10.10.30.224"}]
        facetList.add(facetMap);

        input.setFacets(facetList);
        Map<String, List<String>> queryParams = new HashMap<>();

        //String queryString = SearchImpalaDao.populateSourceDestinationQueryParams(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        String queryStringWithoutQueryParams = ImpalaUtils.getImpalaQuery(input.getModelId(), startRecord, input.getNumRows(), input.getSortField(), input.getSortOrder(),
                SearchImpalaDao.getTimeSeriesSearchQueryForHttpSeries(input,cache),configuration);

        assertEquals("SELECT * FROM E8SEC.WEB_PROXY_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND cefSignatureId = \"200\"  ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithoutQueryParams);


        String[] paramList = new String[]{"10.10.7.2"};
        queryParams.put("sourceNameOrIp", Arrays.asList(paramList));
        input.setQueryParams(queryParams);
        //input.setSummarize(false);
        String queryStringWithQueryParams = ImpalaUtils.getImpalaQuery(input.getModelId(), startRecord, input.getNumRows(), input.getSortField(), input.getSortOrder(),
                SearchImpalaDao.getTimeSeriesSearchQueryForHttpSeries(input,cache),configuration);
        assertEquals("SELECT * FROM E8SEC.WEB_PROXY_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNameOrIp IN ('10.10.7.2') AND cefSignatureId = \"200\"  ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithQueryParams);

    }

    @Test
    public void getTimeSeriesresultsForAdTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(3);
        input.setStartTime("2016-08-01T00:00:00.000Z");
        input.setEndTime("2016-09-24T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("STARTTIMEISO");
        input.setSortOrder("DESC");
        //input.setSummarize(true);
        int startRecord = input.getNumRows() * (input.getPageNo() - 1);

        List<Map<String, String>> facetList = new LinkedList<>();
        Map<String, String> facetMap = new HashMap<>();
        facetMap.put("typeField", "event");
        facetMap.put("group", "Logon/Logoff");
        facetMap.put("groupId", "4624");
        //"facets":[{"typeField":"cefSignatureId","group":"2xx OK","groupId":"200"},{"typeField":"cefSignatureId","group":"2xx OK","groupId":null},{"typeField":"topSource","groupId":"10.10.7.2"},{"typeField":"topDestination","groupId":"10.10.30.224"}]
        facetList.add(facetMap);

        input.setFacets(facetList);
        Map<String, List<String>> queryParams = new HashMap<>();

        String queryStringWithoutQueryParams = ImpalaUtils.getImpalaQuery(input.getModelId(), startRecord, input.getNumRows(), input.getSortField(), input.getSortOrder(),

                SearchImpalaDao.getTimeSeriesSearchQueryForAd(input,cache),configuration);

        assertEquals("SELECT * FROM E8SEC.IAM_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND ( cefSignatureId LIKE '%4624%' ) ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithoutQueryParams);


        String[] paramList = new String[]{"E8SEC"};
        queryParams.put("sourceNtDomain", Arrays.asList(paramList));
        input.setQueryParams(queryParams);

        String queryStringWithQueryParams = ImpalaUtils.getImpalaQuery(input.getModelId(), startRecord, input.getNumRows(), input.getSortField(), input.getSortOrder(),
        SearchImpalaDao.getTimeSeriesSearchQueryForAd(input,cache),configuration);
        assertEquals("SELECT * FROM E8SEC.IAM_PARQUET  WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0", queryStringWithQueryParams);
    }

    @Test
    public void getAutoCompleteRecordsTest() throws Exception {

        int modelId = 2;
        String fieldType = "destination";
        String incomingString = "10";
        String startTime = "2016-08-01T00:00:00.000Z";
        String endTime = "2016-09-24T00:00:00.000Z";
        int startRecord = 0;
        int pageSize = 10;

        String fieldName = SearchUtils.getSrcDestFieldName(modelId, fieldType);
        String tableName = ImpalaUtils.getImpalaTableName(modelId, configuration);

        String sql = ImpalaUtils.getAutoCompleteQuery(fieldName, tableName, startRecord, pageSize, fieldName, "ASC",
                                                        incomingString, startTime, endTime);
        assertEquals("SELECT DISTINCT(destinationNameOrIp) FROM E8SEC.WEB_PROXY_PARQUET WHERE destinationNameOrIp RLIKE '^10.*' AND  STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z' ORDER BY destinationNameOrIp ASC LIMIT 10 OFFSET 0", sql);
    }

    @Test
    public void testGetFacetQuery_v1(){
        int modelId = 3;
        List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, getCache());
        String whereClause = "WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' )";
        String actual = ImpalaUtils.getFacetQuery_v1(ImpalaUtils.getImpalaTableName(modelId, configuration),facetList, whereClause,5);
        String expected = "SELECT destinationDnsDomain, COUNT( destinationDnsDomain) as destinationDnsDomainCount, sourceNtDomain, COUNT( sourceNtDomain) as sourceNtDomainCount, sourceAddress, COUNT( sourceAddress) as sourceAddressCount, sourceUserName, COUNT( sourceUserName) as sourceUserNameCount, destinationServiceName, COUNT( destinationServiceName) as destinationServiceNameCount, sourceProcessName, COUNT( sourceProcessName) as sourceProcessNameCount, cefSignatureId, COUNT( cefSignatureId) as cefSignatureIdCount, destinationNameOrIp, COUNT( destinationNameOrIp) as destinationNameOrIpCount, subStatus, COUNT( subStatus) as subStatusCount, destinationUserName, COUNT( destinationUserName) as destinationUserNameCount, sourceNameOrIp, COUNT( sourceNameOrIp) as sourceNameOrIpCount, destinationNtDomain, COUNT( destinationNtDomain) as destinationNtDomainCount, status, COUNT( status) as statusCount FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY destinationDnsDomain, sourceNtDomain, sourceAddress, sourceUserName, destinationServiceName, sourceProcessName, cefSignatureId, destinationNameOrIp, subStatus, destinationUserName, sourceNameOrIp, destinationNtDomain, status ORDER BY destinationDnsDomainCount, sourceNtDomainCount, sourceAddressCount, sourceUserNameCount, destinationServiceNameCount, sourceProcessNameCount, cefSignatureIdCount, destinationNameOrIpCount, subStatusCount, destinationUserNameCount, sourceNameOrIpCount, destinationNtDomainCount, statusCount";
        assertEquals(expected, actual);
    }
    @Test
    public void testGetFacetQuery_v2(){
        int modelId = 3;
        List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, getCache());
        String whereClause = "WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' )";
        String actual = ImpalaUtils.getFacetQuery_v2(ImpalaUtils.getImpalaTableName(modelId, configuration),facetList, whereClause,5);
        String expected = "SELECT \"destinationDnsDomain\" as facet, destinationDnsDomain as facet_value, COUNT(destinationDnsDomain) as facet_count, SUM(COUNT(destinationDnsDomain)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY destinationDnsDomain ORDER BY destinationDnsDomain LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"sourceNtDomain\" as facet, sourceNtDomain as facet_value, COUNT(sourceNtDomain) as facet_count, SUM(COUNT(sourceNtDomain)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY sourceNtDomain ORDER BY sourceNtDomain LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"sourceAddress\" as facet, sourceAddress as facet_value, COUNT(sourceAddress) as facet_count, SUM(COUNT(sourceAddress)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY sourceAddress ORDER BY sourceAddress LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"sourceUserName\" as facet, sourceUserName as facet_value, COUNT(sourceUserName) as facet_count, SUM(COUNT(sourceUserName)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY sourceUserName ORDER BY sourceUserName LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"destinationServiceName\" as facet, destinationServiceName as facet_value, COUNT(destinationServiceName) as facet_count, SUM(COUNT(destinationServiceName)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY destinationServiceName ORDER BY destinationServiceName LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"sourceProcessName\" as facet, sourceProcessName as facet_value, COUNT(sourceProcessName) as facet_count, SUM(COUNT(sourceProcessName)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY sourceProcessName ORDER BY sourceProcessName LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"cefSignatureId\" as facet, cefSignatureId as facet_value, COUNT(cefSignatureId) as facet_count, SUM(COUNT(cefSignatureId)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY cefSignatureId ORDER BY cefSignatureId LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"destinationNameOrIp\" as facet, destinationNameOrIp as facet_value, COUNT(destinationNameOrIp) as facet_count, SUM(COUNT(destinationNameOrIp)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY destinationNameOrIp ORDER BY destinationNameOrIp LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"subStatus\" as facet, subStatus as facet_value, COUNT(subStatus) as facet_count, SUM(COUNT(subStatus)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY subStatus ORDER BY subStatus LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"destinationUserName\" as facet, destinationUserName as facet_value, COUNT(destinationUserName) as facet_count, SUM(COUNT(destinationUserName)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY destinationUserName ORDER BY destinationUserName LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"sourceNameOrIp\" as facet, sourceNameOrIp as facet_value, COUNT(sourceNameOrIp) as facet_count, SUM(COUNT(sourceNameOrIp)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY sourceNameOrIp ORDER BY sourceNameOrIp LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"destinationNtDomain\" as facet, destinationNtDomain as facet_value, COUNT(destinationNtDomain) as facet_count, SUM(COUNT(destinationNtDomain)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY destinationNtDomain ORDER BY destinationNtDomain LIMIT 5\n" +
                " UNION ALL \n" +
                "SELECT \"status\" as facet, status as facet_value, COUNT(status) as facet_count, SUM(COUNT(status)) OVER() AS total_count FROM E8SEC.IAM_PARQUET WHERE STARTTIMEISO >= '2016-08-01T00:00:00.000Z' AND STARTTIMEISO < '2016-09-24T00:00:00.000Z'  AND sourceNtDomain IN ('E8SEC') AND ( cefSignatureId LIKE '%4624%' ) GROUP BY status ORDER BY status LIMIT 5";

        assertEquals(expected, actual);
    }

    @Ignore
    @Test
    public void testGetFacetQuery2() {
        int modelId = 2;
        List<String> facetList = SearchUtils.getFacetFieldMapForSearchSummary(modelId, getCache());
        String whereClause = "WHERE STARTTIMEISO >= '2016-10-16T00:00:00.000Z' AND STARTTIMEISO <'2016-10-29T00:00:00.000Z'  AND NOT requestMethod IN ('POST','post','GET','get') AND sourceNameOrIp IN  ('192.168.12.18' )";
        String actual = ImpalaUtils.getFacetQuery_v2("WEB_PROXY_KUDU", facetList, whereClause, 5);
        System.out.println(actual);
    }
    @Ignore
    @Test
    public void testParsing() throws Exception{
        String[] header = "requestClientApplication,requestclientapplicationcount,requestclientapplicationtotalcount,sourceUserName,sourceusernamecount,sourcesernametotalcount,responseContentType,responsecontenttypecount,responsecontenttypetotalcount,requestScheme,requestschemecount,requestschemetotalcount,requestMethod,requestmethodcount,requestmethodtotalcount,devicePolicyAction,devicepolicyactioncount,devicepolicyactiontotalcount,sourceNameOrIp,sourcenameoripcount,sourcenameoriptotalcount,cefSignatureId,cefsignatureidcount,cefsignatureidtotalcount,destinationNameOrIp,destinationnameoripcount,destinationnameoriptotalcount\n".split(",");
        String csv =     "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,tiles.services.mozilla.com,1,49\n" +
                        "jupdate,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,javadl-esd-secure.oracle.com,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,blocklists.settings.services.mozilla.com,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,quickdraw.splunk.com,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,snippets.cdn.mozilla.net,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,blocklist.addons.mozilla.org,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,normandy.cdn.mozilla.net,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,addons.cdn.mozilla.net,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,location.services.mozilla.com,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,tiles-cloudfront.cdn.mozilla.net,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,tracking-protection.cdn.mozilla.net,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,1,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,1,49,TCP_MISS,1,49,192.168.12.18,1,49,200,1,49,services.addons.mozilla.org,1,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,2,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,2,49,TCP_MISS,2,49,192.168.12.18,2,49,200,2,49,incoming.telemetry.mozilla.org,2,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,2,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,2,49,TCP_MISS,2,49,192.168.12.18,2,49,200,2,49,aus5.mozilla.org,2,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,2,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,2,49,TCP_MISS,2,49,192.168.12.18,2,49,200,2,49,self-repair.mozilla.org,2,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,3,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,3,49,TCP_MISS,3,49,192.168.12.18,3,49,200,3,49,ftp.mozilla.org,3,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,3,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,3,49,TCP_MISS,3,49,192.168.12.18,3,49,200,3,49,versioncheck-bg.addons.mozilla.org,3,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,5,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,5,49,TCP_MISS,5,49,192.168.12.18,5,49,200,5,49,shavar.services.mozilla.com,5,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,10,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,10,49,TCP_MISS,10,49,192.168.12.18,10,49,200,10,49,safebrowsing.google.com,10,49\n" +
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0,10,49,NULL,0,0,NULL,0,0,NULL,0,0,CONNECT,10,49,TCP_MISS,10,49,192.168.12.18,10,49,200,10,49,safebrowsing-cache.google.com,10,49\n";
        FeatureServiceCache cache = getCache();
        List<String> facetList = Lists.newLinkedList(cache.getWebSummaryFieldMap().keySet());
        List<Map<String, String>> rows = parseCsv(header, csv);
        List<Map<String,  List<Map<String, String>>>>  map = ImpalaUtils.computeSummary_v1(2, cache, rows, facetList);
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

    @Test
    public void testGetImpalaTableName() throws Exception{
        int modelId = 2;
        String actualTableName = ImpalaUtils.getImpalaTableName(modelId,configuration);
        String expectedTableName = "E8SEC.WEB_PROXY_PARQUET";
        assertEquals(actualTableName,expectedTableName);
    }

    @Test
    public void testGetImpalaQuery() throws Exception {
        int modelId = 2;
        int startRecord = 0;
        int endRows = 50;
        String sortField = "STARTTIMEISO";
        String sortOrder = "DESC";
        String whereClause = "WHERE STARTTIMEISO >= '2016-10-16T00:00:00.000Z' AND STARTTIMEISO <'2016-10-29T00:00:00.000Z'  AND NOT requestMethod IN ('POST','post','GET','get') AND sourceNameOrIp IN  ('192.168.12.18' )";
        String actualQuery = ImpalaUtils.getImpalaQuery(modelId, startRecord, endRows, sortField, sortOrder, whereClause, configuration);

        String expectedQuery = "SELECT * FROM E8SEC.WEB_PROXY_PARQUET WHERE STARTTIMEISO >= '2016-10-16T00:00:00.000Z' AND STARTTIMEISO <'2016-10-29T00:00:00.000Z'  AND NOT requestMethod IN ('POST','post','GET','get') AND sourceNameOrIp IN  ('192.168.12.18' ) ORDER BY STARTTIMEISO DESC LIMIT 50 OFFSET 0";
        assertEquals(actualQuery,expectedQuery);
    }

    @Test
    public void testPopulateQueryParams() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>();
        String[] paramList = new String[]{"192.168.12.18"};
        queryParams.put("sourceAddress", Arrays.asList(paramList));
        paramList = new String[]{"Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; rv:11.0) like Gecko"};
        queryParams.put("requestClientApplication", Arrays.asList(paramList));

        String actual = ImpalaUtils.populateQueryParams(queryParams);
        String expected = " AND requestClientApplication IN ('Mozilla\\/5.0\\ \\(Windows\\ NT\\ 6.1\\;\\ Win64\\;\\ x64\\;\\ Trident\\/7.0\\;\\ rv\\:11.0\\)\\ like\\ Gecko') AND sourceAddress IN ('192.168.12.18')";
        assertEquals(actual,expected);
    }

    @Test
    public void testGetColumnName() throws Exception {

        String tableName = "E8SEC.WEB_PROXY_PARQUET";
        String columnName = "requestclientapplication";

        String actual = ImpalaUtils.getColumnName(tableName, columnName, configuration);
        String expected = "requestClientApplication";
        assertEquals(actual,expected);
    }

    @Test
    public void testGetSelectedEntityQuery() throws Exception {
        int modelId = 3;
        int securityEventId = 22;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        String[] selectedEntities = new String[]{"-jyria"};
        String actual = ImpalaUtils.getSelectedEntityQuery(facetInfo,selectedEntities,modelId);

        String expected = " AND cefSignatureId LIKE '%Security_4672%' AND destinationUserName IN  ('jyria' ) ";

        assertEquals(actual, expected);
    }
}