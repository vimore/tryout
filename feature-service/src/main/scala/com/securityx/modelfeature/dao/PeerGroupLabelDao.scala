package com.securityx.modelfeature.dao

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, SecurityEventTypeConfiguration}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

object PeerGroupLabelSelectAll extends Enumeration {

  val FEATURE_ID = Value("FEATURE_ID")
  val FEATURE_LABEL = Value("FEATURE_LABEL")
  val FEATURE_ANOMALY_NAME = Value("FEATURE_ANOMALY_NAME")

  def columns = {
    PeerGroupLabelSelectAll.values.mkString(" , ")
  }
}

class PeerGroupLabelDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[PeerGroupLabelDao])

  def getPeerGroupLabels(cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val map: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getKillchainToSecurityEventDataMap()
    try {
      for ((key, value) <- map) {
        value.foreach { securityEventData =>
          if (!securityEventData.getFeatureLabel.equals("N/A")) {
            val selectionMap = MutableMap[String, Any]()
            selectionMap += "featureId" -> securityEventData.getSecurityEventTypeId
            selectionMap += "featureLabel" -> securityEventData.getFeatureLabel
            selectionMap += "featureAnomalyName" -> securityEventData.getEventType
            selectionMap += "modelId" -> securityEventData.getModel
            buf += selectionMap
          }

        }
      }
    } catch {
      case e: Exception => {
        Logger.error("Exception Occurred : " + e)
      }
    }
    buf
  }

}
