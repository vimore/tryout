package com.securityx.modelfeature.dao

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 11/25/14.
 */
class ModelDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[ModelDao])

  def getAllModels(cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    try {
      for ((modelId, modelName) <- cache.getModelsMap) {
        val selectionMap = MutableMap[String, Any]()
        selectionMap += "modelId" -> modelId
        selectionMap += "modelName" -> modelName
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Exception occurred: " + e)
    }
    buf
  }
}
