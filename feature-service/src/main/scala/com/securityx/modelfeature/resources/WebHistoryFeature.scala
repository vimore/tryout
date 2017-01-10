package com.securityx.modelfeature.resources

import javax.validation.Valid
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.WebProfileAnomalyInput
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{HostEntityPropertiesDao, WebHistoryDao}
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.slf4j.{Logger, LoggerFactory}

@Path ("/webhistory")
@Produces(Array(MediaType.APPLICATION_JSON))
class WebHistoryFeature (val mapper:ObjectMapper, val webProxySolrClient:CloudSolrServer,iamSolrClient: CloudSolrServer, val conf:FeatureServiceConfiguration, cache: FeatureServiceCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[WebHistoryFeature])
  private val parser: DateTimeFormatter = ISODateTimeFormat.dateTimeParser()
  private val webHistoryDao = new WebHistoryDao(conf)
  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)

  @GET
  @Timed
  def getAnomalyProfileSeries(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String,
                        @QueryParam("period") periodInt : Integer) = {

    val buf = webHistoryDao.getAnomalyProfiles(startTime, endTime, periodInt, cache)
    val profileEntries = buf.map(x => scala.collection.mutable.Map[String,Any]("dateTime" -> x.get("dateTime"),
                                      "anomalyProfile" -> x.get("anomalyProfile"),
                                      "riskScore" ->x.get("riskScore"),
                                      "ipAddress" -> x.get("ipAddress"),
                                      "eventType" -> x.getOrElse("eventType", null),
                                      "eventCategory" -> x.getOrElse("eventCategory", null)
    ))

    val a = mapper.writeValueAsString(profileEntries)
    Response.ok(a).build()
  }

  @GET
  @Path("/riskscores")
  @Timed
  def getRiskScoresPerAnomalyProfile(@QueryParam("dateTime") dateTime : String,
                              @QueryParam("anomalyProfile") anomalyProfile : String) = {

    val buf = webHistoryDao.getRiskScoresPerAnomalyProfile(dateTime, anomalyProfile)
    val riskEntries = buf.map(x => scala.collection.mutable.Map[String,Any](
      "ipAddress" -> x.get("ipAddress"),
      "riskScore" ->x.get("riskScore")
    ))

    val a = mapper.writeValueAsString(riskEntries)
    Response.ok(a).build()
  }

  @GET
  @Path("/anomalyProfile")
  @Timed
  def getEntityAnomalyProfile(@QueryParam("dateTime") dateTime : String,
                                  @QueryParam("anomalyProfile") anomalyProfile : String) = {

    val buf = webHistoryDao.getEntityAnomalyProfile(dateTime, anomalyProfile)
    val entries = buf.map(x => scala.collection.mutable.Map[String,Any](
      "ipAddress" -> { if(x.contains("ipAddress") && !x.get("ipAddress").equals(Some(null))) x.get ("ipAddress") else  null },
      "anomalyProfile" -> { if(x.contains("anomalyProfile") && !x.get("anomalyProfile").equals(Some(null))) x.get ("anomalyProfile")
      else  null },
      "type" -> { if(x.contains("type") && !x.get("type").equals(Some(null))) x.get("type")
      else null },
      "subType" -> { if(x.contains("subType") && !x.get("subType").equals(Some(null))) x.get("subType")
      else null },
      "count" -> { if(x.contains("count") && !x.get("count").equals(Some(null))) x.get("count") else  null }
    ))

    val a = mapper.writeValueAsString(entries)
    Response.ok(a).build()
  }

  @GET
  @Path("/entities")
  @Timed
  def getEntPropDetails(@QueryParam("ip") ip : String,
                               @QueryParam("dateTime") dateTime : String) = {

    val buf = hostEntityPropertiesDao.getEntHostPropsByIp(ip, dateTime)
    val entries = buf.map(x => scala.collection.mutable.Map[String,Any](
      "anomalyProfile" -> ip,
      "country" -> x.get("country").orNull,
      "city" ->  x.get("city").orNull,
      "os" -> x.get("os").orNull,
      "browserName" -> x.get("browserName").orNull,
      "browserVersion" -> x.get("browserVersion").orNull,
      "hostName" -> x.get("hostName").orNull,
      "primaryUserId" -> x.get("primaryUserId").orNull,
      "macAddress" -> x.get("macAddress").orNull,
      "userName" -> x.get("userName").orNull
    ))

    val a = mapper.writeValueAsString(entries)
    Response.ok(a).build()
  }

  @POST
  @Path("/facet")
  @Timed
  def getTopNFacets(@Valid input: WebProfileAnomalyInput) ={
   val buf = webHistoryDao.getTopNDestinations(webProxySolrClient, iamSolrClient, input.getSourceAddress, input.getQueryParams, input.getStartTime, input.getEndTime, input.getFacetLimit, input.getPageNo, input.isSummarize, cache);
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @POST
  @Path("/search")
  @Timed
  def getSearchResults(@Valid input: WebProfileAnomalyInput, @QueryParam("modelId") modelId : Int) ={
    val buf = webHistoryDao.getSearchResults(webProxySolrClient, iamSolrClient, modelId,
      input.getSourceAddress, input.getQueryParams, input.getStartTime,input.getEndTime, input.getNumRows, input.getPageNo, input.getFacetLimit, input.getSortField, input.getSortOrder, input.isSummarize, cache);
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

}
