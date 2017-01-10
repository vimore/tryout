package com.securityx.modelfeature.dao

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, SecurityEventTypeConfiguration}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 11/14/14.
 */
class KillchainDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[KillchainDao])

  /**
   * queries the killchain category table and returns all the killchain categories along with corresponding killchain Id.
   *
   * @return List of all the Killchain Categories
   */
  def getKillchainCategories(cache: FeatureServiceCache) = {
    val map = cache.getKillchainMap
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    for ((k, v) <- map) {
      val selectionMap = MutableMap[String, Any]()
      selectionMap += "killChainId" -> k
      selectionMap += "category" -> v
      buf += selectionMap
    }
    buf
  }


  /**
   * queries the killchain category table and returns all the killchain categories along with corresponding killchain Id.
   *
   * @return List of all the Killchain Categories
   */
  def getAllSecurityEventTypes(cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val secEventDataMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getSecurityEventDataMap
    val modelsMap: MutableMap[Integer, String] = cache.getModelsMap
    for ((secEventId, secEventList) <- secEventDataMap) {

      secEventList.foreach { securityEvent =>
        // Filter out Beaconing and/or Command and Control, depending on whether the config tells us to include them.
        var includeCard = true
        if (!conf.includeBeaconingBehaviors() && securityEvent.getSecurityEventTypeId == 0 &&
            securityEvent.getEventType == "Beaconing") {
          includeCard = false
        }
        if (!conf.includeC2Behaviors() && securityEvent.getSecurityEventTypeId == 0 &&
            securityEvent.getEventType == "Command and Control") {
          includeCard = false
        }
        if (includeCard) {
          val selectionMap = MutableMap[String, Any]()
          selectionMap += "killchainId" -> securityEvent.getKillchainId
          selectionMap += "type" -> securityEvent.getEventType
          selectionMap += "typePrefix" -> securityEvent.getTypePrefix
          selectionMap += "eventId" -> secEventId
          selectionMap += "eventModelId" -> securityEvent.getModel
          selectionMap += "eventModelName" -> modelsMap(securityEvent.getModel)
          selectionMap += "eventDescription" -> securityEvent.getEventDescription
          selectionMap += "featureLabel" -> securityEvent.getFeatureLabel

          buf += selectionMap
        }
      }
    }
    buf
  }

  /**
   * Queries the kill_chain_categories table and forms a mapping of killchainID -> killchain category
   *
   * @return mapping of killchain ID to killchain category
   */
  def getKillchainIdCategoryMap(cache: FeatureServiceCache): scala.collection.mutable.Map[Integer, String] = {
    val map = cache.getKillchainMap
    val selectionMap = scala.collection.mutable.Map[Integer, String]()
    for ((k, v) <- map) {
      selectionMap += k -> v
    }
    selectionMap
  }


}
