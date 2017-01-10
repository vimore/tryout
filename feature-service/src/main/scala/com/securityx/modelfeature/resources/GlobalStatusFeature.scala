package com.securityx.modelfeature.resources

import java.util.Calendar
import javax.validation.Valid
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.auth.User
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao._
import io.dropwizard.auth.Auth
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}
import scala.collection.parallel.mutable


/**
 * Apis displaying/filtering global application-level information
 *
 * Created by harish on 3/5/15.
 */
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/global")
class GlobalStatusFeature(val mapper: ObjectMapper, val conf: FeatureServiceConfiguration, val cache: FeatureServiceCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[GlobalStatusFeature])
  private var globalStatusDao = new GlobalStatusDao(conf)

  // The following sets of variables are used to implement a simple cache for data from this api.  The underlying data
  // only changes once per day, so we really only need to get it occasionally
  private final val cacheDurationHours = 6 // We will refresh the data once every 6 hours
  private final val cacheDurationMs = 6 * 60 * 60 * 1000  // refresh data once very 6 hours
  private var mostRecentTotalUsersCheck = 0l
  private var totalUsersResponse: String = null
  private var statisticsCheckTimes = mutable.ParHashMap.empty[String, Long]
  private var statisticsResponse = mutable.ParHashMap.empty[String, String]
  private var riskyHostUserCheckTimes = mutable.ParHashMap.empty[String, Long]
  private var riskyHostUserResponse = mutable.ParHashMap.empty[String, String]

  private var entityCountsResponse:String = null
  private var mostRecentEntityCountsCheck = 0l

  private val enabledCheckDuration = 5 * 60 * 1000; // How long in ms between checks for changes in enabled status
  private var mostRecentEnabledCheck = 0l
  private var enabledCheckResponse: String = null


  @GET
  @Path("/counts")
  @Timed
  @Deprecated
  def getTotalUsers(@Auth user: User) = {
    if (mostRecentTotalUsersCheck + cacheDurationMs < Calendar.getInstance().getTimeInMillis) {
      val result = globalStatusDao.getTotalTrafficCounts()
      totalUsersResponse = mapper.writeValueAsString(result)
      mostRecentTotalUsersCheck = Calendar.getInstance().getTime.getTime
    }
    Response.ok(totalUsersResponse).build()
  }

  @POST
  @Path("/counts/entities")
  @Timed
  def getEntityCounts(@Valid input: QueryJson) = {

    val startTime = input.getStartTime
    val endTime = input.getEndTime

    if ((startTime == null || startTime.isEmpty)
      && (endTime == null || endTime.isEmpty)) {
      // Return Global Entity Counts (from cache if present)

      if (mostRecentEntityCountsCheck + cacheDurationMs < Calendar.getInstance().getTimeInMillis) {

        val allEntities = globalStatusDao.getEntityCounts(input, cache, conf)
        entityCountsResponse = mapper.writeValueAsString(allEntities)
        mostRecentEntityCountsCheck = Calendar.getInstance().getTime.getTime
      }

      Response.ok(entityCountsResponse).build()

    } else {
      // Return Global Entity Counts based on time filter input. No caching

      val allEntities = globalStatusDao.getEntityCounts(input, cache, conf)
      Response.ok(mapper.writeValueAsString(allEntities)).build()

    }

  }

  @GET
  @Path("/statistics")
  @Timed
  def getStatistics(@Auth user: User, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) = {
    // Times will be in the format 2016-04-27T00:00:00.000Z, so getting just the first 10 characters
    // gives us the date.
    val date = startTime.substring(0, 10)
    val mostRecentCheck = {
      if (statisticsCheckTimes contains date)
        statisticsCheckTimes(date)
      else
        0
    }
    if (mostRecentCheck + cacheDurationMs < Calendar.getInstance().getTimeInMillis) {
      val result = globalStatusDao.getStatistics(startTime, endTime, cache)
      statisticsResponse += (date -> mapper.writeValueAsString(result))
      statisticsCheckTimes += (date -> Calendar.getInstance().getTime.getTime)
    }
    Response.ok(statisticsResponse(date)).build()
  }

  @GET
  @Path("/riskyHostsAndUsers")
  @Timed
  def getRiskyHostsAndUsers(@Auth user: User, @QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String,
                            @QueryParam("topN") topN: Integer, @QueryParam("order") order: String) = {
    // Times will be in the format 2016-04-27T00:00:00.000Z, so getting just the first 10 characters
    // gives us the date.
    val date = startTime.substring(0, 10)
    val mostRecentCheck = {
      if (riskyHostUserCheckTimes contains date)
        riskyHostUserCheckTimes(date)
      else
        0
    }
    if (mostRecentCheck + cacheDurationMs < Calendar.getInstance().getTimeInMillis) {
      val result = globalStatusDao.getRiskyHostsAndUsers(startTime, endTime, topN, order)
      riskyHostUserResponse += (date -> mapper.writeValueAsString(result))
      riskyHostUserCheckTimes += (date -> Calendar.getInstance().getTime.getTime)
    }
    Response.ok(riskyHostUserResponse(date)).build()
  }

  @GET
  @Path("/enabled")
  @Timed
  def getEnabled = {
    if (mostRecentEnabledCheck + enabledCheckDuration < Calendar.getInstance().getTimeInMillis) {
      val result = globalStatusDao.getEnabled
      enabledCheckResponse = mapper.writeValueAsString(result)
      mostRecentEnabledCheck = Calendar.getInstance().getTime.getTime
    }
    Response.ok(enabledCheckResponse).build()
  }

  /**
   * Clear a cache for a given date, or if date is "all", clear all dates.
   *
   * @param cache Which cache to clear.  Should be one of "enabled", "riskyHostsAndUsers", "statistics",
   *              or "count".
   * @param date A date in the form YYYY-MM-DD, indicating which day to clear, or "all" if the cache
   *             should be cleared for all dates. For caches that are not split by date, the date parameter
   *             is ignored.
   */
  @GET
  @Path("/clearCache")
  @Timed
  def clearCache(@QueryParam("cache") cache: String, @QueryParam("date") date: String) = {
    // We force a clear by setting the check time for a given cache/date to 0
    var response: String = ""
    if (cache == "enabled") {
      LOGGER.info("Invalidating cache for enabled check")
      mostRecentEnabledCheck = 0l
      response = "enabled cache invalidated"
    } else if (cache == "counts") {
      LOGGER.info("Invalidating cache for total users check (that is, for the /counts api)")
      mostRecentTotalUsersCheck = 0l
      response = "total users cache invalidated"
    } else if (cache == "riskyHostsAndUsers") {
      if (date == "all") {
        LOGGER.info("Invalidating cache for riskyHostsAndUsers for all dates")
        riskyHostUserCheckTimes = mutable.ParHashMap.empty[String, Long]
        response = "risky host and users cache invalidated"
      } else {
        LOGGER.info("Invalidating cache for riskyHostsAndUsers for date [" + date + "]")
        riskyHostUserCheckTimes += (date -> 0)
        response = "risky host and users cache invalidated for date [" + date + "]"
      }
    } else if (cache == "statistics") {
      if (date == "all") {
        LOGGER.info("Invalidating cache for statistics for all dates")
        statisticsCheckTimes = mutable.ParHashMap.empty[String, Long]
        response = "statistics cache invalidated"
      } else {
        LOGGER.info("Invalidating cache for statistics for date [" + date + "]")
        statisticsCheckTimes += (date -> 0)
        response = "statistics cache invalidated for date [" + date + "]"
      }
    }
    Response.ok(mapper.writeValueAsString(response)).build()
  }

  def getMostRecentTime(date: String, map: MutableMap[String, Long]): Long = {
    if (map.contains(date))
      map(date)
    else
      0
  }

  def setGlobalStatusDao(globalStatusDao: GlobalStatusDao): Unit ={
    this.globalStatusDao = globalStatusDao
  }

}
