package com.securityx.modelfeature.dao.alerts

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger
import com.securityx.modelfeature.alert.{AlertHandler, AlertListener, AlertNotifier}
import com.securityx.modelfeature.common.inputs.AlertDestination
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{AlertDestinations, EncryptionUtil}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 5/21/15.
 */
class AlertsDestinationDao(conf: FeatureServiceConfiguration) extends AlertsBaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[AlertsDestinationDao])

  /**
   * Returns the Map of AlertDestination id and alertDestination Name
   * These are all the destinations supported in the product.
   *
   * @param cache FeatureServiceCache
   * @return Map[Integer, String]
   */
  def getAlertDestinationTypes(cache: FeatureServiceCache): util.Map[Integer, String] = {
    cache.getAlertDestinationsMap
  }

  def addAlertDestination(alertDestination: AlertDestination, alertNotifier: AlertNotifier, alertLogger: AlertAuditLogger,
                          featureServiceCache: FeatureServiceCache) = {
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var count = 0
    try {
      val encryptedPassword = EncryptionUtil.encrypt(alertDestination.getAuthPassword)
      conn = getConnection(conf)
      val sql = "UPSERT INTO " + AlertDestinations.getName(conf) + " VALUES( ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ? )"

      pstmt = getPreparedStatement(conn, sql)
      pstmt.setString(1, alertDestination.getDateTime)
      pstmt.setInt(2, alertDestination.getAlertDestinationId)
      pstmt.setString(3, alertDestination.getAlertDestinationName)
      pstmt.setString(4, if (alertDestination.getEmailFrom != null) alertDestination.getEmailFrom.toString else null)
      pstmt.setString(5, if (alertDestination.getEmailTo != null) alertDestination.getEmailTo.toString else null)
      pstmt.setString(6, if (alertDestination.getEmailCc != null) alertDestination.getEmailCc.toString else null)
      pstmt.setString(7, if (alertDestination.getEmailBcc != null) alertDestination.getEmailBcc.toString else null)
      pstmt.setString(8, alertDestination.getHostName)
      pstmt.setInt(9, alertDestination.getPort)
      pstmt.setString(10, alertDestination.getTransport)
      pstmt.setString(11, alertDestination.getAuthUserName)
      pstmt.setString(12, {if(encryptedPassword.isPresent) {encryptedPassword.get()} else ""})
      count = executeUpdate(pstmt)
      Logger.info("Added Alert Destination... ")

      //updating listener
      val alertListner: AlertListener = AlertHandler.getAlertListener(alertDestination, featureServiceCache, alertLogger)
      alertNotifier.register(alertListner)

    } catch {
      case ex: Exception => Logger.error("Failed to get Alert conf => " + ex)
    } finally {
      closeConnections(conn)
    }

    count
  }

  def getAlertDestinations = {
    val buf = new ListBuffer[AlertDestination]()
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val alertDestinationMap: MutableMap[Int, AlertDestination] = MutableMap[Int, AlertDestination]()
    try {
      conn = getConnection(conf)
      val sql = "select " + AlertDestinations.columns + " from " + AlertDestinations.getName(conf) +
        " ORDER BY " + AlertDestinations.DATE_TIME  + " ASC "

      pstmt = getPreparedStatement(conn, sql)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val id = rs.getInt(AlertDestinations.DESTINATION_ID.toString)
        val dateTime = rs.getString(AlertDestinations.DATE_TIME.toString)
        val alertDest: AlertDestination = new AlertDestination()
        alertDest.setDateTime(dateTime)
        alertDest.setAlertDestinationId(id)
        alertDest.setAlertDestinationName(rs.getString(AlertDestinations.DESTINATION_NAME.toString))

        val emailFrom = rs.getString(AlertDestinations.EMAIL_FROM.toString)
        alertDest.setEmailFrom(getListFromArrayString(emailFrom))

        val emailTo = rs.getString(AlertDestinations.EMAIL_TO.toString)
        alertDest.setEmailTo(getListFromArrayString(emailTo))

        val emailCc = rs.getString(AlertDestinations.EMAIL_CC.toString)
        alertDest.setEmailCc(getListFromArrayString(emailCc))

        val emailBcc = rs.getString(AlertDestinations.EMAIL_BCC.toString)
        alertDest.setEmailBcc(getListFromArrayString(emailBcc))

        alertDest.setHostName(rs.getString(AlertDestinations.HOST_NAME.toString))
        alertDest.setPort(rs.getInt(AlertDestinations.PORT.toString))
        alertDest.setTransport(rs.getString(AlertDestinations.TRANSPORT.toString))
        alertDest.setAuthUserName(rs.getString(AlertDestinations.AUTH_USERNAME.toString))
        alertDest.setAuthPassword(rs.getString(AlertDestinations.AUTH_PASSWORD.toString))

        alertDestinationMap += id -> alertDest
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Alert conf => " + ex)
    } finally {
      closeConnections(conn)
    }
    alertDestinationMap.foreach{kv =>
      val alertDestination = kv._2
      buf += alertDestination
    }

    buf
  }

}
