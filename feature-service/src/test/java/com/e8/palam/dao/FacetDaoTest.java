package com.e8.palam.dao;

import com.e8.palam.TestBase;
import com.securityx.modelfeature.SolrServerClient;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.SearchUtils;
import com.securityx.modelfeature.dao.FacetDao;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;

public class FacetDaoTest extends TestBase {

    public FacetDaoTest() {
        super();
    }

    private FacetDao facetDao = null;

    @Before
    public void setup() throws Exception{
        super.setup();
        facetDao = new FacetDao();
    }

    @Test
    public void testFacetResults() {
        SolrServerClient solrClient = new SolrServerClient(super.conf());
        CloudSolrServer webProxySolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.WebPeerAnomaliesModelTuple()._1()));
        CloudSolrServer iamSolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.ADPeerAnomaliesModelTuple()._1()));
        int modelId = 3;
        Set<String> facetFields = new HashSet<String>();
        facetFields.add("sourceUserName");
        facetFields.add("sourceAddress");
        String entityLabel = "destinationUserName";
        String selectedEntities = "[\"v886656\"]";
        String startTime = "2012-07-20T00:00:00.000Z";
        String endTime = "2014-12-22T00:00:00.000Z";
        int facetMinCount = 1;
        int facetLimit = 1;
        int facetStartRows = 0;
        int facetEndRows = 0;

        Map<String, List<Map<String, Long>>> result =
                facetDao.getFacetResults(webProxySolrClient, iamSolrClient, modelId, 13, selectedEntities,
                                         startTime, endTime, facetMinCount, facetLimit, facetStartRows, facetEndRows,
                                         super.cache());

        assertNotNull(result);

    }
    @Test
    public void testFacetResultsNull() {
        SolrServerClient solrClient = new SolrServerClient(super.conf());
        CloudSolrServer webProxySolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.WebPeerAnomaliesModelTuple()._1()));
        CloudSolrServer iamSolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.ADPeerAnomaliesModelTuple()._1()));
        int modelId = 3;
        Set<String> facetFields = new HashSet<String>();
        facetFields.add("sourceUserName");
        facetFields.add("sourceAddress");
        String entityLabel = "destinationUserName";
        String selectedEntities = null;
        String startTime = "2012-07-20T00:00:00.000Z";
        String endTime = "2014-12-22T00:00:00.000Z";
        int facetMinCount = 1;
        int facetLimit = 1;
        int facetStartRows = 0;
        int facetEndRows = 0;

        Map<String, List<Map<String, Long>>> result =
                facetDao.getFacetResults(webProxySolrClient, iamSolrClient, modelId, 13, selectedEntities,
                        startTime, endTime, facetMinCount, facetLimit, facetStartRows, facetEndRows,
                        super.cache());

        assertNotNull(result);

    }
    @Test
    public void testFacetResultsEmpty() {
        SolrServerClient solrClient = new SolrServerClient(super.conf());
        CloudSolrServer webProxySolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.WebPeerAnomaliesModelTuple()._1()));
        CloudSolrServer iamSolrClient = solrClient.getSolrServer(SearchUtils.getCollectionName(
                Constants.ADPeerAnomaliesModelTuple()._1()));
        int modelId = 3;
        Set<String> facetFields = new HashSet<String>();
        facetFields.add("sourceUserName");
        facetFields.add("sourceAddress");
        String entityLabel = "destinationUserName";
        String selectedEntities = "";
        String startTime = "2012-07-20T00:00:00.000Z";
        String endTime = "2014-12-22T00:00:00.000Z";
        int facetMinCount = 1;
        int facetLimit = 1;
        int facetStartRows = 0;
        int facetEndRows = 0;

        Map<String, List<Map<String, Long>>> result =
                facetDao.getFacetResults(webProxySolrClient, iamSolrClient, modelId, 13, selectedEntities,
                        startTime, endTime, facetMinCount, facetLimit, facetStartRows, facetEndRows,
                        super.cache());

        assertNotNull(result);

    }
}
