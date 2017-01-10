package com.securityx.modelfeature.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.common.inputs.DestinationSearch;
import com.securityx.modelfeature.common.inputs.PeerGroupSearchInput;
import com.securityx.modelfeature.common.inputs.SourceDestinationSearchInput;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.SearchDao;
import com.securityx.modelfeature.dao.SearchImpalaDao;
import com.securityx.modelfeature.dao.solr.SearchSolrDao;
import com.securityx.modelfeature.utils.Constants;
import com.securityx.modelfeature.utils.MiscUtils;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrDocument;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchFeature.class);

    private CloudSolrServer webProxySolrClient =  null;
    private CloudSolrServer iamSolrClient =  null;
    private ObjectMapper mapper = null;
    private SearchDao searchDao;
    private FeatureServiceCache cache = null;
    private FeatureServiceConfiguration conf = null;

    public SearchFeature(ObjectMapper mapper, CloudSolrServer webProxySolrClient, CloudSolrServer iamSolrClient,
                         FeatureServiceConfiguration conf, FeatureServiceCache cache) {
        this.webProxySolrClient = webProxySolrClient;
        this.iamSolrClient = iamSolrClient;
        this.mapper = mapper;
        this.cache = cache;
        this.conf = conf;
    }

    @POST
    @Timed
    public Response getFacetedSearchResults(@Valid PeerGroupSearchInput input)
                                    throws Exception {
        String startTime = MiscUtils.getYMDSeparatedString(input.getStartTime());

        int daysElapsed = Days.daysBetween(new DateTime(startTime), DateTime.now()).getDays();

        if(daysElapsed <= 15) {
            searchDao = new SearchSolrDao(webProxySolrClient, iamSolrClient);
        } else {
            searchDao = new SearchImpalaDao(conf);
        }
        List<Map<String, Object>> result = searchDao.getFacetedSearchResults(input.getModelId(),input.getSecurityEventId(), input.getQueryParams(),
                input.getSelectedEntities(), input.getKeywords(), input.getStartTime(),input.getEndTime(), input.getFacetLimit(),
                input.getNumRows(), input.getPageNo(), input.getSortField(), input.getSortOrder(), input.isSummarize(), cache);

        String a = this.mapper.writeValueAsString(result);
        return Response.ok(a).build();
    }


    /**
     *
     * Lists all the logs associated with 1 IP from since 30 days.
     *
     * @param ip String DestinationNameOrIp
     * @param dateTime String specifying time.
     *
     * @return List of all the logs associated with an IP from since 30 days.
     *
     * @throws Exception
     */
    @GET
    @Path("/destination/ip")
    @Timed
    @Deprecated
    public Response getWebLogsForDestinationIp(@QueryParam("ip") String ip, @QueryParam("dateTime") String dateTime,
                                               @QueryParam("lastNDays") int lastNDays) throws Exception {
        SearchSolrDao searchSolrDao = new SearchSolrDao(webProxySolrClient, iamSolrClient);
        List<SolrDocument> result =  searchSolrDao.getWebLogsForDestinationIpOrDomain(this.webProxySolrClient, ip, dateTime, lastNDays);
        String a = this.mapper.writeValueAsString(result);
        return Response.ok(a).build();
    }

    /**
     * Lists all the logs associated with 1 Domain from since 30 days.
     *
     * @param domain String Domain name
     * @param dateTime String specifying time.
     *
     * @return List of all the logs associated with an IP from since 30 days.
     *
     * @throws Exception
     */
    @GET
    @Path("/destination/domain")
    @Timed
    @Deprecated
    public Response getWebLogsForDomain(@QueryParam("domain") String domain, @QueryParam("dateTime") String dateTime,
                                        @QueryParam("lastNDays") int lastNDays) throws Exception {
        SearchSolrDao searchSolrDao = new SearchSolrDao(webProxySolrClient, iamSolrClient);
        List<SolrDocument> result =  searchSolrDao.getWebLogsForDestinationIpOrDomain(this.webProxySolrClient, domain, dateTime,
                lastNDays);
        String a = this.mapper.writeValueAsString(result);
        return Response.ok(a).build();
    }

    /**
     * Lists all the logs associated with multiple IPs and/or multiple domains from since 30 days.
     *
     * @return List of all the logs associated with an IP from since 30 days.
     *
     * @throws Exception
     */
    @POST
    @Path("/destinations/")
    @Timed
    @Deprecated
    public Response getWebLogsForDestinationIp(@Valid DestinationSearch input) throws Exception {
        SearchSolrDao searchSolrDao = new SearchSolrDao(webProxySolrClient, iamSolrClient);
        List<SolrDocument> result =  searchSolrDao.getLogsForIPsDomains(this.webProxySolrClient, input.getIps(), input.getDomains(),
                input.getUrls(), input.getSourceAddress(), input.getDateTime(), input.getLastNDays());
        String a = this.mapper.writeValueAsString(result);
        return Response.ok(a).build();
    }


    /**
     * Api to search for any given Source to Destination  (edge)
     * @param input
     * @return
     * @throws Exception
     */
    @POST
    @Path("/edge/")
    @Timed
    public Response getSearchForSourceDestination(@Valid SourceDestinationSearchInput input) throws Exception{
        String startTime = MiscUtils.getYMDSeparatedString(input.getStartTime());

        int daysElapsed = Days.daysBetween(new DateTime(startTime), DateTime.now()).getDays();

        if(daysElapsed <= 15) {
            searchDao = new SearchSolrDao(webProxySolrClient, iamSolrClient);
        } else {
            searchDao = new SearchImpalaDao(conf);
        }
        List<Map<String, Object>> result = searchDao.getSearchResultsForSourceDestination(Constants.WebPeerAnomaliesModelTuple()._1(),
                input.getSource(), input.getDestination(), input.getQueryParams(),
                input.getStartTime(), input.getEndTime(), input.getFacetLimit(), input.getNumRows(), input.getPageNo(), input.getSortField(), input.getSortOrder(), input.isSummarize(), cache);
        String a = this.mapper.writeValueAsString(result);
        return Response.ok(a).build();
    }
}