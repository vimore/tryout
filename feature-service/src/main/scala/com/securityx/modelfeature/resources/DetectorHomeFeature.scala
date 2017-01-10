package com.securityx.modelfeature.resources

import java.util
import javax.validation.Valid
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.EntityModelInfo
import com.securityx.modelfeature.common.cache.AutoCompleteCache
import com.securityx.modelfeature.common.inputs.{TaniumEntityCardInput, QueryJson}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{DetectorHomeDao, TaniumStatsDao}
import com.wordnik.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

@Path ("/detector")
@JsonInclude(Include.NON_NULL)
@Produces(Array(MediaType.APPLICATION_JSON))
class DetectorHomeFeature (val mapper:ObjectMapper, val conf:FeatureServiceConfiguration, val cache: FeatureServiceCache,
                           val autoCompleteCache: AutoCompleteCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[DetectorHomeFeature])
  private val parser: DateTimeFormatter = ISODateTimeFormat.dateTimeParser()
  private var detectorHomeDao = new DetectorHomeDao(conf)
  private val taniumStatsDao = new TaniumStatsDao(conf)

  @GET
  @Timed
  def getEntityRiskScores(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String, @QueryParam("topN") topN : Integer) = {

    val buf = detectorHomeDao.getEntityAllRiskScores(startTime, endTime, topN)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getEntityAllRiskScores", buf.size, buf.toString())
    val a = mapper.writeValueAsString(buf)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()
  }


  @Path("/getRiskyEntities")
  @POST
  @Timed
  @ApiOperation(value = "Api for searching for threats.  Allows search by risk range, and by entity properties (e.g. host name, user name, ip " +
    "and mac address",
    httpMethod = "POST", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing information about entities and their highest risk threat"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getRiskyEntities(@Valid input: QueryJson) ={
    val buf = detectorHomeDao.getEntityScores(input, returnEntityIds = true, cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "ipAddress" -> {if(x.contains("ipAddress")) x("ipAddress") else "" },
      "hostName" -> {if(x.contains("hostName")) x("hostName") else "" },
      "userName" -> {if(x.contains("userName")) x("userName") else "" },
      "currentScore" -> {if(x.contains("currentScore")) x("currentScore") else "" },
      "macAddress" ->  {if(x.contains("macAddress")) x("macAddress") else "" },
      "entityId" ->  {if(x.contains("entityId")) x("entityId") else "" },
      "entityType" ->  {if(x.contains("entityType")) x("entityType") else "" },
      "modelScores" -> {if(x.contains("modelScores")) x("modelScores") else "" },
      "processes" -> {if(x.contains("processes")) x("processes") else "" },
      "md5s" -> {if(x.contains("md5s")) x("md5s") else "" },
      "currentSecurityEventId" -> {if(x.contains("currentSecurityEventId")) x("currentSecurityEventId") else "" },
      "currentModelId" -> {if(x.contains("currentModelId")) x("currentModelId") else "" },
      "currentCardId" -> {if(x.contains("currentCardId")) x("currentCardId") else "" },
      "currentDateTime" -> {if(x.contains("currentDateTime")) x("currentDateTime") else "" }
    ))
    val a = mapper.writeValueAsString(categories)
