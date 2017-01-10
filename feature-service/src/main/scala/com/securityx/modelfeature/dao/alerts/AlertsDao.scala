package com.securityx.modelfeature.dao.alerts

import java.util
import java.util.{Optional, UUID}

import com.securityx.modelfeature.alert.AlertHandler
import com.securityx.modelfeature.alert.AlertHandler.ALERT_STATE
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger
import com.securityx.modelfeature.alert.zookeeper.ZookeeperClient
import com.securityx.modelfeature.common.AlertLog
import com.securityx.modelfeature.common.cache.AlertCache
import com.securityx.modelfeature.common.inputs.AlertDefinition
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions
import scala.collection.JavaConverters._
import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 5/21/15.
 */
class AlertsDao(conf: FeatureServiceConfiguration) extends AlertsBaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[AlertsDao])


  def getAlerts(alertCache: AlertCache) = {
    getValidAlertsFromCache(alertCache)
  }

  def createAlert(alert: AlertDefinition, alertCache: AlertCache,
                  zookeeperClient : ZookeeperClient, alertLogger: AlertAuditLogger) = {
    if (!alertCache.getAlertsMap.isEmpty) {
      // delete existing alerts.  Note that we clone the key set - this prevents a concurrent
      // modification exception when we delete the alerts.  It's not clear to me why we may
      // have more than one alert - I believe there should be only one at a time, as this is
      // the setting that we display in the alert setting page.
      val alertIds = alertCache.getKeys.asScala.clone()
      for (id <- alertIds) {
        deleteAlert(id, alertCache, zookeeperClient, alertLogger)
      }
    }

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val dateTime = DateTime.now()
    val zookeeperAlertStore : ZooKeeperAlertStore = new ZooKeeperAlertStore(zookeeperClient)
    //check if the alert already exists.
    // If it does, just fire an update query on the same alertId,
    // else create a new alertId
    var uuid = ""
    if(alertCache.getAlertsMap.containsKey(alert.getAlertId)){
      uuid = alert.getAlertId
    }else{
      uuid = UUID.randomUUID().toString
      //update the alertId of the incoming AlertDefinition
      alert.setAlertId(uuid)
    }
    val alertDef: Optional[AlertDefinition] = zookeeperAlertStore.createAlert(alert)
    if(alertDef.isPresent) {
      val alertAuditLog: AlertLog = new AlertLog(dateTime.toString, alert,null, ALERT_STATE.CREATED, null, null)
      alertLogger.logAlert(alertAuditLog)

      // return alertId
      val selectionMap = MutableMap[String, Any]()
      selectionMap += "alertId" -> alert.getAlertId
      buf += selectionMap

      Logger.info("Added Alert... ")
    }
    buf
  }

  def updateAlert(alert: AlertDefinition, alertCache: AlertCache, zookeeperClient: ZookeeperClient,
                  alertLogger: AlertAuditLogger) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val dateTime = DateTime.now()
    val zookeeperAlertStore: ZooKeeperAlertStore = new ZooKeeperAlertStore(zookeeperClient)
      val alertDef: Optional[AlertDefinition] = zookeeperAlertStore.updateAlert(alert)
      if (alertDef.isPresent) {
        val alertAuditLog: AlertLog = new AlertLog(dateTime.toString, alert,null, ALERT_STATE.MODIFIED, null, null)
        alertLogger.logAlert(alertAuditLog)

        // return alertId
        val selectionMap = MutableMap[String, Any]()
        selectionMap += "alertId" -> alert.getAlertId
        buf += selectionMap
        Logger.info("Updated Alert... ")
      }
    buf
  }

  def deleteAlert(alertId: String, alertCache: AlertCache, zookeeperClient: ZookeeperClient, alertLogger: AlertAuditLogger) = {

    val dateTime = DateTime.now()
    val zookeeperAlertStore: ZooKeeperAlertStore = new ZooKeeperAlertStore(zookeeperClient)
    var deleted = true
    try {
        val alert : AlertDefinition = alertCache.getAlertsMap.get(alertId)
        zookeeperAlertStore.deleteAlert(alert.getAlertId)
        Logger.info("Deleted Alert... ")
        //logging
        val alertAuditLog: AlertLog = new AlertLog(dateTime.toString, alert, null, ALERT_STATE.DELETED, null, null)
        alertLogger.logAlert(alertAuditLog)
    } catch {
      case ex: Exception => {
        Logger.error("Failed to Delete Alert => " + ex)
        deleted = false
      }
    }
    deleted
  }

  def getValidAlertsFromCache(alertCache: AlertCache) = {
    alertCache.getAlertsMap.values()
  }

  /**
   * Given alert Id, finds all the alert Destinations for this alert
   *
   * @param alertId String alertId
   * @param alertCache AlertCache
   * @return List of AlertDestinations for the given alertId
   */
  def getValidAlertDestinationList(alertId: String, alertCache: AlertCache) = {

    var buf: util.List[String] = new util.LinkedList[String]()
    if(alertCache.getAlertsMap.containsKey(alertId)){
      val alertDef: AlertDefinition = alertCache.getAlertsMap.get(alertId)
      buf= alertDef.getAlertDestination
    }

    buf
  }


  def getValidAlertsFromDb(zooKeeperAlertStore: ZooKeeperAlertStore) = {
    val buf =  MutableMap[String, AlertDefinition]()

    try {
        val alert : Optional[AlertDefinition] = zooKeeperAlertStore.getAlert
         if(alert.isPresent){
           buf += alert.get().getAlertId -> alert.get()
         }
    } catch {
      case ex: Exception => Logger.error("Failed to get Alerts => " + ex)
    }
    buf
  }


  def getAlertFrequencies = {
    AlertHandler.AlertFequencies.values()
  }


}
