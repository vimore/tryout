package com.e8.palam.dao;

import com.e8.palam.TestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.common.inputs.PeerGroupSearchInput;
import com.securityx.modelfeature.dao.solr.SearchSolrDao;
import com.securityx.modelfeature.utils.AdFacetHelper;
import com.securityx.modelfeature.utils.PeerGroupFacetHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class SearchDaoTest extends TestBase {

    public SearchDaoTest() {
        super();
    }

    //private SearchDao searchDao = null;

    Map<String, List<String>> emptyQueryParams = new HashMap<String, List<String>>();

    @Before
    public void setup() throws Exception{
        super.setup();
        //searchDao = new SearchSolrDao();
    }


    //////////////////////////////////////////////////////////////////////////////////
    //                  Tests for model 2                                           //
    //////////////////////////////////////////////////////////////////////////////////

    @Test
    public void model2SecEvent0Test() {
        int modelId = 2;
        int securityEventId = 0;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.64"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.64))", queryString);
    }

    @Test
    public void model2SecEvent1Test() {
        int modelId = 2;
        int securityEventId = 1;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.66"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("devicePolicyAction:(*DENIED* OR *denied* OR *block* OR *BLOCK*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.66))"));
    }

    @Test
    public void model2SecEvent2Test() {
        int modelId = 2;
        int securityEventId = 2;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.66"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("deviceEventCategory:(*pending* OR *unavailable* OR *uncategorized* OR *unknown* OR *parked* OR *not\\-resolved*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.66))"));
    }

    @Test
    public void model2SecEvent3Test() {
        int modelId = 2;
        int securityEventId = 3;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.164"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("deviceEventCategory:(*Suspicious* OR *suspicious* OR  *Malicious* OR *malicious* OR *unwanted* OR *Unwanted* OR *malware* OR *Malware* OR *malnet* OR *Malnet* OR *spy* OR *Spy* OR *hacking* OR *Hacking* OR *phishing* OR *Phishing* OR *questionable* OR *Questionable* OR *proxy\\-avoidence\\-and\\-anonym* OR *dynamic\\-dns*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.164))"));
    }

    @Test
    public void model2SecEvent4Test() {
        int modelId = 2;
        int securityEventId = 4;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.167"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.167))", queryString);
    }

    @Test
    public void model2SecEvent5Test() {
        int modelId = 2;
        int securityEventId = 5;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.164"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("-destinationPort:80 AND -destinationPort:8080 AND -destinationPort:443"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.164))"));
    }

    @Test
    public void model2SecEvent6Test() {
        int modelId = 2;
        int securityEventId = 6;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.55"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.55))", queryString);
    }

    @Test
    public void model2SecEvent7Test() {
        int modelId = 2;
        int securityEventId = 7;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.167"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.167))", queryString);
    }

    @Test
    public void model2SecEvent8Test() {
        int modelId = 2;
        int securityEventId = 8;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.164"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("NOT requestMethod:(*POST* OR *post* OR *GET* OR *get*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.164))"));
    }

    @Test
    public void model2SecEvent9Test() {
        int modelId = 2;
        int securityEventId = 9;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.111"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.111))", queryString);
    }

    @Test
    public void model2SecEvent10Test() {
        int modelId = 2;
        int securityEventId = 10;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.25"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.25))", queryString);
    }

    @Test
    public void model2SecEvent11Test() {
        int modelId = 2;
        int securityEventId = 11;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.119"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.119))", queryString);
    }

    @Test
    public void model2SecEvent12Test() {
        int modelId = 2;
        int securityEventId = 12;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.31"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("devicePolicyAction:(*DENIED* OR *denied* OR *block* OR *BLOCK*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.31))"));
    }

    @Test
    public void model2SecEvent13Test() {
        int modelId = 2;
        int securityEventId = 13;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.31"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("deviceEventCategory:(*pending* OR *unavailable* OR *uncategorized* OR *unknown* OR *parked* OR *not\\-resolved*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.31))"));
    }

    @Test
    public void model2SecEvent14Test() {
        int modelId = 2;
        int securityEventId = 14;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.164"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("deviceEventCategory:(*Suspicious* OR *suspicious* OR  *Malicious* OR *malicious* OR *unwanted* OR *Unwanted* OR *malware* OR *Malware* OR *malnet* OR *Malnet* OR *spy* OR *Spy* OR *hacking* OR *Hacking* OR *phishing* OR *Phishing* OR *questionable* OR *Questionable* OR *proxy\\-avoidence\\-and\\-anonym* OR *dynamic\\-dns*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.164))"));
    }

    @Test
    public void model2SecEvent15Test() {
        int modelId = 2;
        int securityEventId = 15;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.119"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertEquals("(sourceNameOrIp:(192.168.1.119))", queryString);
    }

    @Test
    public void model2SecEvent16Test() {
        int modelId = 2;
        int securityEventId = 16;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.16"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("requestMethod:(*POST* OR *post*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.16))"));
    }

    @Test
    public void model2SecEvent17Test() {
        int modelId = 2;
        int securityEventId = 17;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.164"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("requestMethod:(*POST* OR *post*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.164))"));
    }

    @Test
    public void model2SecEvent18Test() {
        int modelId = 2;
        int securityEventId = 18;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.117"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:*"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.117))"));
    }

    @Test
    public void model2SecEvent19Test() {
        int modelId = 2;
        int securityEventId = 19;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.85"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:*"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.85))"));
    }

    @Test
    public void model2SecEvent20Test() {
        int modelId = 2;
        int securityEventId = 20;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.49"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:(4*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.49))"));
    }

    @Test
    public void model2SecEvent21Test() {
        int modelId = 2;
        int securityEventId = 21;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        String[] selectedEntities = new String[]{"192.168.1.31"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:(5*)"));
        assertTrue(queryString.contains("(sourceNameOrIp:(192.168.1.31))"));
    }

    //////////////////////////////////////////////////////////////////////////////////
    //                  Tests for model 3                                           //
    //////////////////////////////////////////////////////////////////////////////////

    @Test
    public void populateSolrQueryStringForPeerGroupTest(){
        int modelId = 3;
        int securityEventId = 537;
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        String[] selectedEntities = new String[]{"mark_ingram"};
        String[] keywords = new String[] {"(\\\\*\\Finance)", "(\\\\*\\Shared)"};
        String[] facetFields = new String[]{"destinationShareName"};
        String[] sumFields = null;
        String cefSignature = "5140";
        String entryLabel = "sourceUserName";
        String sortOrder = "DESC";
        //PeerGroupFacetHelper facetInfo  = new PeerGroupFacetHelper(facetFields, sumFields, entryLabel, sortOrder);
        AdFacetHelper adFacetHelper = new AdFacetHelper(cefSignature, facetFields, sumFields, entryLabel, sortOrder);

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, queryParams, selectedEntities, keywords, adFacetHelper);

        assertEquals("cefSignatureId:*5140* AND (sourceUserName:(mark_ingram)) AND (rawLog:*\\\\\\\\\\*\\\\Finance* OR rawLog:*\\\\\\\\\\*\\\\Shared*)", queryString);
    }
    @Test
    public void populateSolrQueryStringForPeerGroupTest2(){
        int modelId = 3;
        int securityEventId = 537;
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        String[] selectedEntities = new String[]{"mark_ingram"};
        String[] keywords = new String[] {"(\\\\*\\Finance)"};
        String[] facetFields = new String[]{"destinationShareName"};
        String[] sumFields = null;
        String cefSignature = "5140";
        String entryLabel = "sourceUserName";
        String sortOrder = "DESC";
        //PeerGroupFacetHelper facetInfo  = new PeerGroupFacetHelper(facetFields, sumFields, entryLabel, sortOrder);
        AdFacetHelper adFacetHelper = new AdFacetHelper(cefSignature, facetFields, sumFields, entryLabel, sortOrder);

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, queryParams, selectedEntities, keywords, adFacetHelper);

        assertEquals("cefSignatureId:*5140* AND (sourceUserName:(mark_ingram)) AND (rawLog:*\\\\\\\\\\*\\\\Finance*)", queryString);
    }

    @Test
    public void tstPeerGroupSearchInput()throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = "{\"startTime\":\"2016-02-10T00:00:00.000Z\",\"endTime\":\"2016-02-17T00:00:00.000Z\",\"numRows\":50,\"pageNo\":1,\"summarize\":true,\"modelId\":7,\"securityEventId\":537,\"queryParams\":{},\"selectedEntities\":[\"mark_ingram\"],\"sortField\":\"startTimeISO\",\"sortOrder\":\"desc\",\"keywords\":[\"(\\\\\\\\*\\\\Finance)\",\"(\\\\\\\\*\\\\Shared)\"]}";
        PeerGroupSearchInput input = mapper.readValue(jsonStr, PeerGroupSearchInput.class);
        assertNotNull("Failed ot parse new keywords field in the object", input);

        jsonStr = "{\"startTime\":\"2016-02-10T00:00:00.000Z\",\"endTime\":\"2016-02-17T00:00:00.000Z\",\"numRows\":50,\"pageNo\":1,\"summarize\":true,\"modelId\":7,\"securityEventId\":537,\"queryParams\":{},\"selectedEntities\":[\"mark_ingram\"],\"sortField\":\"startTimeISO\",\"sortOrder\":\"desc\"}";
        input = mapper.readValue(jsonStr, PeerGroupSearchInput.class);
        assertNotNull("Failed to parse the string when keywords is not specified. Backwards compatibility is broken!!!!", input);
    }

    //////////////////////////////////////////////////////////////////////////////////
    //                  Tests for model 7                                           //
    //////////////////////////////////////////////////////////////////////////////////
    @Test
    public void model7SecEvent535Test() {
        int modelId = 7;
        int securityEventId = 535;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);

        // This string will cause us to check that we correctly escape spaces
        String[] selectedEntities = new String[]{"arnold felly"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:*4733*"));
        assertTrue(queryString.contains("(destinationUserName:(arnold\\ felly))"));

        // Check also for other solr reserved characters (a list of reserved characters can be found at
        // https://lucene.apache.org/core/2_9_4/queryparsersyntax.html#Escaping Special Characters
        selectedEntities = new String[]{"FW\\\\elully"};

        queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:*4733*"));
        assertTrue(queryString.contains("(destinationUserName:(FW\\\\\\\\elully))"));

        selectedEntities = new String[]{"ab{mark^arundel}"};

        queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, emptyQueryParams, selectedEntities, null, facetInfo);
        assertTrue(queryString.contains("cefSignatureId:*4733*"));
        assertTrue(queryString.contains("(destinationUserName:(ab\\{mark\\^arundel\\}))"));
    }

    @Test
    public void populateSolrQueryStringForPeerGroupTest3() {
        int modelId = 7;
        int securityEventId = 535;
        PeerGroupFacetHelper facetInfo = getCache().getPeerFacetHelperFromModelIdSecEventId(modelId, securityEventId);
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
        String[] selectedEntities = new String[]{"mark ingram"};
        String[] keywords = new String[] {"(Security-4733-Success Audit)"};

        String queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, queryParams, selectedEntities, keywords, facetInfo);
        assertEquals("cefSignatureId:*4733* AND (destinationUserName:(mark\\ ingram))", queryString);

        // Test with multiple keywords, one of which we want to use
        keywords = new String[] {"(Security-4733-Success Audit)", "(\\\\*\\Shared)"};
        queryString = SearchSolrDao.populateSolrQueryStringForPeerGroup(modelId, securityEventId, queryParams, selectedEntities, keywords, facetInfo);
        assertEquals("cefSignatureId:*4733* AND (destinationUserName:(mark\\ ingram)) AND (rawLog:*\\\\\\\\\\*\\\\Shared*)", queryString);
    }



    //TODO Add more tests for populateSolrQueryStringForPeerGroup. Currently we are only testing modelId 2 and 3 and some limited testing for model 7,
    // need to add tests for more modelIds
    /*
    curl 'http://localhost:9080/service/search' -H 'Authorization: Basic ZThzZWM6MW55ZWV1NzNuZzNjOGZyNzcwbTdqM3ZkdnFzMzhmcg==' -H 'Origin: https://10.10.80.87' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36' -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Referer: https://10.10.80.87/entityinvestigator?entities=%7B%22name%22%3A%22mark_ingram%22%2C%22type%22%3A%22userName%22%7D&start_time=2016-02-10T00%3A00%3A00.000Z%2C2016-02-16T00%3A00%3A00.000Z&riskRange=0%2C1&moduleName=INVESTIGATOR' -H 'X-Requested-With: XMLHttpRequest' -H 'Cookie: i18next=en-US' -H 'Connection: keep-alive' --data-binary '{"startTime":"2016-02-10T00:00:00.000Z","endTime":"2016-02-17T00:00:00.000Z","numRows":50,"pageNo":1,"summarize":true,"modelId":7,"securityEventId":537,"queryParams":{},"selectedEntities":["mark_ingram"],"sortField":"startTimeISO","sortOrder":"desc","keywords":["(\\\\*\\Finance)","(\\\\*\\Shared)"]}' --compressed --insecure
     */
}
