package com.securityx.modelfeature.resources

import java.io.IOException
import java.util
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.core.Context

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.cache.FeatureResponseCache
import com.securityx.modelfeature.common.inputs.{EndPointAnalytics, TimeSeriesInput}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{SearchImpalaDao, TaniumStatsDao, TimeSeriesDao}
import com.securityx.modelfeature.utils.MiscUtils
import com.wordnik.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.joda.time.{DateTime, Days}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 12/26/14.
 */
@Path("/timeseries/")
@Produces(Array(MediaType.APPLICATION_JSON))
@JsonInclude(Include.NON_NULL)
class TimeSeriesFeature  (val mapper: ObjectMapper, val webProxySolrClient:CloudSolrServer,iamSolrClient: CloudSolrServer,
                          val taniumHostInfoSolrClient:CloudSolrServer, taniumHetSolrClient: CloudSolrServer, val taniumUetSolrClient:CloudSolrServer,
                          val conf: FeatureServiceConfiguration, cache: FeatureServiceCache, featureResponseCache: FeatureResponseCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[TimeSeriesFeature])
  private val timeSeriesDao = new TimeSeriesDao(conf)
  private val taniumStatsDao = new TaniumStatsDao(conf)


  @Path("/types")
  @GET
  @Timed
  @Deprecated
  @deprecated
  def getTypes(@QueryParam("modelId") modelId: Int, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) ={
    val result = timeSeriesDao.getTypes(modelId, startTime, endTime);
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  /**
   * Gets the summary information for the given model over the given time period.
   * The code may be called for the AD peer anomalies model, the web peer anomalies model and the endpoint models.  It is not
   * clear whether the UI actually calls it for the endpoint models, however.
   *
   * @param modelId model to get results for
   * @param startTime start date of the period for which data will be gotten
   * @param endTime end date of the period for which data will be gotten
   * @return a map of json objects containing the summary information
   */
  @GET
  @Path("/facets")
  @Timed
  @ApiOperation(value = "Api for getting summary information about the data used by a given model over a given time period.",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A map of json objects containing the summary information"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getFacethierarchy(@QueryParam("modelId") modelId: Int, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) = {
    val result = timeSeriesDao.getFacetHierarchy(modelId, startTime, endTime, cache)
      val a = mapper.writeValueAsString(result)
      Response.ok(a).build()
  }

  @Path("/timeseries/type/")
  @GET
  @Timed
  @Deprecated
  @deprecated
  def getTimeSeriesTypes(@QueryParam("modelId") modelId: Int, @QueryParam("type") typeField:String,  @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) = {
    val result = timeSeriesDao.getTimeSeries(modelId, typeField, startTime, endTime, cache)
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  /**
   * Get time series data for a given model, start and end times, type field, group and group id.  All the parameters are passed as a post form of the type specified
   * by the TimeSeriesInput class.
   *
   * @param input TimeSeriesInput class object containing the parameters that indicate which data to return
   * @return A list of time series data corresponding to the inputs passed in
   */
  @POST
  @Path("/type/group")
  @Timed
  @ApiOperation(value = "Api for getting time series data for a given model. Takes a model id, start and end times, type field, group and group id, and returns time series data based on these inputs.",
    httpMethod = "POST", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of time series data"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getTimeSeriesTypeGroup(@Valid input: TimeSeriesInput) = {
    val result = timeSeriesDao.getTimeSeriesTypeGroup(input.getModelId, input.getStartTime, input.getEndTime, input.getTypeField, input.getGroup, input.getGroupId, cache)
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

 /**
  * Supports the auto complete feature for TimeSeries by fetching probable matching results(hostnames/IPs/Usernames) for the given incoming string, model ID and Field type for the given time period.
  * The code may be called for the AD peer anomalies model(ModelID = 3) to fetch matching usernames and the web peer anomalies model(ModelId = 2) to fetch matching hostnames/IPs.
  * Possible values for fieldType are source and destination.
  * pageNo would always be 1??
  * @param modelId
  * @param fieldType
  * @param incomingString
  * @param startTime
  * @param endTime
  * @param pageNo
  * @return A list of matching hostnames/IPs/Usernames corresponding to the input criteria
  */
  @GET
  @Path("/auto/")
  @Timed
  @ApiOperation(value = "Api for fetching matching hostnames or IPs or Usernames for a given modelID, incoming string and field type. Takes a model id, start and end times, type field, incoming string, pageNo and returns matching hostnames or IPs in case of Model2 and Usernames in case of Model 3, corresponding to the given input criteria",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of matching hostnames or IPs or Usernames"),
    new ApiResponse(code = 400, message = "Bad Request")))
  def getAutoCompleteRecords(@QueryParam("modelId") modelId: Int, @QueryParam("fieldType") fieldType: String, @QueryParam("incomingString") incomingString: String,
                             @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                             @QueryParam("page") pageNo: Int): Response = {
    val result: util.Set[String] = timeSeriesDao.getAutoCompleteRecords(this.webProxySolrClient, this.iamSolrClient,
      taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,
      modelId, fieldType, incomingString, startTime, endTime, pageNo, 10, conf)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  /**
   * Get the top N destinations/users corresponding to the model ID and field type for the given period of time.
   * The code may be called for the AD peer anomalies model(ModelID = 3) to fetch top N users and the web peer anomalies model(ModelId = 2) to fetch top N destinations.
   * Possible values for fieldType are source and destination.
   * @param modelId
   * @param fieldType
   * @param startTime
   * @param endTime
   * @param n
   * @return list of top N destinations/users along with count of total bits/events and % of total traffic/events for the given model ID and time period.
   */
  @GET
  @Path("/top")
  @Timed
  @ApiOperation(value = "Api for fetching top N destinations or users for the given model ID and field type. Takes a model id, start and end times, type field and returns top N destinations or users corresponding to the given input criteria",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of top N destinations or users"),
    new ApiResponse(code = 400, message = "Bad Request")))
  def getTopNSourceDestination(@QueryParam("modelId") modelId: Int, @QueryParam("fieldType") fieldType: String, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String, @QueryParam("N") n: Int): Response = {
    val result: util.List[util.Map[String, AnyRef]] = timeSeriesDao.getTopNSourceDestination(this.webProxySolrClient,
      this.iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,
      modelId, fieldType, startTime, endTime, 1, n, 0, 0,conf)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build
  }

  @Path("/top/field/")
  @GET
  @Timed
  @Deprecated
  @deprecated
  def getTopNField(@QueryParam("modelId") modelId: Int, @QueryParam("fieldName") fieldName: String, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String, @QueryParam("N") n: Int): Response = {
    val result: util.List[util.Map[String, AnyRef]] = timeSeriesDao.getTopN(this.webProxySolrClient, this.iamSolrClient,
      taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, modelId, fieldName, startTime, endTime, 1, n, 0, 0)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build
  }

  /**
   * API to get the list of newly observed domains (for ModelId = 2)/users(for ModelId = 3) corresponding to given model Id and time period.
   * The code may be called for the AD peer anomalies model(ModelID = 3) to fetch top N new users and the web peer anomalies model(ModelId = 2) to fetch top N newly observed domains.
   * @param modelId
   * @param startTime
   * @param endTime
   * @param n
   * @return list of top N newly observed domains/users, along with the count of total bits/events and % of total traffic/events for the given model ID and time period
   */
  @GET
  @Path("/nod/")
  @Timed
  @ApiOperation(value = "Api for fetching top N newly observed domains or users for the given model ID and time period. Takes a model id, start and end times and returns top N newly observed domains or users corresponding to the given input criteria",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of newly observed domains/users"),
    new ApiResponse(code = 400, message = "Bad Request")))
  def getNewlyObservedDomains(@QueryParam("modelId") modelId: Int, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String, @QueryParam("N") n: Int): Response = {
    val result: util.List[util.Map[String, AnyRef]] = timeSeriesDao.getNewlyObservedDomains(this.webProxySolrClient, this.iamSolrClient, modelId, startTime, endTime, 1, n, 0, 0)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build
  }

  /**
   * Gets search results for a given model.
   * The code may be called for the AD peer anomalies model, the web peer anomalies model and the endpoint models.  It is not
   * clear whether the UI actually calls it for the endpoint models, however.
   *
   * @param input object constructed from the post body giving details about the search to be done.
   * @return a list of maps containing search results
   */
  @POST
  @Path("/search/")
  @Timed
  @ApiOperation(value = "Api for timeseries search.  The post body contains a model id and a list of facets to search for",
    httpMethod = "POST", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing the search results"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getSearchResults(@Valid input: TimeSeriesInput): Response = {

    val startDate: String = MiscUtils.getYMDSeparatedString(input.getStartTime)

    val daysElapsed: Int = Days.daysBetween(new DateTime(startDate), DateTime.now).getDays

    val result = {
      if (daysElapsed > 15)
        new SearchImpalaDao(conf).getTimeSeriesSearchResults(input,cache)
      else
        timeSeriesDao.getTimeSeriesSearchResults(webProxySolrClient, iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, input, cache)

    }
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  @Path("/series/top")
  @GET
  @Timed
  @Deprecated
  @deprecated
  def getTimeSeriesForTopSourceDestination(@QueryParam("modelId") modelId: Int, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String, @QueryParam("fieldType") fieldType: String, @QueryParam("value") value: String) ={
    val result = timeSeriesDao.getTimeSeriesForTopN(modelId, startTime, endTime,fieldType,value, cache)
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  /**
   * Returns a set of endpoint analytic data for a given model id, start and end time, and other parameters that specify which data to return.  The inputs are passed in as a
   * post form of the type specified by the EndPointAnalytics class.
   *
   * @param request HttpServletRequest class object containing the parameters that indicate which data to return
   * @return a list of maps containing the endpoint data requested
   */
  @POST
  @Path("/analytics/endpoint")
  @Timed
  @ApiOperation(value = "Api for getting endpoint analytics data based on the information in the POST body.  The post will contain a start and end time, a model id, what" +
    "kind of data to be queried (e.g. MD5, PORT, etc), and so on.",
    httpMethod = "POST", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A list of maps containing the data requested"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getEndPointAnalytics(@Context request : HttpServletRequest ) = {
    try{
      val input = this.mapper.readValue(request.getInputStream(), classOf[EndPointAnalytics])
      val result = taniumStatsDao.getEndpointAnalyticsData(input, featureResponseCache)
      val a = mapper.writeValueAsString(result)
      Response.ok(a).build()
    }catch{
      case jpe: JsonParseException => Response.status(Response.Status.BAD_REQUEST).entity(String.format("{\"status\":\"error\", \"code\": 400,\"message\":\"%s\"}",jpe.getMessage)).build()
      case jme: JsonMappingException => Response.status(Response.Status.BAD_REQUEST).entity(String.format("{\"status\":\"error\", \"code\": 400,\"message\":\"%s\"}",jme.getMessage)).build()
      case ioe: IOException => Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(String.format("{\"status\":\"error\", \"code\": 500,\"message\":\"%s\"}",ioe.getMessage)).build()
    }
  }
  /**
   * Returns the date of the oldest endpoint data in the system
   *
   * @return a map with a single entry key "firstTimeSeen", the value of which will be the date of the oldest endpoint data
   *         in the system.
   */
  @GET
  @Path("/analytics/endpoint/firstdatapoint")
  @Timed
  @ApiOperation(value = "Api for getting the timestamp of the oldest endpoint data in the system",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = "A map containing the timestamp of the oldest endpoint data"),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getFirstEndpointDatapoint() = {
    val result = taniumStatsDao.getFirstEndpointDatapoint()
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }
}