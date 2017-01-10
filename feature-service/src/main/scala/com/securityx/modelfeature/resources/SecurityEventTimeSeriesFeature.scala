package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{DetectorHomeDao, SecurityEventTimeSeriesDao}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by harish on 2/27/15.
 */
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/securityevents")
class SecurityEventTimeSeriesFeature(val mapper: ObjectMapper, val conf: FeatureServiceConfiguration, val cache: FeatureServiceCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[QueryOperatorFeature])
  private val securityEventTimeSeriesDao = new SecurityEventTimeSeriesDao(conf)
  private val detectorHomeDao = new DetectorHomeDao(conf)

  @Path("/series")
  @GET
  @Timed
  def getOperators(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String)= {
    val buf = securityEventTimeSeriesDao.getSecurityEventTimeSeries(startTime, endTime, cache)
    val operators = buf.map(x => scala.collection.mutable.Map[String, Any](
      "dateTime" -> x.get("dateTime"),
      "eventCount" -> x.get("eventCount")
    ))

    val a = mapper.writeValueAsString(operators)
    Response.ok(a).build()
  }

  @Path("/top")
  @GET
  @Timed
  def getTopNSecurityEvents(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                            @QueryParam("topN") topN: Int)= {
    val buf = detectorHomeDao.getTopNSecurityEvents(startTime, endTime, topN, cache)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }
}
