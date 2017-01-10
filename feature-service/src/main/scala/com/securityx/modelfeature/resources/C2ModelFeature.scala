package com.securityx.modelfeature.resources

import javax.ws.rs.core.{Response, MediaType}
import javax.ws.rs._

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{C2ModelDao, BeaconsDao}
import org.slf4j.{LoggerFactory, Logger}

/**
 * Created by harish on 3/15/16.
 */
@Path ("/c2")
@Produces(Array(MediaType.APPLICATION_JSON))
class C2ModelFeature (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration, val cache: FeatureServiceCache) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[C2ModelFeature])
  private val c2ModelDao = new C2ModelDao(conf)

  @GET
  @Path("/anomalies")
  @Timed
  def getBeacons(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String,
                  @DefaultValue("300") @QueryParam("topN") topN : Integer) = {
    val buf = c2ModelDao.getC2Anomalies(startTime, endTime, topN, cache)
    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }


}
