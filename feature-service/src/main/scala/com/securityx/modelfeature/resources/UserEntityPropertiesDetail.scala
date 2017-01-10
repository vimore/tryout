package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces, QueryParam}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.UserEntityPropertiesDao


@Path ("/entity/user")
@Produces(Array(MediaType.APPLICATION_JSON))
class UserEntityPropertiesDetail (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration) {

  private val userEntityPropertiesDao = new UserEntityPropertiesDao(conf)

  @GET
  @Path("/")
  @Timed
  def getUserId(@QueryParam("id") UserId : String) = {

    val result = userEntityPropertiesDao.getUserId(UserId);

    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }
}
