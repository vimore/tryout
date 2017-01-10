package com.securityx.modelfeature.resources


import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs._

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.EntityFeaturesDao
import com.wordnik.swagger.annotations.{ApiResponse, ApiResponses, ApiOperation}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by harish on 7/31/15.
 */
@Path ("/featureextraction")
@Produces(Array(MediaType.APPLICATION_JSON))
class EntityFeatures (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration, val cache: FeatureServiceCache) {



  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[EntityFeatures])
  private val entityFeaturesDao = new EntityFeaturesDao(conf)

  @GET
  @Timed
  @Path ("/id")
  @Deprecated
  def getFeatureTimeSeriesById(@QueryParam("startTime") startTime : String,
                 @QueryParam("endTime") endTime : String,
                 @QueryParam("source") source : String,
                 @QueryParam("destination") destination : String,
                 @QueryParam("modelId") modelId : Int,
                 @QueryParam("securityEventId") securityEventId : Int) = {
    val buf = entityFeaturesDao.getEntityFeaturesByFeatureId(startTime, endTime,
      source, destination,
      modelId, securityEventId, cache)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  /**
   * Gets entity features for a given time range, model id, source, destination and feature label.  Returns the count of
   * entries grouped by feature value.
   *
   * @param startTime start of the time range
   * @param endTime end of the time range
   * @param source source to query for. May be null
   * @param destination destination to query for. May be null
   * @param modelId model id to query for
   * @param featureLabel feature label to query for
   * @return counts of entries that match the inputs grouped by the feature values
   */
  @GET
  @Timed
  @ApiOperation(value = "Gets entity features for a given time range, model id, source, destination and feature label.  Returns the count of " +
    "entries grouped by feature value",
    httpMethod = "GET", produces = MediaType.APPLICATION_JSON, response = classOf[Response])
  @ApiResponses(Array(new ApiResponse(code = 200, message = ""),
    new ApiResponse(code = 400, message = "Bad Request")))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def getFeatureTimeSeriesByFeatureLabel(@QueryParam("startTime") startTime : String,
                 @QueryParam("endTime") endTime : String,
                 @QueryParam("source") source : String,
                 @QueryParam("destination") destination : String,
                 @QueryParam("modelId") modelId : Int,
                 @QueryParam("featureLabel") featureLabel : String) = {
    val buf = entityFeaturesDao.getEntityFeaturesByFeatureLabel(startTime, endTime,
      source, destination,
      modelId, featureLabel, cache)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }


}