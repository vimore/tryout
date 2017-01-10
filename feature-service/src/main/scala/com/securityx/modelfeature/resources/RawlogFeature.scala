package com.securityx.modelfeature.resources

import javax.ws.rs.core.{Response, MediaType}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.config.FeatureServiceConfiguration

import javax.ws.rs.{GET, Path, Produces, QueryParam}

import scala.collection.mutable.{Map => MutableMap, ListBuffer}

import com.securityx.modelfeature.dao.RawlogsDao
import org.slf4j.{LoggerFactory, Logger}


@Path("/rawlogs")
@Produces(Array(MediaType.APPLICATION_JSON))
class RawlogFeature (val mapper: ObjectMapper, val conf:FeatureServiceConfiguration) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[RawlogFeature])
  private val rawlogDao = new RawlogsDao(conf)

  @GET
  @Path("/byUuid")
  @Timed
  @deprecated
  @Deprecated
  def getRawlog(@QueryParam("uuid") uuid : String) = {
    val buf = rawlogDao.getRawlog(uuid)
    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(buf)
    Response.ok(a).build()
  }

  @GET
  @Path("/byUuidList")
  @Timed
  @deprecated
  @Deprecated
  def getRawlogList(@QueryParam("uuidList") uuidList : String) = {
    var result: List[MutableMap[String, Any]] = null
    if (uuidList != null) {
      // UuidList must be a comma separated list of uuids. Convert it to an array.
      val uuidArray = uuidList.split(",").map(_.trim)

      result = rawlogDao.getRawlogList(uuidArray)
    }

    mapper.registerModule(DefaultScalaModule)

    val a = mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

}
