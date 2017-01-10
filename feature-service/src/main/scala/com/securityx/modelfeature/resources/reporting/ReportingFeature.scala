package com.securityx.modelfeature.resources.reporting

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.alerts.AlertLogDao
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by harish on 6/12/15.
 */
@Path("/reports")
@Produces(Array(MediaType.APPLICATION_JSON))
class ReportingFeature (mapper: ObjectMapper, conf: FeatureServiceConfiguration) {
  private val Logger: Logger = LoggerFactory.getLogger(classOf[ReportingFeature])
  private val alertLogDao: AlertLogDao = new AlertLogDao(conf)

  @Path("/alerts")
  @GET
  @Timed
  def getAlerts : Response = {
    val buf = alertLogDao.getAlertLogs()
    val a =  mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }


}
