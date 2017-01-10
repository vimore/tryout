package com.securityx.modelfeature.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.FacetDao;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/facets")
@Produces(MediaType.APPLICATION_JSON)
public class FacetFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacetFeature.class);

    private CloudSolrServer webProxySolrClient =  null;
    private CloudSolrServer iamSolrClient =  null;
    private ObjectMapper mapper = null;
    private FacetDao facetDao = new FacetDao();
    private FeatureServiceCache cache = null;

    public FacetFeature (ObjectMapper mapper, CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                         FeatureServiceConfiguration conf, FeatureServiceCache cache) {
        this.webProxySolrClient = webProxySolrClient;
        this.iamSolrClient = iamSolrClient;
        this.mapper = mapper;
        this.cache = cache;
    }

    @GET
    @Timed
    public Response getFacetResults(@QueryParam("modelId") int modelId,@QueryParam("eventId") int securityEventId,
                                    @QueryParam("selectedEntities") String selectedEntities,
                                    @QueryParam("startTime") String startTime, @QueryParam("endTime") String endTime)
                                    throws Exception {

        Map<String, List<Map<String, Long>>> result = facetDao.getFacetResults(this.webProxySolrClient, this.iamSolrClient, modelId, securityEventId,
                selectedEntities, startTime, endTime, 1, 100, 0, 0, cache);

        String a = this.mapper.writeValueAsString(result);
        return Response.ok(a).build();
    }
}
