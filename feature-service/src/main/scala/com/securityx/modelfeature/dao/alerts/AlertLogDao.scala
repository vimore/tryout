package com.securityx.modelfeature.dao.alerts

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util.Optional

import com.securityx.modelfeature.alert.AlertHandler.ALERT_STATE
import com.securityx.modelfeature.common.AlertLog
import com.securityx.modelfeature.common.inputs.AlertDefinition
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{AlertAuditLog, JsonUtils}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

class AlertLogDao(conf : FeatureServiceConfiguration) extends  AlertsBaseDao(conf){

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[AlertLogDao])

  def logAlert(alertLog: AlertLog) = {
    val alert: AlertDefinition = alertLog.getAlert
    val alertString : Optional[String] = JsonUtils.toJsonString(alert)
    if(alertString.isPresent) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      try {
        conn = getConnection(conf)
        val sql = "UPSERT INTO " + AlertAuditLog.getName(conf) + " VALUES( ? , ? , ?, ?, ? , ? )"

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, alertLog.getDateTime)
        pstmt.setString(2, alertString.get())
        pstmt.setString(3, alertLog.getAlertDestination)
        pstmt.setString(4, alertLog.getAlertState.name())
        pstmt.setString(5, alertLog.getAlertLog)
        pstmt.setString(6, alertLog.getErrorLog)

        executeUpdate(pstmt)

        Logger.info("Logged Alert in db ... ")
        /*
        Logger.info("statement"+pstmt.toString())
        Logger.info("arg 1 " + alertLog.getDateTime());
        Logger.info("arg 2 " + alertString.get());
        Logger.info("arg 3 " + alertLog.getAlertDestination());
        Logger.info("arg 4 " + alertLog.getAlertState());
        Logger.info("arg 5 " + alertLog.getAlertLog());
        Logger.info("arg 6 " + alertLog.getErrorLog());
        */

      } catch {
        case ex: Exception => Logger.error("Failed to log Alert => " + ex)
      } finally {
        closeConnections(conn)
      }
    }
  }


  def getAlertLogs() = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sql = "Select * From " + AlertAuditLog.getName(conf)

      pstmt = getPreparedStatement(conn, sql)

      rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Alert-logs => " + ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * Checks for the latest alert sent with Status ALERT_STATE.DELIVERED
   * @return
   */
  def getLogsForLatestAlertSent() : Array[String] = {
    var logs : String = null
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sql = "Select * From " + AlertAuditLog.getName(conf) +
        " WHERE " + AlertAuditLog.ALERT_STATE + " = '" + ALERT_STATE.DELIVERED  + "'" +
        " ORDER BY " + AlertAuditLog.DATE_TIME

      pstmt = getPreparedStatement(conn, sql)

      rs = executeQuery(pstmt)
      while (rs.next()) {
        logs = rs.getString(AlertAuditLog.ALERT_LOG.toString)
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Alert-logs => " + ex)
    } finally {
      closeConnections(conn)
    }
    if(logs != null) {
      logs.split(System.lineSeparator())
    }else{
      null
    }
  }
}
