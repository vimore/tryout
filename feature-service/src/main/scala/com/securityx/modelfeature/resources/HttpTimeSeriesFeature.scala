package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.HttpTimeSeriesDao
import org.joda.time.MutableDateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.ListBuffer


@Path ("/httptimeseries")
@Produces(Array(MediaType.APPLICATION_JSON))
class HttpTimeSeriesFeature (val mapper:ObjectMapper, val conf:FeatureServiceConfiguration) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[HttpTimeSeriesFeature])
  private val parser:DateTimeFormatter = ISODateTimeFormat.dateTimeParser()
  private val httpTimeSeriesDao = new HttpTimeSeriesDao(conf)

  def fillZeros(startDateStr : String,
                endDateStr : String,
                periodSeconds : Int,
                timeEntries : scala.collection.mutable.ListBuffer[scala.collection.mutable.Map[String, Any]]) = {
    var finalTimeSeries = ListBuffer[collection.mutable.Map[String, Any]]()
    var startDate = MutableDateTime.parse(startDateStr)
    val endDate = MutableDateTime.parse(endDateStr)

    while(startDate.isBefore(endDate) || startDate.equals(endDate)) {
      val searchResult = timeEntries.filter(x => x.get("dateTime") == Some(startDate.toString))
      if (searchResult.length > 0) {
        val head = searchResult.head
        finalTimeSeries += collection.mutable.Map(
          "dateTime" -> head.get("dateTime"),
          "bitsInPerSecond" -> head.get("bitsInPerSecond"),
          "bitsOutPerSeconds" -> head.get("bitsOutPerSeconds"),
          "connectionsPerSecond" -> head.get("connectionsPerSecond")
        )
      }
      else {
        // Otherwise append an inactive entry
        finalTimeSeries += collection.mutable.Map(
          "dateTime" -> startDate.toString,
          "bitsInPerSecond" -> 0.0,
          "bitsOutPerSeconds" -> 0.0,
          "connectionsPerSecond" -> 0.0
        )
      }
      startDate.addSeconds(periodSeconds)
    }
    finalTimeSeries
  }

  @GET
  @Path("/httpTimeSeries")
  @Timed
  def httpTimeSeries(@QueryParam("startTime") startTimeStr : String,
                     @QueryParam("endTime") endTimeStr : String,
                     @QueryParam("type") typeStr : String,
                     @QueryParam("groupField") groupFieldStr : String,
                     @QueryParam("period") periodInt : Integer) = {

    val buf = httpTimeSeriesDao.getHttpTimeSeriesByTimestamps(startTimeStr, endTimeStr, typeStr, groupFieldStr, periodInt)
    val timeEntries = buf.map(x => scala.collection.mutable.Map[String,Any]("dateTime" -> x.get("dateTime"),
                                      "bitsInPerSecond" -> scala.math.BigDecimal(x.get("bitsInPerSecond").get.asInstanceOf[Double]).setScale(2, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble,
                                      "bitsOutPerSeconds" ->scala.math.BigDecimal(x.get("bitsOutPerSecond").get.asInstanceOf[Double]).setScale(2, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble,
                                      "connectionsPerSecond" -> scala.math.BigDecimal(x.get("connectionsPerSecond").get.asInstanceOf[Double]).setScale(2, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble
    ))

    val group = Map("periodSeconds" -> periodInt.toString,
                    "type" -> typeStr,
                    "groupField" -> groupFieldStr)
    val returnObject = Map("size" -> timeEntries.length, "group" -> group, "timeEntries" -> timeEntries)

    val a = mapper.writeValueAsString(returnObject)
    Response.ok(a).build()
  }

  @GET
  @Path("/types")
  @Timed
  def getTypes(@QueryParam("startTime") startTimeStr : String,
               @QueryParam("endTime") endTimeStr : String) = {
    val buf = httpTimeSeriesDao.getTypes(startTimeStr, endTimeStr)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @GET
  @Path("/groups")
  @Timed
  def getTypes(@QueryParam("startTime") startTimeStr : String,
               @QueryParam("endTime") endTimeStr : String,
               @QueryParam("type") typeStr : String) = {
    val buf = httpTimeSeriesDao.getGroups(startTimeStr, endTimeStr, typeStr)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @GET
  @Path("/groupHttpTimeSeries")
  @Timed
  def getTimeSeriesForAllGroups(@QueryParam("startTime") startDateStr : String,
                                @QueryParam("endTime") endDateStr : String,
                                @QueryParam("type") typeStr : String,
                                @QueryParam("period") periodInt : Int) = {
    val buf = httpTimeSeriesDao.getTimeSeriesForAllGroups(startDateStr, endDateStr, typeStr, periodInt)    // Group by the unique fields
    val timeEntries = buf.map(x => scala.collection.mutable.Map[String,Any](
        "type" -> x.get("type"),
        "groupField" -> x.get("groupField"),
        "periodSeconds" -> x.get("periodSeconds"),
        "dateTime" -> x.get("dateTime"),
        "bitsInPerSecond" -> scala.math.BigDecimal(x.get("bitsInPerSecond").get.asInstanceOf[Double]).setScale(4, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble,
        "bitsOutPerSecond" ->scala.math.BigDecimal(x.get("bitsOutPerSecond").get.asInstanceOf[Double]).setScale(4, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble,
        "connectionsPerSecond" -> scala.math.BigDecimal(x.get("connectionsPerSecond").get.asInstanceOf[Double]).setScale(4, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble
      ))
    val groups = timeEntries.groupBy(x => scala.collection.immutable.Map[String, Option[Any]](
      "type" -> x.get("type"),
      "groupField" -> x.get("groupField"),
      "periodSeconds" -> x.get("periodSeconds")
    ))
    var finalGroups = ListBuffer[Map[String, Any]]()
    groups.foreach {
      case(jsonKey,group) =>
        finalGroups += Map("count" -> timeEntries.length, "group" -> jsonKey, "timeSeries" -> group.map(x => Option[Any](  scala.collection.mutable.Map[String,Any](
        "dateTime" -> x.get("dateTime"),
        "bitsInPerSecond" -> x.get("bitsInPerSecond"),
        "bitsOutPerSecond" -> x.get("bitsOutPerSecond"),
        "connectionsPerSecond" -> x.get("connectionsPerSecond")
        ))))
    }

    val a = mapper.writeValueAsString(finalGroups)
    Response.ok(a).build()
  }
}
