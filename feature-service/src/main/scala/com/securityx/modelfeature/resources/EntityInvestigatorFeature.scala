package com.securityx.modelfeature.resources

import javax.validation.Valid
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs._

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.cache.AutoCompleteCache
import com.securityx.modelfeature.common.inputs.{TaniumEntityCardInput, QueryJson}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{EntityInvestigatorDao, DetectorHomeDao}
import com.wordnik.swagger.annotations.{ApiResponse, ApiResponses, ApiOperation}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

/**
 * Created by harish on 6/16/15.
 */
@Path ("/entityinvestigator")
@Produces(Array(MediaType.APPLICATION_JSON))
class EntityInvestigatorFeature(val mapper:ObjectMapper, val conf:FeatureServiceConfiguration, val cache: FeatureServiceCache,
                                val autoCompleteCache: AutoCompleteCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[EntityInvestigatorFeature])
  private val detectorHomeDao = new DetectorHomeDao(conf)
  private val entityInvestigatorDao = new EntityInvestigatorDao(conf)


  @Path("/timeseries/default")
  @GET
  @Timed
  def getMedianRiskScoreBasedTimeSeriesForEntity(@QueryParam ("startTime") startTime: String,
                                                 @QueryParam("endTime") endTime: String) ={
    val buf = detectorHomeDao.getMedianRiskScoreBasedTimeSeries(startTime, endTime)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * A post which takes a QueryJson object in the post body and returns a list of dates and scores for the parameters
   * in the QueryJson object. The QueryJson object contains a start and end time, plus a set of fields and values to
   * query for.  Other possible QueryJson parameters are a limit to the number of results, a field to sort on, order in
   * which to sort, etc.
   *
   * @param input QueryJson object containing details on the query to be executed
   * @return a list of maps, one map per day with a maximum risk score and a sub list of maps including details
   */
  @Path("/timeseries")
  @POST
  @Timed
  @ApiOperation(value = "Query for timeseries data based on the query specified in a QueryJson object passed in the post body",
    httpMethod = "POST", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps, one per day, each containing a day, a maximum risk score, and " +
    "a sub-list of maps with details"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getRiskScoreBasedTimeSeriesForEntity(@Valid input: QueryJson) ={
    val buf: List[mutable.Map[String, Any]] = detectorHomeDao.getRiskScoreBasedTimeSeriesForEntity(input, cache)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Get the properties for a given entity during a given time period.  Returns a list containing a single map, which has three submaps,
   * one each for ip properties, user name properties and host name properties.  If a given call does not pass in one of the three
   * entity parameters, the map for that kind of properties will be empty.
   *
   * @param startTime the starttime for the period for which data will be returned (required)
   * @param endTime the endtime for the period for which data will be returned (required)
   * @param entityIp ip of the entity to get properties for
   * @param entityUserName username of the entity to get properties for
   * @param entityHostName hostname of the entity to get properties for
   * @return a list containing a map with submaps for properties for each of the entity parameters
   */
  @Path("/entityproperties")
  @GET
  @Timed
  @ApiOperation(value = "Get properties during a given time period for a given entity.  The entity is specified by ip address, host name and user name",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list containing a map containing three submaps, one each for ipProperties, " +
    "hostProperties and userProperties.  The results will be the same as those returned by /entityinvestigator/entityproperties/singleview," +
    "except that the properties will be separated into the three submaps"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEntityProperties(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                          @QueryParam("ipAddress") entityIp: String, @QueryParam("userName") entityUserName: String,
                          @QueryParam("hostName") entityHostName: String) ={
    val buf = entityInvestigatorDao.getEntityProperties(entityIp, entityHostName, entityUserName, startTime, endTime)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Get the properties for a given entity during a given time period.  Returns a list containing a single map with the properties from each of the
   * three entity paramaters. In cases where an entity does not have a value for a given property, there will still be an entry whose value will be
   * the empty string.
   *
   * @param startTime the starttime for the period for which data will be returned (required)
   * @param endTime the endtime for the period for which data will be returned (required)
   * @param entityId id of the entity to get properties for
   * @return a list containing a map with properties for each of the entity parameters
   */
  @Path("/entityproperties/byEntityId")
  @GET
  @Timed
  @ApiOperation(value = "Get properties during a given time period for a given entity.  The entity is specified by entity id",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list containing a map containing an entry for each property of the entity. The entry will consist of a list " +
    "of property entries, where each entry consists of a value for the property, the date when the value was set and the source for the value.  The list provides the history " +
    "of changes to the property over the time range specified by startTime and endTime"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEntityPropertiesByEntityId(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                                    @QueryParam("entityId") entityId: String) ={
    val buf = entityInvestigatorDao.getEntityPropertiesByEntityId(entityId, startTime, endTime)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Get related entity threats for the passed in entity.
   *
   * @param startTime the starttime for the period for which data will be returned (required)
   * @param endTime the endtime for the period for which data will be returned (required)
   * @param entityId id of the entity for which we should get related entities (required)
   * @return a list containing maps with information on related entities
   */
  @Path("/relatedEntities")
  @GET
  @Timed
  @ApiOperation(value = "Get entity threat info on the highest threat for every entity related to the passed in entity",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list containing threat info for each related entity"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getRelatedEntities(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                                    @QueryParam("entityId") entityId: String) ={
    val buf = entityInvestigatorDao.getRelatedEntities(startTime, endTime, entityId, cache)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Get related entity threats for the passed in entity list.
   *
   * @param startTime the starttime for the period for which data will be returned (required)
   * @param endTime the endtime for the period for which data will be returned (required)
   * @param entityIds comma separated list of entity ids for which we should get related entities (required)
   * @return a map containing an entry for every entity in the entityIds list, containing the highest
   *         threat (if any) for each related entity.
   */
  @Path("/relatedEntityList")
  @GET
  @Timed
  @ApiOperation(value = "Get properties during a given time period for a given entity.  The entity is specified by entity id",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A map containing an entry for each entity in the passed " +
    "in entity list. The value for each entity will be a list of threat info for any entities related to that entity " +
    "over the time range specified by startTime and endTime"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getRelatedEntityList(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                           @QueryParam("entityIds") entityIds: String) ={
    val buf = entityInvestigatorDao.getRelatedEntityList(startTime, endTime, entityIds, cache)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Get the properties for a given entity during a given time period.  Returns a list containing a single map with the properties from each of the
   * three entity paramaters. In cases where an entity does not have a value for a given property, there will still be an entry whose value will be
   * the empty string.
   *
   * @param startTime the starttime for the period for which data will be returned (required)
   * @param endTime the endtime for the period for which data will be returned (required)
   * @param entityIp ip of the entity to get properties for
   * @param entityUserName username of the entity to get properties for
   * @param entityHostName hostname of the entity to get properties for
   * @return a list containing a map with properties for each of the entity parameters
   */
  @Path("/entityproperties/singleview")
  @GET
  @Timed
  @Deprecated // This api is replaced by the /entityinvestigator/entityproperties/byEntityId api
  @ApiOperation(value = "Get properties during a given time period for a given entity.  The entity is specified by ip address, host name and user name",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list containing a map containing three submaps, one each for ipProperties, " +
    "hostProperties and userProperties.  The results will be the same as those returned by /entityinvestigator/entityproperties," +
    "except that the properties will be flattened into a single map"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEntityPropertiesSingleView(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                          @QueryParam("ipAddress") entityIp: String, @QueryParam("userName") entityUserName: String,
                          @QueryParam("hostName") entityHostName: String) ={
    val buf = entityInvestigatorDao.getEntityPropertiesSingleView(entityIp, entityHostName, entityUserName, startTime, endTime)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Get custom behavior properties during a given time period for a given ip, host and/or user (though the current implementation only
   * uses the ip) and behavior types. Custom behavior properties are counts of how often certain conditions have been detected on the entity.  The conditions
   * are scan detection, flood detection, url filtering, spyware phone home, spyware download, vulerability exploit detection,
   * and data filtering detection.
   *
   * @param startTime the starttime for the period for which data will be returned (required)
   * @param endTime the endtime for the period for which data will be returned (required)
   * @param entityIp ip address to check (may not be null)
   * @param entityUserName user name to check (may be null)
   * @param entityHostName host name to check (may be null)
    * @param behaviorTypes behavior types
   * @return a list of maps containing the alert properties
   */
  @Path("/entityproperties/customBehavior")
  @GET
  @Timed
  @ApiOperation(value = "Get custom behavior properties for a given ip, host and/or user during a given period of time",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing information about the indicated ip, host or user. The information" +
    "will be counts of times that alerts have been detected for such things as spyware download, spyware phone home, scan detection, etc."),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEntityCustomBehavior(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                          @QueryParam("ipAddress") entityIp: String, @QueryParam("userName") entityUserName: String,
                          @QueryParam("hostName") entityHostName: String, @QueryParam("behaviorTypes") behaviorTypes: String ) ={
    val buf = entityInvestigatorDao.getEntityCustomBehaviors(entityIp, entityHostName, entityUserName, startTime, endTime, behaviorTypes)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Gets the properties for a given host, process and md5 during the given time period
   *
   * @param startTime The start time for the period from which data will be returned
   * @param endTime The end time for the period from which data will be returned
   * @param md5 Used in getting the process properties
   * @param processName Used in getting the process properties
   * @param entityHostName Used in getting host, risk score and process properties
   * @return a map containing three sub-maps, one each for host, risk score and process properties
   */
  @Path("/endpointProperties")
  @GET
  @Timed
  @Deprecated // This api is replaced by the /entityinvestigator/endpointPropertiesByEntityId api
  @ApiOperation(value = "Api for getting properties for a host, process and md5 during a given time period",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A map containing information about any matching entity cards. The map will include child maps for host properties, " +
    "risk scores and process properties"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEndpointEntityProperties(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                                  @QueryParam("md5") md5: String, @QueryParam("processName") processName: String,
                                  @QueryParam("hostName") entityHostName: String)={
    val buf = entityInvestigatorDao.getEndpointEntityProperties(entityHostName, md5, processName, startTime, endTime)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Gets the properties for a given entity, process and md5 during the given time period.  The entity is assumed to
   * be a host entity.
   *
   * @param startTime The start time for the period from which data will be returned
   * @param endTime The end time for the period from which data will be returned
   * @param md5 Used in getting the process properties
   * @param processName Used in getting the process properties
   * @param entityId Used in getting host, risk score and process properties
   * @return a map containing three sub-maps, one each for host, risk score and process properties
   */
  @Path("/endpointPropertiesByEntityId")
  @GET
  @Timed
  @ApiOperation(value = "Api for getting properties for an entity, process and md5 during a given time period",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A map containing information about any matching entity cards. The map will include child maps for host properties, " +
    "risk scores and process properties"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEndpointPropertiesByEntityId(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                                  @QueryParam("md5") md5: String, @QueryParam("processName") processName: String,
                                  @QueryParam("entityId") entityId: String)={
    val buf = entityInvestigatorDao.getEndpointEntityPropertiesByEntityId(entityId, md5, processName, startTime, endTime)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }
}
