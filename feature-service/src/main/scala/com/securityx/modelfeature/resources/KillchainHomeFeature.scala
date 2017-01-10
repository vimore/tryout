package com.securityx.modelfeature.resources

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao._
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}


/**
 * Apis supporting the page for displaying/filtering Killchain Categories and Security EVent Types.
 *
 * Created by harish on 11/14/14.
 */
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/")
class KillchainHomeFeature(val mapper: ObjectMapper, val conf: FeatureServiceConfiguration, val cache: FeatureServiceCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[WebHistoryFeature])
  private val parser: DateTimeFormatter = ISODateTimeFormat.dateTimeParser()
  private val killchainDao = new KillchainDao(conf)
  private val securityEventTypeDao = new SecurityEventTypesDao(conf)
  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)
  private val userEntityProperties = new UserEntityPropertiesDao(conf)
  private val noxDao = new NewlyObservedXDao(conf)
  private val homePageDao = new HomePageDao(conf)

  /**
   * provides information about all the killChain categories
   * @return
   */
  @GET
  @Path("/killchain")
  @Timed
  def getAllKillchains() = {

    val buf = killchainDao.getKillchainCategories(cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killChainId" -> x.get("killChainId"),
      "category" -> x.get("category")
    ))

    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }


  /**
   * provides information about all the Security Event Types
   * @return
   */
  @GET
  @Path("/killchain/securityeventtypes/")
  @Timed
  def getAllSecurityEventTypes() = {

    val buf = killchainDao.getAllSecurityEventTypes(cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> { if(x.contains("killchainId") && !x.get("killchainId").equals(Some(null))) x.get("killchainId") else  null },
      "eventId" -> { if(x.contains("eventId") && !x.get("eventId").equals(Some(null))) x.get("eventId") else  null },
      "type" ->{ if(x.contains("type") && !x.get("type").equals(Some(null))) x.get("type") else  null },
      "typePrefix" -> { if(x.contains("typePrefix") && !x.get("typePrefix").equals(Some(null))) x.get("typePrefix") else  null },
      "eventModelId" -> { if(x.contains("eventModelId") && !x.get("eventModelId").equals(Some(null))) x.get("eventModelId") else  null },
      "eventModelName" ->  { if(x.contains("eventModelName") && !x.get("eventModelName").equals(Some(null))) x.get("eventModelName") else
        null },
      "eventDescription" ->  { if(x.contains("eventDescription") && !x.get("eventDescription").equals(Some(null))) x.get("eventDescription") else  null },
      "featureLabel" ->  { if(x.contains("featureLabel") && !x.get("featureLabel").equals(Some(null))) x.get("featureLabel") else  null }
    ))

    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }

  /**
   * provides information about all the killChain categories
   * @return
   */
  @GET
  @Path("/home/killchain/{id}/entities/counts")
  @Timed
  def getEntitiesPerKillChainType(@PathParam("id") killchainId: Int, @QueryParam("startTime") startTimeStr: String, @QueryParam("endTime") endTimeStr: String, @QueryParam ("risk") minRisk: Double) = {

    val buf = homePageDao.getEntitiesPerKillChainType(killchainId, startTimeStr, endTimeStr, minRisk, cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "dateTime" -> x.get("dateTime"),
      "count" -> x.get("count")
    ))

    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }

  /**
   *
   * @param killchainId Int killchainid
   * @return returns SecurityEventTypes associated with the input killchain Id
   */
  @GET
  @Path("/killchain/{id}/securityeventtypes")
  @Timed
  def getSecurityEventsFromKillchainId(@PathParam("id") killchainId: Int) = {
    val buf = securityEventTypeDao.getSecurityEventTypesFromKillchainId(killchainId, cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "securityEventTypeId" -> x.get("securityEventTypeId"),
      "typePrefix" -> x.get("typePrefix"),
      "type" -> x.get("type")
    ))

    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()

  }

  @GET
  @Path("/home/killchain/{killId}/securityeventtypes/{eventId}/entities/counts")
  @Timed
  def getEventEntityCountsByKillchainIdSecEventId(@PathParam("killId") killchainId: Int, @PathParam("eventId") securityEventId: Int,
                                                  @QueryParam("startTime") startTimeStr: String,
                                                  @QueryParam("endTime") endTimeStr: String, @QueryParam ("risk") minRisk: Double) = {
    val buf = homePageDao.getEventEntityCountsByKillchainIdSecEventId(killchainId, securityEventId, startTimeStr, endTimeStr, minRisk, cache);
    if (buf == null)
      Response.noContent()
    else {
      val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
        "killchainId" -> x.get("killchainId"),
        "securityEventTypeId" -> x.get("securityEventTypeId"),
        "dateTime" -> x.get("dateTime"),
        "count" -> x.get("count")
      ))

      val a = mapper.writeValueAsString(categories)
      Response.ok(a).build()
    }

  }

  @GET
  @Path("/home/securityeventtypes/entities/counts")
  @Timed
  def getEventEntityCounts(@QueryParam("startTime") startTimeStr: String, @QueryParam("endTime") endTimeStr: String, @QueryParam ("risk") minRisk: Double) = {

      val buf = homePageDao.getEventEntityCounts(startTimeStr, endTimeStr, minRisk, cache)

    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "securityEventTypeId" -> x.get("securityEventTypeId"),
      "typePrefix" -> x.get("typePrefix"),
      "type" -> x.get("type"),
      "dateTime" -> x.get("dateTime"),
      "count" -> x.get("count"),
      "maxRisk" -> x.get("maxRisk")
    ))

    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }


  /**
   * List of top 100 entity summary details for all kill chains for all security event types for a given day sorted by risk scores.
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @return
   */
  @GET
  @Path("/home/entities/summary")
  @Timed
  def getEntitySummaryForAllKillchain(@QueryParam("startTime") startTimeStr: String,
                                      @QueryParam("endTime") endTimeStr: String, @QueryParam("topN") topN: Int) = {

    val buf = homePageDao.getEntitySummaryForAllKillchains(startTimeStr, endTimeStr, topN, cache)


    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "entityIp" -> x.get("entityIp"),
      "entityName" -> x.get("entityName"),
      "dateTime" -> x.get("dateTime"),
      "risk" -> x.get("risk"),
      "securityEventId" -> x.get("securityEventId"),
      "modelId" -> x.get("modelId"),
      "modelName" -> x.get("modelName")
    ))


    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }

  /**
   * List of top 100 entity summary details associated with each kill chain for a given day sorted by risk scores.
   * @param killchainId Int killchain Id
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @return
   */
  @GET
  @Path("/home/killchain/{id}/entities/summary")
  @Timed
  def getEntitySummaryByKillchainId(@PathParam("id") killchainId: Int, @QueryParam("startTime") startTimeStr: String,
                                    @QueryParam("endTime") endTimeStr: String, @QueryParam("topN") topN: Int) = {

    val buf = homePageDao.getEntitySummaryByKillchainId(killchainId, startTimeStr, endTimeStr, topN, cache)


    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "entityIp" -> x.get("entityIp"),
      "entityName" -> x.get("entityName"),
      "dateTime" -> x.get("dateTime"),
      "risk" -> x.get("risk"),
      "securityEventId" -> x.get("securityEventId"),
      "modelId" -> x.get("modelId"),
      "modelName" -> x.get("modelName")
    ))


    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }


  /**
   * List of top 100 entity details associated with each security event type associated with each kill chain for a given day sorted by risk scores
   * @param killchainId Int killchain Id
   * @param securityEventId Int securityEvent Id
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @return
   */
  @GET
  @Path("/home/killchain/{killId}/securityeventtypes/{eventId}/entities/summary")
  @Timed
  def getEntitySummaryByKillchainId(@PathParam("killId") killchainId: Int, @PathParam("eventId") securityEventId: Int, @QueryParam("startTime") startTimeStr: String,
                                    @QueryParam("endTime") endTimeStr: String, @QueryParam("topN") topN: Int) = {

    val buf = homePageDao.getEntitySummaryByKillchainIdSecurityEventId(killchainId, securityEventId, startTimeStr, endTimeStr, topN, cache)


    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "killchainId" -> x.get("killchainId"),
      "entityIp" -> x.get("entityIp"),
      "entityName" -> x.get("entityName"),
      "dateTime" -> x.get("dateTime"),
      "risk" -> x.get("risk"),
      "securityEventId" -> x.get("securityEventId"),
      "modelId" -> x.get("modelId"),
      "modelName" -> x.get("modelName")
    ))


    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }


  /**
   *
   * @param entityId Int killchainid
   * @return returns SecurityEventTypes associated with the input killchain Id
   */
  @GET
  @Path("/home/entity/{id}")
  @Timed
  def getEntityDetails(@PathParam("id") entityId: String) = {
    val result = hostEntityPropertiesDao.getHostId(entityId);

    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()

  }

  @GET
  @Path("/home/usertypes/count")
  @Timed
  def getUserTypes(@QueryParam("startTime") startTimeStr: String,
                   @QueryParam("endTime") endTimeStr: String) = {
    val result = userEntityProperties.getUserTypes(startTimeStr, endTimeStr)
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()

  }


  @GET
  @Path("/home/nox/")
  @Timed
  def getNox(@QueryParam("startTime") startTimeStr: String,
             @QueryParam("endTime") endTimeStr: String) = {
    val result = noxDao.getNOX(startTimeStr, endTimeStr, cache)
    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()

  }

}