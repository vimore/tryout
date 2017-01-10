package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.CloudChamberDao
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by mdeshon on 9/24/14.
 */

@Path ("/cloudchamber")
@Produces(Array(MediaType.APPLICATION_JSON))
class CloudChamberFeature (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration) {
  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[CloudChamberFeature])
  private val cloudChamber = new CloudChamberDao(conf)

  @GET
  @Path("/domaindate")
  @Timed
  def getDomainDate(@QueryParam("domain") domain : String) = {
    var buf: Map[String, String] = cloudChamber.getDomainDate(domain)
    buf += "domain" -> domain

    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @GET
  @Path("/whoisbyip")
  @Timed
  def getWhoIsByIp(@QueryParam("ip") ip : String) = {
    val response = cloudChamber.getWhoisByIp(ip)

    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(response)
    Response.ok(a).build()
  }

  @GET
  @Path("/whoisbydomain")
  @Timed
  def getWhoIsByDomain(@QueryParam("domain") domain : String) = {
    val response = cloudChamber.getDomainWhois(domain)

    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(response)
    Response.ok(a).build()
  }
}
