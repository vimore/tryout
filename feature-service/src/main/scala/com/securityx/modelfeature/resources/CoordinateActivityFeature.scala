package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{CoordActivityDao, HostEntityPropertiesDao}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

@Path ("/coordactivity")
@Produces(Array(MediaType.APPLICATION_JSON))
class CoordinateActivityFeature (val mapper:ObjectMapper, val conf:FeatureServiceConfiguration) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[CoordinateActivityFeature])
  private val eventDao = new CoordActivityDao(conf)
  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)

  @GET
  @Timed
  def getEventsInTime(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String,
                        @QueryParam("topN") topN : Integer) = {
    val buf = eventDao.getEventsInTime(startTime, endTime, topN)
    val entries = buf.map(x => scala.collection.mutable.Map[String,Any](
      "dateTime" -> {if(x.contains("dateTime")) x("dateTime") else "" },
      "edgeId" -> {if(x.contains("edgeId")) x("edgeId") else "" },
      "clusterId" -> {if(x.contains("clusterId")) x("clusterId") else "" },
      "anomalyClusterScore" -> {if(x.contains("anomalyClusterScore")) x("anomalyClusterScore") else "" },
      "anomalyEdgeScore" -> {if(x.contains("anomalyEdgeScore")) x("anomalyEdgeScore") else "" },
      "sourceNameOrIp" -> {if(x.contains("sourceNameOrIp")) x("sourceNameOrIp") else "" },
      "destinationNameOrIp" -> {if(x.contains("destinationNameOrIp")) x("destinationNameOrIp") else "" },
      "selectedFeatures" -> {if(x.contains("selectedFeatures")) x("selectedFeatures") else "" },
      "featureValues" -> {if(x.contains("featureValues")) x("featureValues") else "" },
      "entityId" -> {if(x.contains("entityId")) x("entityId") else "" }
    ))

    val a = mapper.writeValueAsString(entries)
    Response.ok(a).build()
  }

  @GET
  @Path("/entities")
  @Timed
  def getCoordActivityEntitiesParallel(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String,
                                   @QueryParam("id") id : Integer) = {
    val entries = eventDao.getCoordActivityEntitiesParallel(startTime, endTime, id)
    if (entries.isEmpty) {
      LOGGER.debug("Empty IP list returned.")
      Response.ok(mapper.writeValueAsString(collection.mutable.ListBuffer.empty[MutableMap[String,Any]])).build()
    } else {
      val a = mapper.writeValueAsString(entries)
      Response.ok(a).build()
    }
  }
}
