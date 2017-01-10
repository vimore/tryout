package com.e8.palam.dao;

import com.e8.palam.TestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.common.inputs.TimeSeriesInput;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.SearchImpalaDao;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.ImpalaUtils;
import com.securityx.modelfeature.utils.PeerGroupFacetHelper;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

@SuppressWarnings({"unchecked"})
public class SearchImpalaDaoOnlineTest extends TestBase {

    public SearchImpalaDaoOnlineTest()  {
        super();
    }

    //private SearchDao searchDao = null;

    Map<String, List<String>> emptyQueryParams = new HashMap<>();
    private static FeatureServiceConfiguration configuration = new FeatureServiceConfiguration();
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static SearchImpalaDao impalaDao = null;

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
        impalaDao = new SearchImpalaDao(configuration);
    }

    @Test
    public void getSearchResultsForSourceDestinationTest() throws Exception {

        int modelId = Constants.WebPeerAnomaliesModelTuple()._1();
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        List<String> sources = new ArrayList<>();
        int summaryFacetLimit = 5;
        int endRows=50;
        int pageNo=1;
        String sortField ="STARTTIMEISO";
        String sortOrder = "DESC";
        boolean summarize = false;
        int startRecord = endRows * (pageNo - 1);

        sources.add("192.168.12.30");

        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        List<String> destinations = new ArrayList<>();
        destinations.add("r20swj13mr.microsoft.com");
        Map<String, List<String>>  queryParams = new HashMap<>();
        //String[] paramList = new String[] {"Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; rv:11.0) like Gecko"};
        //queryParams.put("requestClientApplication", Arrays.asList(paramList));

        List<Map<String, Object>> results = impalaDao.getSearchResultsForSourceDestination(modelId,  sources,
                                                                    destinations, queryParams, startTime, endTime, 5, endRows, pageNo,
                                                                    sortField, sortOrder, summarize, cache);
        assertNotNull(results);
        for(Map<String, Object> map : results) {
            List<Map> resultList = (List<Map>)map.get("result");
            assertNotNull(resultList);
            for(Map valueMap: resultList) {
                //if(valueMap.containsKey("sourcena,"))
                String ip  = (String)valueMap.get("sourceNameOrIp");
                assertNotNull(ip);
                if(ip.equals("192.168.12.30")) {
                    assertEquals("r20swj13mr.microsoft.com", valueMap.get("destinationNameOrIp"));
                    //assertEquals("Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; rv:11.0) like Gecko", (String)valueMap.get("requestclientapplication"));
                } else {
                    assertTrue("Got unexpected ip address [" + ip + "]", false);
                }

            }
        }

    }

    @Test
    public void getFacetedSearchResultsTest() throws Exception {
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
        String sortField ="STARTTIMEISO";
        String sortOrder = "DESC";
        boolean summarize = false;
        //int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"E8SEC"};
        queryParams.put("destinationNtDomain", Arrays.asList(paramList));

        List<Map<String, Object>> results = impalaDao.getFacetedSearchResults(modelId,  securityEventId,
                                                                    queryParams, selectedEntities, new String[]{},startTime,
                                                                    endTime, 5, endRows, pageNo, sortField, sortOrder, summarize, cache);
        assertNotNull(results);
        assertNotEquals(0, results.size());
        for(Map<String, Object> map : results) {
            List<Map> resultList = (List<Map>)map.get("result");
            assertNotNull(resultList);
            for(Map valueMap: resultList) {
                String entity  = (String)valueMap.get("destinationUserName");
                assertNotNull(entity);
                if(entity.equals("jyria")) {
                    assertEquals("E8SEC", valueMap.get("destinationNtDomain"));
                } else {
                    assertTrue("Got unexpected entity [" + entity + "]", false);
                }


            }
        }

    }

    @Test
    public void getWebAnomalyFacetedSearchResultsTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        int modelId = 6;
        String source = "w2k8r2-ad$";
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        int summaryFacetLimit = 5;
        int endRows=50;
        int pageNo=1;
        String sortField ="STARTTIMEISO";
        String sortOrder = "DESC";
        boolean summarize = false;
        int startRecord = endRows * (pageNo - 1);

        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"192.168.12.18"};
        queryParams.put("sourceAddress", Arrays.asList(paramList));

        List<Map<String, Object>> results = impalaDao.getWebAnomalyFacetedSearchResults(modelId, queryParams,
                                                                    source, startTime, endTime, endRows, pageNo, summaryFacetLimit, sortField,
                                                                    sortOrder, summarize, cache);
        assertNotNull(results);
        for(Map<String, Object> map : results) {
            List<Map> resultList = (List<Map>)map.get("result");
            assertNotNull(resultList);
            for(Map valueMap: resultList) {
                //String entity  = (String)valueMap.get("sourceaddress");
                String sourceUser = (String)valueMap.get("sourceUserName");
                String destUser = (String)valueMap.get("destinationUserName");
                //assertNotNull(sourceUser);
                //assertNotNull(destUser);
                if((sourceUser != null &&  sourceUser.equals("w2k8r2-ad$")) ||
                        (destUser != null && destUser.equals("w2k8r2-ad$"))) {
                    assertEquals("192.168.12.18", valueMap.get("sourceAddress"));
                } else {
                    assertTrue("Got unexpected entity [" + sourceUser +"/"+destUser + "]", false);
                }
            }
        }
    }

    @Test
    public void getTimeSeriesResultsForHttpTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(2);
        input.setStartTime("2016-10-17T00:00:00.000Z");
        input.setEndTime("2016-10-18T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("STARTTIMEISO");
        input.setSortOrder("DESC");
        input.setSummarize(false);
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
        facetMap.put("groupId","10.7.7.2");
        //facetMap.put("groupId","200");
        facetList.add(facetMap);

        facetMap = new HashMap<>();
        facetMap.put("typeField","topDestination");
        facetMap.put("groupId","10.10.30.224");
        //facetMap.put("groupId","200");
        facetList.add(facetMap);

        /*facetMap = new HashMap();
        facetMap.put("typeField","requestScheme");
        facetMap.put("group","HTTP");
        //facetMap.put("groupId","200");
        facetList.add(facetMap);*/

        input.setFacets(facetList);
        Map<String, List<String>>  queryParams = new HashMap<>();
        String[] paramList = new String[] {"10.10.7.2"};
        queryParams.put("sourceNameOrIp", Arrays.asList(paramList));
        input.setQueryParams(queryParams);

        List<Map<String, Object>> results = impalaDao.getTimeSeriesSearchResults(input, cache);
        assertNotNull(results);
        for(Map<String, Object> map : results) {
            List<Map> resultList = (List<Map>)map.get("result");
            assertNotNull(resultList);
            for(Map valueMap: resultList) {
                String entity  = (String)valueMap.get("cefSignatureId");
                assertNotNull(entity);
                switch(entity) {
                    case "200":
                    case "400":
                    case "404":
                        break;
                    default:
                        assertTrue("Got unexpected entity [" + entity + "]", false);
                        break;
                }
            }
        }
    }

    @Test
    public void getTimeSeriesresultsForAdTest() throws Exception {
        FeatureServiceCache cache = new FeatureServiceCache(configuration);
        TimeSeriesInput input = new TimeSeriesInput();
        input.setModelId(3);
        input.setStartTime("2016-10-17T00:00:00.000Z");
        input.setEndTime("2016-10-18T00:00:00.000Z");
        input.setNumRows(50);
        input.setPageNo(1);
        input.setSortField("STARTTIMEISO");
        input.setSortOrder("DESC");
        input.setSummarize(false);
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

        List<Map<String, Object>> results = impalaDao.getTimeSeriesSearchResults(input, cache);
        assertNotNull(results);
        for(Map<String, Object> map : results) {
            List<Map> resultList = (List<Map>)map.get("result");
            assertNotNull(resultList);
            for(Map valueMap: resultList) {
                String entity  = (String)valueMap.get("cefSignatureId");
                if(entity.contains("4624")) {
                    assertEquals("E8SEC",valueMap.get("sourceNtDomain"));
                } else {
                    assertTrue("Got unexpected entity [" + entity + "]", false);
                }

            }
        }
    }

    @Test
    public void getAutoCompleteRecordsTest() throws Exception{

        int modelId = 2;
        String fieldType = "destination";
        String incomingString = "c";
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        int startRecord = 0;
        int pageSize = 10;

        Set<String> results = impalaDao.getAutoCompleteRecords(modelId, fieldType, incomingString,
                                                                        startTime, endTime, startRecord, pageSize);
        assertNotNull(results);
        assertNotEquals(0,results.size());
        for(String values : results) {
            if(values.startsWith(incomingString)) {
                assertTrue("Got expected entity [" + values + "]", true);
            } else {
                assertTrue("Got unexpected entity [" + values + "]", false);
            }
        }
    }

    @Test
    public void getTotalRecordsTimeFilterTest() throws Exception{
        int modelId = 2;
        String startTime = "2016-10-17T00:00:00.000Z";
        String endTime = "2016-10-18T00:00:00.000Z";
        int len = ImpalaUtils.getTotalRecordsTimeFilter(modelId, startTime, endTime, configuration);
        assertEquals(52, len);
    }
}