//    val a = "[{\"modelScores\":[{\"killchainId\":0,\"featureLabel\":\"Global Newly Observed Process\",\"typePrefix\":\"Global Endpoint\",\"eventDescription\":\"Global Newly Observed Process\",\"shortDescription\":\"Global Newly Observed Process\",\"cardId\":6,\"risk\":1.0,\"dateTime\":\"2016-02-03T00:00:00.000Z\",\"securityEventTypeId\":0,\"eventType\":\"Global Newly Observed Process\",\"model\":10},{\"killchainId\":0,\"featureLabel\":\"Local Newly Observed Process\",\"typePrefix\":\"Local Endpoint\",\"eventDescription\":\"Local Newly Observed Process\",\"shortDescription\":\"Local Newly Observed Process\",\"cardId\":6,\"risk\":0.7773,\"dateTime\":\"2016-02-03T00:00:00.000Z\",\"securityEventTypeId\":0,\"eventType\":\"Local Newly Observed Process\",\"model\":9}],\"macAddress\":\"12:7F:C8:56:84:17\",\"entityId\":\"e-12345\",\"entityType\":\"host\",\"ipAddress\":\"192.168.12.27\",\"currentSecurityEventId\":0,\"userName\":\"\",\"currentDateTime\":\"2016-02-03T00:00:00.000Z\",\"currentCardId\":6,\"processes\":\"\",\"currentScore\":1.0,\"md5s\":\"\",\"currentModelId\":10,\"hostName\":\"RDP-GW\"},{\"modelScores\":[{\"killchainId\":3,\"featureLabel\":\"features_4625_dstusr_logon_type\",\"typePrefix\":\"AD\",\"eventDescription\":\"This user failed to authenticate for an unusual reason\",\"shortDescription\":\"This user failed to authenticate for an unusual reason\",\"cardId\":7,\"risk\":0.98,\"dateTime\":\"2016-02-02T00:00:00.000Z\",\"securityEventTypeId\":502,\"eventType\":\"Anomalous User Activity\",\"model\":7}],\"macAddress\":\"E4-EB-C9-03-9B-41\",\"entityId\":\"e-11115\",\"entityType\":\"user\",\"ipAddress\":\"192.168.1.174\",\"currentSecurityEventId\":502,\"userName\":\"e\\\\8\\\\jdoe\",\"currentDateTime\":\"2016-02-02T00:00:00.000Z\",\"currentCardId\":7,\"processes\":\"\",\"currentScore\":0.98,\"md5s\":\"\",\"currentModelId\":7,\"hostName\":\"laptop874\"},{\"modelScores\":[{\"killchainId\":3,\"featureLabel\":\"features_4624_highly_active_dest_user\",\"typePrefix\":\"IAM\",\"eventDescription\":\"This user successfuly authenticated an unusual number of times\",\"shortDescription\":\"This user successfuly authenticated an unusual number of times\",\"cardId\":1,\"risk\":0.87337,\"dateTime\":\"2016-02-01T00:00:00.000Z\",\"securityEventTypeId\":8,\"eventType\":\"Anomalous User Activity\",\"model\":3}],\"macAddress\":\"7C-12-F1-53-5D-76\",\"entityId\":\"e-54321\",\"entityType\":\"user\",\"ipAddress\":\"192.168.1.150\",\"currentSecurityEventId\":8,\"userName\":\"e\\\\8\\\\jdoe\",\"currentDateTime\":\"2016-02-01T00:00:00.000Z\",\"currentCardId\":1,\"processes\":\"\",\"currentScore\":0.87337,\"md5s\":\"\",\"currentModelId\":3,\"hostName\":\"laptop850\"},{\"modelScores\":[{\"killchainId\":1,\"featureLabel\":\"features_4625_highly_active_dest_user_failure\",\"typePrefix\":\"IAM\",\"eventDescription\":\"This user failed to authenticate an unusual number of times\",\"shortDescription\":\"This user failed to authenticate an unusual number of times\",\"cardId\":1,\"risk\":0.7135,\"dateTime\":\"2016-02-01T00:00:00.000Z\",\"securityEventTypeId\":14,\"eventType\":\"Brute Force Activity\",\"model\":3}],\"macAddress\":\"63-A6-D2-AB-F2-B2\",\"entityId\":\"e-112233\",\"entityType\":\"user\",\"ipAddress\":\"192.168.1.151\",\"currentSecurityEventId\":14,\"userName\":\"kenbrell_thompkins\",\"currentDateTime\":\"2016-02-01T00:00:00.000Z\",\"currentCardId\":1,\"processes\":\"\",\"currentScore\":0.7135,\"md5s\":\"\",\"currentModelId\":3,\"hostName\":\"laptop851\"}]"
    Response.ok(a).build()
  }

  @Path("/home")
  @POST
  @Timed
  @Deprecated
  // This api is the pre-hbase-entity-fusion api and is replaced by the /detector/getRiskyEnities api
  def getEntityRiskInfo(@Valid input: QueryJson) ={
    val buf = detectorHomeDao.getEntityScores(input, returnEntityIds = false, cache)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getEntityScores", buf.size, buf.toString())
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "ipAddress" -> {if(x.contains("ipAddress")) x("ipAddress") else "" },
      "hostName" -> {if(x.contains("hostName")) x("hostName") else "" },
      "userName" -> {if(x.contains("userName")) x("userName") else "" },
      "currentScore" -> {if(x.contains("currentScore")) x("currentScore") else "" },
      "macAddress" ->  {if(x.contains("macAddress")) x("macAddress") else "" },
      "modelScores" -> {if(x.contains("modelScores")) x("modelScores") else "" },
      "processes" -> {if(x.contains("processes")) x("processes") else "" },
      "md5s" -> {if(x.contains("md5s")) x("md5s") else "" },
      "currentSecurityEventId" -> {if(x.contains("currentSecurityEventId")) x("currentSecurityEventId") else "" },
      "currentModelId" -> {if(x.contains("currentModelId")) x("currentModelId") else "" },
      "currentCardId" -> {if(x.contains("currentCardId")) x("currentCardId") else "" },
      "currentDateTime" -> {if(x.contains("currentDateTime")) x("currentDateTime") else "" }
    ))
    val a = mapper.writeValueAsString(categories)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()

  }

  @Path("/entitySearch")
  @POST
  @Timed
  @ApiOperation(value = "Api for searching for threats.  Allows search by risk range, and by entity properties (e.g. host name, user name, ip " +
    "and mac address",
    httpMethod = "POST", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing information about entities and their highest risk threat"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def entitySearch(@Valid input: QueryJson) ={
    val buf = detectorHomeDao.getSearchedEntityScores(input, returnEntityIds = true, cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "ipAddress" -> {if(x.contains("ipAddress")) x("ipAddress") else "" },
      "hostName" -> {if(x.contains("hostName")) x("hostName") else "" },
      "userName" -> {if(x.contains("userName")) x("userName") else "" },
      "currentScore" -> {if(x.contains("currentScore")) x("currentScore") else "" },
      "macAddress" ->  {if(x.contains("macAddress")) x("macAddress") else "" },
      "entityId" ->  {if(x.contains("entityId")) x("entityId") else "" },
      "entityType" ->  {if(x.contains("entityType")) x("entityType") else "" },
      "modelScores" -> {if(x.contains("modelScores")) x("modelScores") else "" },
      "processes" -> {if(x.contains("processes")) x("processes") else "" },
      "md5s" -> {if(x.contains("md5s")) x("md5s") else "" },
      "currentSecurityEventId" -> {if(x.contains("currentSecurityEventId")) x("currentSecurityEventId") else "" },
      "currentModelId" -> {if(x.contains("currentModelId")) x("currentModelId") else "" },
      "currentCardId" -> {if(x.contains("currentCardId")) x("currentCardId") else "" },
      "currentDateTime" -> {if(x.contains("currentDateTime")) x("currentDateTime") else "" }
    ))
    val a = mapper.writeValueAsString(categories)
