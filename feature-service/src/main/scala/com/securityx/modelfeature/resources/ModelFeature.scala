package com.securityx.modelfeature.resources

import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{GET, Path, Produces}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.ModelDao
import org.slf4j.{Logger, LoggerFactory}

/**
 * Apis for Models
 *
 * Created by harish on 11/25/14.
 */
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/models")
class ModelFeature (val mapper: ObjectMapper, val conf: FeatureServiceConfiguration, val cache: FeatureServiceCache) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[ModelFeature])
  private val modelDao = new ModelDao(conf)

  /**
   * provides (ModelName,ModelId) for all models
   * @return
   */
  @GET
  @Timed
  def getAllModels() = {
    val buf = modelDao.getAllModels(cache)
    val categories = buf.map(x => scala.collection.mutable.Map[String, Any](
      "modelId" -> x.get("modelId"),
      "modelName" -> x.get("modelName")
    ))

    val a = mapper.writeValueAsString(categories)
    Response.ok(a).build()
  }


}
