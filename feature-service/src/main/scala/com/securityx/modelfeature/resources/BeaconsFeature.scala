package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.BeaconsDao
import org.slf4j.{Logger, LoggerFactory}

@Path ("/beacons")
@Produces(Array(MediaType.APPLICATION_JSON))
class BeaconsFeature (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[BeaconsFeature])
  private val beaconsDao = new BeaconsDao(conf)

  @GET
  @Path("/beacons")
  @Timed
  def getBeacons(@QueryParam("dateTime") dateStr : String, @QueryParam("period") periodSecondsInt : Integer) = {
    val buf = beaconsDao.getBeacons(dateStr, periodSecondsInt)
    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @GET
  @Path("/beaconsday")
  @Timed
  def getBeaconsDay(@QueryParam("startTime") startTime : String, @QueryParam("endTime") endTime : String) = {

    val buf = beaconsDao.BeaconsDay(startTime, endTime)
    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @GET
  @Path("/beaconSeries")
  @Timed
  def getBeaconSeries(@QueryParam("startTime") startTimeStr : String,
                 @QueryParam("endTime") endTimeStr : String,
                 @QueryParam("period") periodSecondsInt : Integer) = {

    println("startTimeStr: " + startTimeStr + " endTimeStr: " + endTimeStr + " period: " + periodSecondsInt.toString)
    val beacons = beaconsDao.getBeaconingSeries(startTimeStr, endTimeStr, periodSecondsInt);

    val a = mapper.writeValueAsString(beacons)
    Response.ok(a).build()
  }

  @GET
  @Path("/beaconSeries2")
  @Timed
  def getBeaconSeries2(@QueryParam("startTime") startTimeStr : String,
                      @QueryParam("endTime") endTimeStr : String,
                      @QueryParam("period") periodSecondsInt : Integer) = {

    println("startTimeStr: " + startTimeStr + " endTimeStr: " + endTimeStr + " period: " + periodSecondsInt.toString)
    val beacons = beaconsDao.getBeaconingSeries(startTimeStr, endTimeStr, periodSecondsInt);
    val groups = beacons.groupBy(x => Map[String, Any]
      ("sourceNameOrIp" -> x.get("sourceNameOrIp"),
        "destinationNameOrIp" -> x.get("destinationNameOrIp")
      )
    )

    val answer = groups.map { case (key, value) => (Map("key" -> key, "value" -> value.map { x => Map("risk" -> x.get("risk"),
      "eventTime" -> x.get("eventTime"), "interval" -> x.get("interval"))}))}

    val a = mapper.writeValueAsString(answer)
    Response.ok(a).build()
  }
}
