package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.BehaviorAnomalyDao
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by harish on 8/20/15.
 */


@Path ("/abnormalbehaviour")
@Produces(Array(MediaType.APPLICATION_JSON))
class AbnormalBehaviorFeature (val mapper:ObjectMapper, val webProxySolrClient:CloudSolrServer,iamSolrClient: CloudSolrServer,
                               val conf:FeatureServiceConfiguration, cache: FeatureServiceCache) {
  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[AbnormalBehaviorFeature])
  private val behaviorAnomalyDao = new BehaviorAnomalyDao(conf)

  @GET
  @Path ("/")
  @Timed
  def getTimeSeriesBehaviorAnomalies(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String,
                                      @QueryParam("modelId") modelId : Integer) = {
    val buf = behaviorAnomalyDao.getAnomaliesByModelId(startTime, endTime, modelId, cache)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

}
