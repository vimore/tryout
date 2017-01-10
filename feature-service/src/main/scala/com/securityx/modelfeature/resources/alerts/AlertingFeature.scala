package com.securityx.modelfeature.resources.alerts

import javax.validation.Valid
import javax.ws.rs._
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.alert.AlertNotifier
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger
import com.securityx.modelfeature.alert.zookeeper.ZookeeperClient
import com.securityx.modelfeature.common.cache.AlertCache
import com.securityx.modelfeature.common.inputs.{AlertDefinition, AlertDestination}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.alerts.{AlertsDao, AlertsDestinationDao}
import org.slf4j.{Logger, LoggerFactory}

@Path("/alerts")
@Produces(Array(MediaType.APPLICATION_JSON))
class AlertingFeature(mapper: ObjectMapper, conf: FeatureServiceConfiguration, cache: FeatureServiceCache,
                      alertCache: AlertCache, alertNotifier: AlertNotifier, alertLogger: AlertAuditLogger,
                      zookeeperClient : ZookeeperClient ) {
  private val Logger: Logger = LoggerFactory.getLogger(classOf[AlertingFeature])
  private val alertsDestinationDao: AlertsDestinationDao = new AlertsDestinationDao(conf)
  private val alertsDao: AlertsDao = new AlertsDao(conf)


  @Path("/")
  @GET
  @Timed
  def getAlerts : Response = {
    val buf = alertsDao.getValidAlertsFromCache(alertCache)
    val a =  mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @Path("/")
  @POST
  @Timed
  def createAlert(@Valid alert: AlertDefinition) : Response = {

    val alertIdBuf = alertsDao.createAlert(alert,alertCache, zookeeperClient, alertLogger)
    val a = mapper.writeValueAsString(alertIdBuf)
    Response.ok(a).build()

  }

  @Path("/")
  @PUT
  @Timed
  def updateAlert(@Valid alert: AlertDefinition) : Response = {

    val alertIdBuf = alertsDao.updateAlert(alert,alertCache, zookeeperClient, alertLogger)
    val a = mapper.writeValueAsString(alertIdBuf)
    Response.ok(a).build()

  }

  @Path("/{id}")
  @DELETE
  @Timed
  def deleteAlert(@PathParam("id")  id : String) : Response = {

    val deleted = alertsDao.deleteAlert(id, alertCache, zookeeperClient, alertLogger)
    var response: Response  = null
    if(deleted){
      response = Response.ok(mapper.writeValueAsString("SUCCESS")).build()
    }else{
      response = Response.status(Status.BAD_REQUEST).entity(mapper.writeValueAsString("FAILURE")).build()
    }
    response

  }

  @Path("/destinationTypes/")
  @GET
  @Timed
  def getAlertDestinationsTypes: Response = {
    Response.ok(alertsDestinationDao.getAlertDestinationTypes(cache)).build
  }

    @Path("/destinations/")
  @GET
  @Timed
  def getAlertDestinations: Response = {
    val destinations = alertsDestinationDao.getAlertDestinations
    val a = mapper.writeValueAsString(destinations)
    Response.ok(a).build
  }

  @Path("/destinations/")
  @POST
  @Timed
  def addAlertDestination(@Valid alertDestination: AlertDestination) : Response = {

    val count = alertsDestinationDao.addAlertDestination(alertDestination, alertNotifier, alertLogger, cache)
    var response: Response  = null
    if(count != 0){
      response = Response.ok(mapper.writeValueAsString("SUCCESS")).build()
    }else{
      response = Response.status(Status.BAD_REQUEST).entity(mapper.writeValueAsString("FAILURE")).build()
    }
    response
  }


  @Path("/getAlertFrequencies")
  @GET
  @Timed
  def getAlertFrequencies : Response = {
    val buf = alertsDao.getAlertFrequencies
    val a =  mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }


}
