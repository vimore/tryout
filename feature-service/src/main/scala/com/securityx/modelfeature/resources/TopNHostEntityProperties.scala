package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.HostEntityPropertiesDao


@Path ("/entity/hosts")
@Produces(Array(MediaType.APPLICATION_JSON))
class TopNHostEntityProperties (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration) {

  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)
  @GET
  @Path("/")
  @Timed
  def getTopNHosts(@QueryParam("topn") TopN : Integer) = {

    val result = hostEntityPropertiesDao.getTopNHosts(TopN)

    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  } 
}
