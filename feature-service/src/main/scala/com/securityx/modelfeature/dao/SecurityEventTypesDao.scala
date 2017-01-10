package com.securityx.modelfeature.dao

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, SecurityEventTypeConfiguration}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}


/**
 * Created by harish on 11/15/14.
 */
class SecurityEventTypesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[SecurityEventTypesDao])

  /**
   *
   * @param killchainId Int killchainid
   * @return returns SecurityEventTypes associated with the input killchain Id
   */
  def getSecurityEventTypesFromKillchainId(killchainId: Int, cache: FeatureServiceCache) = {
    val map = cache.getKillchainToSecurityEventDataMap()
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (map.contains(killchainId)) {
      val secEventsList: ListBuffer[SecurityEventTypeConfiguration] = map(killchainId)
      secEventsList.foreach { secEvent =>
        val selectionMap = MutableMap[String, Any]()
        selectionMap += "type" -> secEvent.getEventType
        selectionMap += "typePrefix" -> secEvent.getTypePrefix
        selectionMap += "securityEventTypeId" -> secEvent.getSecurityEventTypeId
        selectionMap += "killchainId" -> killchainId
        buf += selectionMap
      }
    }
    buf
  }


}
