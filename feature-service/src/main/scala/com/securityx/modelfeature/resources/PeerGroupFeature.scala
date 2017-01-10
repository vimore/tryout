package com.securityx.modelfeature.resources

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.utils.Constants
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{HostEntityPropertiesDao, PeerGroupDao, PeerGroupLabelDao}
import com.wordnik.swagger.annotations.{ApiParam, Api, ApiOperation}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

@Path("/peergroup")
@Produces(Array(MediaType.APPLICATION_JSON))
@Api(value = "/peergroup",
  description = "API End point to provide Peer Group Features, Anomalies and Entities.",
  produces = MediaType.APPLICATION_JSON)
class PeerGroupFeature(val mapper: ObjectMapper, val conf: FeatureServiceConfiguration, cache: FeatureServiceCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[PeerGroupFeature])
  private val peerGroupDao = new PeerGroupDao(conf)
  private val peerGroupLabelDao = new PeerGroupLabelDao(conf)
  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)

  @GET
  @Timed
  @ApiOperation(httpMethod = HttpMethod.GET,
    value = "Returns the requested number of peer group anomalies in specified Date and Time period.",
    response = classOf[Response])
  def getPeerGroupAnomalies(@ApiParam(value = "Start Date and Time", required = true) @QueryParam("startTime") startTime: String,
                            @ApiParam(value = "End Date and Time", required = true) @QueryParam("endTime") endTime: String,
                            @ApiParam(value = "Number of anomalies to be fetched", required = true) @QueryParam("topN") topN: Integer) = {

    val buf = peerGroupDao.getPeerGroupAnomalies(startTime, endTime, topN, cache)
    val peerGroupEntries = buf.map(x => scala.collection.mutable.Map[String, Any](
      "dateTime" -> x.get("dateTime"),
      "peerId" -> x.get("peerId"),
      "peerTotal" -> x.get("peerTotal"),
      "peerType" -> x.get("peerType"),
      "anomalyScore" -> x.get("anomalyScore"),
      "peerEntities" -> x.get("peerUsers"),
      "peerFeatures" -> x.get("peerFeatures"),
      "peerPosition" -> x.get("peerPosition"),
      "featureScores" -> x.get("featureScores"),
      "featureId" -> x.get("featureId"),
      "featureAnomalyScore" -> x.get("featureAnomalyScore"),
      "dateTime" -> x.get("dateTime"),
      "model" -> x.get("model"),
      "killchainId" -> x.get("killchainId"),
      "securityEventId" -> x.get("securityEventId"),
      "eventDescription" -> x.get("eventDescription"),
      "numberOfEntities" -> x.get("numberOfEntities")
    ))

    val a = mapper.writeValueAsString(peerGroupEntries)
    Response.ok(a).build()
  }

  @GET
  @Path("/model/{id}")
  @Timed
  @ApiOperation(httpMethod = HttpMethod.GET,
    value = "Returns the requested number of peer group anomalies in specified Date and Time period and Model Id.",
    response = classOf[Response])
  def getPeerGroupAnomaliesByModelId(@ApiParam(value = "Model Id", required = true) @PathParam("id") modelId: Int,
                                     @ApiParam(value = "Start Date and Time", required = true) @QueryParam("startTime") startTime: String,
                                     @ApiParam(value = "End Date and Time", required = true) @QueryParam("endTime") endTime: String,
                                     @ApiParam(value = "Number of anomalies to be fetched", required = true) @QueryParam("topN") topN: Integer) = {

    val buf = peerGroupDao.getPeerGroupAnomaliesByModelId(startTime, endTime, modelId, topN, cache)
    val peerGroupEntries = buf.map(x => scala.collection.mutable.Map[String, Any]("dateTime" -> x.get("dateTime"),
      "peerId" -> x.get("peerId"),
      "peerTotal" -> x.get("peerTotal"),
      "peerType" -> x.get("peerType"),
      "anomalyScore" -> x.get("anomalyScore"),
      "peerEntities" -> x.get("peerEntities"),
      "entityId" -> x.get("entityId"),
      "entityIps" -> x.get("entityIps"),
      "entityHostNames" -> x.get("entityHostNames"),
      "peerFeatures" -> x.get("peerFeatures"),
      "peerPosition" -> x.get("peerPosition"),
      "featureScores" -> x.get("featureScores"),
      "featureId" -> x.get("featureId"),
      "featureAnomalyScore" -> x.get("featureAnomalyScore"),
      "dateTime" -> x.get("dateTime"),
      "model" -> x.get("model"),
      "killchainId" -> x.get("killchainId"),
      "securityEventId" -> x.get("securityEventId"),
      "eventDescription" -> x.get("eventDescription"),
      "numberOfEntities" -> x.get("numberOfEntities")

    ))

    val a = mapper.writeValueAsString(peerGroupEntries)
    Response.ok(a).build()
  }


  @GET
  @Path("/{peerId}/model/{modelId}")
  @Timed
  @ApiOperation(httpMethod = HttpMethod.GET,
    value = "Returns the requested number of peer group anomalies for specified Peer Group, Model Id, in specified Date and Time period",
    response = classOf[Response])
  def getPeerGroupAnomaliesByPeerId(@ApiParam(value = "Peer Id", required = true) @PathParam("peerId") peerId: Int,
                                    @ApiParam(value = "Model Id", required = true) @PathParam("modelId") modelId: Int,
                                    @ApiParam(value = "Start Date and Time", required = true) @QueryParam("startTime") startTime: String,
                                    @ApiParam(value = "End Date and Time", required = true) @QueryParam("endTime") endTime: String,
                                    @ApiParam(value = "Number of anomalies to be fetched", required = true) @QueryParam("topN") topN: Integer) = {

    val buf = peerGroupDao.getPeerGroupAnomaliesFromId(startTime, endTime, modelId, peerId, topN, cache)
    val peerGroupEntries = buf.map(x => scala.collection.mutable.Map[String, Any]("dateTime" -> x.get("dateTime"),
      "peerId" -> x.get("peerId"),
      "peerTotal" -> x.get("peerTotal"),
      "peerType" -> x.get("peerType"),
      "anomalyScore" -> x.get("anomalyScore"),
      "peerEntities" -> x.get("peerEntities"),
      "peerFeatures" -> x.get("peerFeatures"),
      "peerPosition" -> x.get("peerPosition"),
      "featureScores" -> x.get("featureScores"),
      "featureId" -> x.get("featureId"),
      "featureAnomalyScore" -> x.get("featureAnomalyScore"),
      "dateTime" -> x.get("dateTime"),
      "model" -> x.get("model"),
      "killchainId" -> x.get("killchainId"),
      "securityEventId" -> x.get("securityEventId"),
      "eventDescription" -> x.get("eventDescription"),
      "numberOfEntities" -> x.get("numberOfEntities")
    ))

    val a = mapper.writeValueAsString(peerGroupEntries)
    Response.ok(a).build()
  }

  @GET
  @Path("/entities")
  @Timed
  @ApiOperation(httpMethod = HttpMethod.GET,
    value = "Returns the peer group entities for specified Peer Group and in specified Date and Time period.",
    response = classOf[Response])
  def getPeerGroupEntitiesParallel(@ApiParam(value = "Start Date and Time", required = true) @QueryParam("startTime") startTime: String,
                                   @ApiParam(value = "End Date and Time", required = true) @QueryParam("endTime") endTime: String,
                                   @ApiParam(value = "Peer Id", required = true) @QueryParam("id") id: Integer) = {


    val buf = peerGroupDao.getPeerEntityProperties(startTime, endTime, id)

    var entries = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    entries = buf.map(x => scala.collection.mutable.Map[String, Any](
      "dateTime" -> x.get("dateTime"),
      "ipAddress" -> x.get("ipAddress"),
      "macAddress" -> x.get("macAddress"),
      "country" -> (if (x.get("country").get == "None") {
        null
      } else {
        x.get("country").get
      }),
      "city" -> (if (x.get("city").get == "None") {
        null
      } else {
        x.get("city").get
      }),
      "os" -> (if (x.get("os").get == "N/A") {
        null
      } else {
        x.get("os").get
      }),
      "browserName" -> (if (x.get("browserName").get == "N/A") {
        null
      } else {
        x.get("browserName").get
      }),
      "browserVersion" -> (if (x.get("browserVersion").get == "N/A") {
        null
      } else {
        x.get("browserVersion").get
      }),
      "hostName" -> (if (x.get("hostName").get == "") {
        null
      } else {
        x.get("hostName").get
      }),
      "primaryUserId" -> x.get("primaryUserId")
    ))

    val a = mapper.writeValueAsString(entries)
    Response.ok(a).build()
  }

  @GET
  @Path("/labels")
  @Timed
  @ApiOperation(httpMethod = HttpMethod.GET,
    value = "Returns the peer group feature details like Label, Id, Anomaly Name and Model Id.",
    response = classOf[Response])
  def getPeerGroupLabels() = {

    val buf = peerGroupLabelDao.getPeerGroupLabels(cache)
    val peerGroupEntries = buf.map(x => scala.collection.mutable.Map[String, Any](
      "featureId" -> x.get("featureId"),
      "featureLabel" -> x.get("featureLabel"),
      "featureAnomalyName" -> x.get("featureAnomalyName"),
      "modelId" -> x.get("modelId")
    ))

    val a = mapper.writeValueAsString(peerGroupEntries)
    Response.ok(a).build()
  }

  @GET
  @Path("{peerId}/model/{modelId}/entities")
  @Timed
  @ApiOperation(httpMethod = HttpMethod.GET,
    value = "Returns the peer group entities for specified Peer Group and Model Id in specified Date and Time period.",
    response = classOf[Response])
  def getPeerProperties(@PathParam("peerId") peerId: Int, @PathParam("modelId") modelId: Int,
                        @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) = {


    val buf = peerGroupDao.getPeerPropertiesForModel(startTime, endTime, peerId, modelId)
    var entries = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    if (modelId.equals(Constants.ADPeerAnomaliesModelTuple._1)) {
      entries = buf.map(x => scala.collection.mutable.Map[String, Any](
        "userName" ->{ if(x.contains("userName") && !x.get("userName").equals(Some(null))) x.get("userName") else  null },
        "fullName" -> { if(x.contains("fullName") && !x.get("fullName").equals(Some(null))) x.get("fullName") else  null },
        "accountType" -> { if(x.contains("accountType") && !x.get("accountType").equals(Some(null))) x.get("accountType") else  null },
        "securityId" -> { if(x.contains("securityId") && !x.get("securityId").equals(Some(null))) x.get("securityId") else  null },
        "isCritical" -> { if(x.contains("isCritical") && !x.get("isCritical").equals(Some(null))) x.get("isCritical") else  null },
        "jobTitle" -> { if(x.contains("jobTitle") && !x.get("jobTitle").equals(Some(null))) x.get("jobTitle") else  null },
        "email" -> { if(x.contains("email") && !x.get("email").equals(Some(null))) x.get("email") else  null },
        "location" -> { if(x.contains("location") && !x.get("location").equals(Some(null))) x.get("location") else  null },
        "department" ->  { if(x.contains("department") && !x.get("department").equals(Some(null))) x.get("department") else  null },
        "manager" -> { if(x.contains("manager") && !x.get("manager").equals(Some(null))) x.get("manager") else  null },
        "primaryHost" ->  { if(x.contains("primaryHost") && !x.get("primaryHost").equals(Some(null))) x.get("primaryHost") else  null },
        "creationDate" -> { if(x.contains("creationDate") && !x.get("creationDate").equals(Some(null))) x.get("creationDate") else  null },
        "lastModificationDate" ->  { if(x.contains("lastModificationDate") && !x.get("lastModificationDate").equals(Some(null))) x.get("lastModificationDate") else  null },
        "passwordLastSetDate" -> { if(x.contains("passwordLastSetDate") && !x.get("passwordLastSetDate").equals(Some(null))) x.get("passwordLastSetDate") else  null },
        "lastLogonData" -> { if(x.contains("lastLogonDate") && !x.get("lastLogonDate").equals(Some(null))) x.get("lastLogonDate") else  null },
        "risk" -> { if(x.contains("risk") && !x.get("risk").equals(Some(null))) x.get("risk") else  null }
      ))
    }else if(modelId.equals(Constants.WebPeerAnomaliesModelTuple._1)){
      entries = buf.map(x => scala.collection.mutable.Map[String, Any](
          "dateTime" -> { if(x.contains("dateTime") && !x.get("dateTime").equals(Some(null))) x.get("dateTime") else  null },
          "ipAddress" -> { if(x.contains("ipAddress") && !x.get("ipAddress").equals(Some(null))) x.get("ipAddress") else  null },
          "macAddress" ->  { if(x.contains("macAddress") && !x.get("macAddress").equals(Some(null))) x.get("macAddress") else  null },
          "country" -> { if(x.contains("country") && !x.get("country").equals(Some(null))) x.get("country") else  null },
          "city" -> { if(x.contains("city") && !x.get("city").equals(Some(null))) x.get("city") else  null },
          "os" -> { if(x.contains("os") && !x.get("os").equals(Some(null))) x.get("os") else  null },
          "browserName" -> { if(x.contains("browserName") && !x.get("browserName").equals(Some(null))) x.get("browserName") else  null },
          "browserVersion" -> { if(x.contains("browserVersion") && !x.get("browserVersion").equals(Some(null))) x.get("browserVersion") else  null },
          "hostName" -> { if(x.contains("hostName") && !x.get("hostName").equals(Some(null))) x.get("hostName") else  null },
          "primaryUserId" -> { if(x.contains("primaryUserId") && !x.get("primaryUserId").equals(Some(null))) x.get("primaryUserId") else  null },
          "risk" -> { if(x.contains("risk") && !x.get("risk").equals(Some(null))) x.get("risk") else  null }
      ))
    }

    val a = mapper.writeValueAsString(entries)
    Response.ok(a).build()
  }

}