//    val a = "[{\"entityId\":\"e2\",\"ipAddress\":\"192.168.1.151\",\"macAddress\":\"63-A6-D2-AB-F2-B2\",\"modelScores\":[{\"killchainId\":1,\"featureLabel\":\"features_4625_highly_active_dest_user_failure\",\"typePrefix\":\"IAM\",\"eventDescription\":\"This user failed to authenticate an unusual number of times\",\"shortDescription\":\"This user failed to authenticate an unusual number of times\",\"cardId\":1,\"risk\":0.7135,\"dateTime\":\"2016-02-01T00:00:00.000Z\",\"securityEventTypeId\":14,\"eventType\":\"Brute Force Activity\",\"model\":3}],\"currentSecurityEventId\":14,\"userName\":\"kenbrell_thompkins\",\"currentDateTime\":\"2016-02-01T00:00:00.000Z\",\"processes\":\"\",\"currentCardId\":1,\"currentScore\":0.7135,\"entityType\":\"user\",\"md5s\":\"\",\"hostName\":\"laptop851\",\"currentModelId\":3}]"
    Response.ok(a).build()
  }

  @Path("/home/search")
  @POST
  @Timed
  @Deprecated
  // This api is the pre-hbase-entity-fusion api and is replaced by the /detector/entitySearch api
  def getSearchedEntityRiskInfo(@Valid input: QueryJson) ={
    val buf = detectorHomeDao.getSearchedEntityScores(input, returnEntityIds = false, cache)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getSearchedEntityScores", buf.size, buf.toString())
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "ipAddress" -> {if(x.contains("ipAddress")) x("ipAddress") else "" },
      "hostName" -> {if(x.contains("hostName")) x("hostName") else "" },
      "userName" -> {if(x.contains("userName")) x("userName") else "" },
      "currentScore" -> {if(x.contains("currentScore")) x("currentScore") else "" },
      "macAddress" ->  {if(x.contains("macAddress")) x("macAddress") else "" },
      "modelScores" -> {if(x.contains("modelScores")) x("modelScores") else "" },
      "processes" -> {if(x.contains("processes")) x("processes") else "" },
      "md5s" -> {if(x.contains("md5s")) x("md5s") else "" },
      "currentSecurityEventId" -> {if(x.contains("currentSecurityEventId")) x("currentSecurityEventId") else "" },
      "currentModelId" -> {if(x.contains("currentModelId")) x("currentModelId") else "" },
      "currentCardId" -> {if(x.contains("currentCardId")) x("currentCardId") else "" },
      "currentDateTime" -> {if(x.contains("currentDateTime")) x("currentDateTime") else "" }
    ))
    val a = mapper.writeValueAsString(categories)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()

  }

  /**
   * Used to get behavior information for a specific entry on the Entity Behavior Graph.
   *
   * @param input QueryJson object specifying details on the query to run: start and end times, model and security
   *              event id to be searched for, and a list of excluded entities.
   * @return a map containing hostName, userName, ip address, mac address, entity id and entity type for
   *         entities matching the search criteria.
   */
  @Path("/entitiesByBehavior")
  @POST
  @Timed
  def getEntityInfoByBehavior(@Valid input: QueryJson) ={
    val buf = detectorHomeDao.getTopNRiskyEntitiesByBehavior(input, cache)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getTopNRiskyEntitiesByBehavior", buf.size, buf.toString())
    val a = mapper.writeValueAsString(buf)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()

  }

  @Path("/home/killchain/counts")
  @GET
  def getKillchainCounts(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) ={
    val buf = detectorHomeDao.getKillchainCounts(startTime, endTime, cache)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getKillchainCounts", buf.size, buf.toString())
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "count" -> x.get("count")
    ))
    val a = mapper.writeValueAsString(categories)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()

  }

  /**
   * Gets the model scores for a specific entity id
   *
   * @param startTime The start time for the period from which data will be returned.  May not be null
   * @param endTime The end time for the period from which data will be returned.  May not be null
   * @param entityId The entity id for which we will get card details.  May not be null
   * @param lowerRisk Lower bound on the risk of details that should be returned.  May not be null
   * @param upperRisk Upper bound on the risk of details that should be returned.  May not be null
   * @return a list of maps containing the entity card details
   */
  @Path("/modelScoresById")
  @Timed
  @GET
  @ApiOperation(value = "Api for getting model scores for a given time range, risk range and entity id",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing information about any matching entity cards"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getModelScoresById(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String, @QueryParam("entityId") entityId: String,
                         @QueryParam("lowerRisk") lowerRisk: String, @QueryParam("upperRisk") upperRisk: String) ={
    if (entityId == null || entityId.isEmpty || startTime == null || startTime.isEmpty || endTime == null || endTime.isEmpty ||
      lowerRisk == null || lowerRisk.isEmpty || upperRisk == null || upperRisk.isEmpty) {
      val emptyParamList = checkParams(entityId, startTime, endTime, lowerRisk, upperRisk)
      Response.status(400).entity(emptyParamList + " must not be null").build()
    } else {
      var parseSuccess = true
      var lowerRiskDouble: Double = 0.0
      try {
        lowerRiskDouble = lowerRisk.toDouble
      } catch {
        case ex: Exception =>
          parseSuccess = false
          Response.status(400).entity("lowerRisk value must be a double value").build()
      }
      var upperRiskDouble: Double = 0.0
      try {
        upperRiskDouble = upperRisk.toDouble
      } catch {
        case ex: Exception =>
          parseSuccess = false
          Response.status(400).entity("upperRisk value must be a double value").build()
      }
      if (parseSuccess) {
        val modelScores = detectorHomeDao.getModelScoresById(entityId, startTime, endTime, lowerRiskDouble, upperRiskDouble, cache)
        val a = mapper.writeValueAsString(modelScores)
        Response.ok(a).build()
      }
    }
//    val a = "[{\"killchainId\":0,\"featureLabel\":\"Global Newly Observed Process\",\"typePrefix\":\"Global Endpoint\",\"eventDescription\":\"Global Newly Observed Process\",\"shortDescription\":\"Global Newly Observed Process\",\"cardId\":6,\"risk\":1.0,\"dateTime\":\"2016-02-03T00:00:00.000Z\",\"securityEventTypeId\":0,\"eventType\":\"Global Newly Observed Process\",\"model\":10},{\"killchainId\":0,\"featureLabel\":\"Local Newly Observed Process\",\"typePrefix\":\"Local Endpoint\",\"eventDescription\":\"Local Newly Observed Process\",\"shortDescription\":\"Local Newly Observed Process\",\"cardId\":6,\"risk\":0.7773,\"dateTime\":\"2016-02-03T00:00:00.000Z\",\"securityEventTypeId\":0,\"eventType\":\"Local Newly Observed Process\",\"model\":9}]"
//    Response.ok(a).build()
  }

  def checkParams(entityId: String, startTime: String, endTime: String, lowerRisk: String, upperRisk: String): String = {
    var hasValue = false
    var emptyParamList = ""
    if (entityId == null || entityId.isEmpty) {
      hasValue = true
      emptyParamList = "entityId"
    }
    if (startTime == null || startTime.isEmpty) {
      if (hasValue) {
        emptyParamList = emptyParamList + ", "
      }
      hasValue = true
      emptyParamList = emptyParamList + "startTime"
    }
    if (endTime == null || endTime.isEmpty) {
      if (hasValue) {
        emptyParamList = emptyParamList + ", "
      }
      hasValue = true
      emptyParamList = emptyParamList + "endTime"
    }
    if (lowerRisk == null || lowerRisk.isEmpty) {
      if (hasValue) {
        emptyParamList = emptyParamList + ", "
      }
      hasValue = true
      emptyParamList = emptyParamList + "lowerRisk"
    }
    if (upperRisk == null || upperRisk.isEmpty) {
      if (hasValue) {
        emptyParamList = emptyParamList + ", "
      }
      hasValue = true
      emptyParamList = emptyParamList + "upperRisk"
    }
    emptyParamList
  }

  /**
   * Gets the entity card details for a specific entity id
   *
   * @param startTime The start time for the period from which data will be returned.  May not be null
   * @param endTime The end time for the period from which data will be returned.  May not be null
   * @param entityId The entity id for which we will get card details.  May not be null
   * @param lowerRisk Lower bound on the risk of details that should be returned.  May not be null
   * @param upperRisk Upper bound on the risk of details that should be returned.  May not be null
   * @return a list of maps containing the entity card details
   */
  @Path("/entityCardsById")
  @Timed
  @GET
  @ApiOperation(value = "Api for getting entity card details for a given time range, risk range and entity id",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing information about any matching entity cards"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEntityCardsById(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String, @QueryParam("entityId") entityId: String,
                          @QueryParam("lowerRisk") lowerRisk: String, @QueryParam("upperRisk") upperRisk: String) ={
    if (entityId == null || entityId.isEmpty || startTime == null || startTime.isEmpty || endTime == null || endTime.isEmpty ||
      lowerRisk == null || lowerRisk.isEmpty || upperRisk == null || upperRisk.isEmpty) {
      val emptyParamList = checkParams(entityId, startTime, endTime, lowerRisk, upperRisk)
      Response.status(400).entity(emptyParamList + " must not be null").build()
    } else {
      var parseSuccess = true
      var lowerRiskDouble: Double = 0.0
      try {
        lowerRiskDouble = lowerRisk.toDouble
      } catch {
        case ex: Exception =>
          parseSuccess = false
          Response.status(400).entity("lowerRisk value must be a double value").build()
      }
      var upperRiskDouble: Double = 0.0
      try {
        upperRiskDouble = upperRisk.toDouble
      } catch {
        case ex: Exception =>
          parseSuccess = false
          Response.status(400).entity("upperRisk value must be a double value").build()
      }
      if (parseSuccess) {
        val groups = detectorHomeDao.getEntityCardsByEntityId(startTime, endTime, entityId, lowerRiskDouble, upperRiskDouble, cache)
        val answer = groups.map { case (key, value) =>
          Map(
            "key" -> key,
            "value" -> value.map {
              x => Map(
                "dateTime" -> x.get("dateTime"),
                "sourceIp" -> {
                  if (x.contains("sourceIp") && !x.get("sourceIp").equals(Some(null))) x.get("sourceIp") else "N/A"
                },
                "sourceUserName" -> {
                  if (x.contains("sourceUserName") && !x.get("sourceUserName").equals(Some(null))) x.get("sourceUserName") else "N/A"
                },
                "destinationIp" -> {
                  if (x.contains("destinationIp") && !x.get("destinationIp").equals(Some(null))) x.get("destinationIp") else "N/A"
                },
                "destinationUserName" -> {
                  if (x.contains("destinationUserName") && !x.get("destinationUserName").equals(Some(null))) x.get("destinationUserName") else "N/A"
                },
                "sourcePort" -> {
                  if (x.contains("sourcePort") && !x.get("sourcePort").equals(Some(null))) x.get("sourcePort") else "N/A"
                },
                "destinationPort" -> {
                  if (x.contains("destinationPort") && !x.get("destinationPort").equals(Some(null))) x.get("destinationPort") else "N/A"
                },
                "winEventType" -> {
                  if (x.contains("winEventType") && !x.get("winEventType").equals(Some(null))) x.get("winEventType") else "N/A"
                },
                "winEventId" -> {
                  if (x.contains("winEventId") && !x.get("winEventId").equals(Some(null))) x.get("winEventId") else "N/A"
                },
                "bytesIn" -> {
                  if (x.contains("bytesIn") && !x.get("bytesIn").equals(Some(null))) x.get("bytesIn") else "N/A"
                },
                "bytesOut" -> {
                  if (x.contains("bytesOut") && !x.get("bytesOut").equals(Some(null))) x.get("bytesOut") else "N/A"
                },
                "url" -> {
                  if (x.contains("url") && !x.get("url").equals(Some(null))) x.get("url") else "N/A"
                },
                "httpMethod" -> {
                  if (x.contains("httpMethod") && !x.get("httpMethod").equals(Some(null))) x.get("httpMethod") else "N/A"
                },
                "httpResponseCode" -> {
                  if (x.contains("httpResponseCode") && !x.get("httpResponseCode").equals(Some(null))) x.get("httpResponseCode") else "N/A"
                },
                "userAgent" -> {
                  if (x.contains("userAgent") && !x.get("userAgent").equals(Some(null))) x.get("userAgent") else "N/A"
                },
                "interval" -> {
                  if (x.contains("interval") && !x.get("interval").equals(Some(null))) x.get("interval") else "N/A"
                }, //applicable only for beacons
                "isDaily" -> {
                  if (x.contains("isDaily") && !x.get("isDaily").equals(Some(null))) x.get("isDaily") else true
                }, //applicable only for beacons
                "targetId" -> {
                  if (x.contains("targetId") && !x.get("targetId").equals(Some(null))) x.get("targetId") else "N/A"
                },
                "targetDescription" -> {
                  if (x.contains("targetDescription") && !x.get("targetDescription").equals(Some(null))) x.get("targetDescription") else "N/A"
                },
                "featureKeyCount" -> {
                  if (x.contains("featureKeyCount") && !x.get("featureKeyCount").equals(Some(null))) x.get("featureKeyCount") else "N/A"
                },
                "risk" -> {
                  if (x.contains("riskScore")) x.get("riskScore") else 0.0
                },
                "processes" -> {
                  if (x.contains("processes") && !x.get("processes").equals(Some(null))) x.get("processes") else "N/A"
                },
                "md5s" -> {
                  if (x.contains("md5s") && !x.get("md5s").equals(Some(null))) x.get("md5s") else "N/A"
                },
                "ports" -> {
                  if (x.contains("ports") && !x.get("ports").equals(Some(null))) x.get("ports") else "N/A"
                },
                "hosts" -> {
                  if (x.contains("hosts") && !x.get("hosts").equals(Some(null))) x.get("hosts") else "N/A"
                },
                "paths" -> {
                  if (x.contains("paths") && !x.get("paths").equals(Some(null))) x.get("paths") else "N/A"
                },
                "datesSeen" -> {
                  if (x.contains("datesSeen") && !x.get("datesSeen").equals(Some(null))) x.get("datesSeen") else "N/A"
                },
                "firstTimeSeen" -> {
                  if (x.contains("firstTimeSeen") && !x.get("firstTimeSeen").equals(Some(null))) x.get("firstTimeSeen") else "N/A"
                },
                "nox" -> {
                  if (x.contains("nox") && !x.get("nox").equals(Some(null))) x.get("nox") else "N/A"
                },
                "groupInffo" -> {
                  if (x.contains("groupInffo") && !x.get("groupInffo").equals(Some(null))) x.get("groupInffo") else "N/A"
                },
                "groupInfo" -> {
                  if (x.contains("groupInfo") && !x.get("groupInfo").equals(Some(null))) x.get("groupInfo") else "N/A"
                },
                "isAutorun" -> {
                  if (x.contains("isAutorun") && !x.get("isAutorun").equals(Some(null))) x.get("isAutorun") else "N/A"
                }

              )
            }
          )
        }

        val a = mapper.writeValueAsString(answer)
        Response.ok(a).build()
      }
    }
  }

  /**
   * Gets the entity card details as specified by the parameters. Except for the startTime and endTime
   * the other parameters are optional, but it is expected in general that one or more of them will have
   * some value.
   *
   * @param startTime The start time for the period from which data will be returned
   * @param endTime The end time for the period from which data will be returned
   * @param entityIp The ip address for which data should be returned. May be null/empty
   * @param entityUserName The user name for which data should be returned.  May be be null/empty
   * @param entityHostName The host name for which data should be returned. May be null/empty.
   * @param entityMacAddress The mac address for which data should be returned.  Maybe be null/empty.
   * @return a list of maps containing the entity card details
   */
  @Path("/home/entityCards")
  @Timed
  @GET
  @Deprecated
  // This api is the pre-hbase-entity-fusion api and is replaced by the /detector/entityCardsById api
  @ApiOperation(value = "Api for getting entity card details for a given time range, ip address, username, hostname and/or mac address",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing information about any matching entity cards"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEntityCards(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                     @QueryParam("ipAddress") entityIp: String, @QueryParam("userName") entityUserName: String,
                     @QueryParam("hostName") entityHostName: String, @QueryParam("macAddress") entityMacAddress: String) ={
    val groups = detectorHomeDao.getEntityCards(startTime, endTime, entityIp, entityUserName,entityHostName, entityMacAddress, cache)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getEntityCards", groups.size, groups.toString())
    val answer = groups.map { case (key, value) =>
      Map(
        "key" -> key,
        "value" -> value.map {
          x => Map(
            "dateTime" ->  x.get("dateTime"),
            "sourceIp" ->  { if(x.contains("sourceIp") && !x.get("sourceIp").equals(Some(null))) x.get("sourceIp") else "N/A" },
            "sourceUserName" -> { if(x.contains("sourceUserName") && !x.get("sourceUserName").equals(Some(null)) ) x.get("sourceUserName") else "N/A" },
            "destinationIp" -> { if(x.contains("destinationIp") && !x.get("destinationIp").equals(Some(null))) x.get("destinationIp") else "N/A" },
            "destinationUserName" ->{ if(x.contains("destinationUserName") && !x.get("destinationUserName").equals(Some(null))) x.get("destinationUserName") else "N/A" },
            "sourcePort" -> { if(x.contains("sourcePort") && !x.get("sourcePort").equals(Some(null))) x.get("sourcePort") else "N/A" },
            "destinationPort" -> { if(x.contains("destinationPort") && !x.get("destinationPort").equals(Some(null))) x.get("destinationPort") else "N/A" },
            "winEventType" -> { if(x.contains("winEventType") && !x.get("winEventType").equals(Some(null))) x.get("winEventType") else "N/A" },
            "winEventId" -> { if(x.contains("winEventId") && !x.get("winEventId").equals(Some(null))) x.get("winEventId") else "N/A" },
            "bytesIn" -> { if(x.contains("bytesIn") && !x.get("bytesIn").equals(Some(null))) x.get("bytesIn") else "N/A" },
            "bytesOut" -> { if(x.contains("bytesOut") && !x.get("bytesOut").equals(Some(null))) x.get("bytesOut") else "N/A" },
            "url" -> { if(x.contains("url") && !x.get("url").equals(Some(null))) x.get("url") else "N/A" },
            "httpMethod" -> { if(x.contains("httpMethod") && !x.get("httpMethod").equals(Some(null))) x.get("httpMethod") else "N/A" },
            "httpResponseCode" -> { if(x.contains("httpResponseCode") && !x.get("httpResponseCode").equals(Some(null))) x.get("httpResponseCode") else "N/A" },
            "userAgent" -> { if(x.contains("userAgent") && !x.get("userAgent").equals(Some(null))) x.get("userAgent") else "N/A" },
            "interval" -> { if(x.contains("interval") && !x.get("interval").equals(Some(null))) x.get("interval") else "N/A" },     //applicable only for beacons
            "isDaily" -> { if(x.contains("isDaily") && !x.get("isDaily").equals(Some(null))) x.get("isDaily") else true },     //applicable only for beacons
            "targetId" -> { if(x.contains("targetId") && !x.get("targetId").equals(Some(null))) x.get("targetId") else "N/A" },
            "targetDescription" -> { if(x.contains("targetDescription") && !x.get("targetDescription").equals(Some(null))) x.get("targetDescription") else "N/A" },
            "featureKeyCount" -> { if(x.contains("featureKeyCount") && !x.get("featureKeyCount").equals(Some(null))) x.get("featureKeyCount") else "N/A" },
            "risk" -> { if(x.contains("riskScore")) x.get("riskScore") else 0.0 },
            "processes" ->  { if(x.contains("processes") && !x.get("processes").equals(Some(null))) x.get("processes") else "N/A" },
            "md5s" -> { if(x.contains("md5s") && !x.get("md5s").equals(Some(null))) x.get("md5s") else "N/A" },
            "ports" -> { if(x.contains("ports") && !x.get("ports").equals(Some(null))) x.get("ports") else "N/A" },
            "hosts" -> { if(x.contains("hosts") && !x.get("hosts").equals(Some(null))) x.get("hosts") else "N/A" },
            "paths" -> { if(x.contains("paths") && !x.get("paths").equals(Some(null))) x.get("paths") else "N/A" },
            "datesSeen" -> { if(x.contains("datesSeen") && !x.get("datesSeen").equals(Some(null))) x.get("datesSeen") else "N/A" },
            "firstTimeSeen" -> { if(x.contains("firstTimeSeen") && !x.get("firstTimeSeen").equals(Some(null))) x.get("firstTimeSeen") else "N/A" },
            "nox" -> { if(x.contains("nox") && !x.get("nox").equals(Some(null))) x.get("nox") else "N/A" },
            "groupInffo" ->  { if(x.contains("groupInffo") && !x.get("groupInffo").equals(Some(null))) x.get("groupInffo") else "N/A" },
            "groupInfo" ->  { if(x.contains("groupInfo") && !x.get("groupInfo").equals(Some(null))) x.get("groupInfo") else "N/A" },
            "isAutorun" ->   { if(x.contains("isAutorun") && !x.get("isAutorun").equals(Some(null))) x.get("isAutorun") else "N/A" }

          )
        }
      )
    }

    val a = mapper.writeValueAsString(answer)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()
  }

  @Path("/autocomplete")
  @Timed
  @GET
  def getAutoCompleteResults(@QueryParam("incomingString") incomingString: String,
                             @QueryParam("fieldName") fieldName: String) = {

    val resultStr: String = {

      if (fieldName != null && !fieldName.isEmpty) {

        val result: mutable.Buffer[String] = scala.collection.JavaConversions.asScalaBuffer(
          detectorHomeDao.getAutoCompleteResults(incomingString, fieldName, autoCompleteCache))
        LOGGER.debug("Fetched {} {} records from detectorHomeDao.getAutoCompleteResults", result.size, result.toString())
        mapper.writeValueAsString(result)

      } else {

        val result = detectorHomeDao.getAutoCompleteResultsOnAll(incomingString, autoCompleteCache)
        LOGGER.debug("Fetched {} {} records from detectorHomeDao.getAutoCompleteResultsOnAll", result.size, result.toString())
        mapper.writeValueAsString(result)

      }
    }
    LOGGER.debug("Returning values: {}", resultStr)
    Response.ok(resultStr).build()
  }


  @Path("/home/tanium")
  @GET
  @Timed
  @Deprecated
  // This code is not being updated as part of the endpoint bifurcation work. If we ever decide to call it again,
  // it will likely need work.
  def getTopNTaniumHosts(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String)={
    val buf = taniumStatsDao.getTaniumTopNHostsByRisk(startTime,endTime, 5, cache)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getTaniumTopNHostsByRisk", buf.size, buf.toString())
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "ipAddress" -> {if(x.contains("ipAddress")) x("ipAddress") else "" },
      "hostName" -> {if(x.contains("hostName")) x("hostName") else "" },
      "userName" -> {if(x.contains("userName")) x("userName") else "" },
      "currentScore" -> {if(x.contains("currentScore")) x("currentScore") else "" },
      "macAddress" ->  {if(x.contains("macAddress")) x("macAddress") else "" },
      "modelScores" -> {if(x.contains("modelScores")) x("modelScores") else "" },
      "processes" -> {if(x.contains("processes")) x("processes") else "" }
    ))
    val a = mapper.writeValueAsString(categories)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()

  }

  @Path("/home/taniumentitycards")
  @POST
  @Timed
  def geTaniumEntityCards(@Valid input: TaniumEntityCardInput)={
    val buf = taniumStatsDao.getTaniumEntityCards(input.getStartTime,input.getEndTime, input.getProcessList, input.getMd5List)
    LOGGER.debug("Fetched {} {} records from detectorHomeDao.getTaniumTopNHostsByRisk", buf.size, buf.toString())
    val a = mapper.writeValueAsString(buf)
    LOGGER.debug("Returning values: {}", a)
    Response.ok(a).build()
  }

  def setDetectorHomeDao(detectorHomeDao: DetectorHomeDao) = {
    this.detectorHomeDao = detectorHomeDao
  }
}
