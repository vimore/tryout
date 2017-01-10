package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by harish on 2/18/15.
 */
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/query")
class QueryOperatorFeature(val mapper: ObjectMapper, val conf: FeatureServiceConfiguration, val cache: FeatureServiceCache) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[QueryOperatorFeature])
  private val queryOperatorDao = new QueryOperatorDao(conf)

  @Path("/operators")
  @GET
  @Timed
  def getOperators()= {
    val buf = queryOperatorDao.getOperators(cache)
    val operators = buf.map(x => scala.collection.mutable.Map[String, Any](
      "name" -> x.get("name"),
      "id" -> x.get("id")
    ))

    val a = mapper.writeValueAsString(operators)
    Response.ok(a).build()
  }


  @Path("/filters")
  @GET
  @Timed
  def getDimensions()= {
    val buf = queryOperatorDao.getQueryFields(cache)
    val operators = buf.map(x => scala.collection.mutable.Map[String, Any](
      "name" -> x.get("name"),
      "id" -> x.get("id"),
      "operators" -> x.get("operators")
    ))

    val a = mapper.writeValueAsString(operators)
    Response.ok(a).build()
  }
}
