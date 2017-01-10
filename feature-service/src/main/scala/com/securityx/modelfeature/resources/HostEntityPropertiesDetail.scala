package com.securityx.modelfeature.resources

import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.HostEntityPropertiesDao


@Path ("/entity/host/{id}")
@Produces(Array(MediaType.APPLICATION_JSON))
class HostEntityPropertiesDetail (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration) {

  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)

  @GET
  @Path("/")
  @Timed
  def getHostId(@PathParam("id") HostId : String) = {

    val result = hostEntityPropertiesDao.getHostId(HostId);

    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }
}
