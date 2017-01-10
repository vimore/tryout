package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{Beacons, Constants}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map => MutableMap}


/**
 * DAO to access beacon activity table.
 *
 */
class BeaconsDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[BeaconsDao])
  final val suppressionDao = new SuppressionDao(conf)

  def getBeacons(eventTimeStr: String, periodSecondsInt: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val suppressionList = suppressionDao.getSuppressionList
    var suppressionCount = 0

    //NOTE: Need to refactor common functionality to a separate class. See other DAOs
    val tableName = Beacons.getName(conf)
    val sqlStr = "select " + "*" + " from " + tableName + " where " + Beacons.EVENT_TIME + " = ? and " + Beacons.PERIOD_SECONDS + "  = ? "
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, eventTimeStr)
      pstmt.setInt(2, periodSecondsInt)
      rs = executeQuery(pstmt)

      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        // We don't know if sourceNameOrIp will be an ip, host or even a username, so use it as the value to check for all three
        val sourceNameOrIp: String = selectionMap.getOrElse("sourceNameOrIp", "").asInstanceOf[String]
        if (suppressionDao.shouldSuppress(suppressionList, sourceNameOrIp, sourceNameOrIp, sourceNameOrIp, "Beaconing", false)) {
          suppressionCount += 1
        } else {
          buf += selectionMap
        }
      }
    } catch {
      case e: Exception => LOGGER.error("Failed to get Beacons for => " + eventTimeStr + " and period => " + periodSecondsInt +
        " seconds => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getBeaconingSeries(startTimeStr: String, endTimeStr: String, periodSecondsInt: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val suppressionList = suppressionDao.getSuppressionList
    var suppressionCount = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val tableName = Beacons.getName(conf)
      val sqlStr = "select " + Beacons.columns + " from " +
        tableName + " where " + Beacons.EVENT_TIME + "  >= ? and " + Beacons.EVENT_TIME + "  <= ? and " + Beacons.PERIOD_SECONDS + "  = ? "
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setInt(3, periodSecondsInt)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        val source: String = selectionMap("sourceNameOrIp").asInstanceOf[String]
        // For suppression, we don't know if the value of source is an ip, or what.  So use it for all the
        // possible things to suppress.
        if (suppressionDao.shouldSuppressNoBehaviorInfo(suppressionList, source, source, source, false)) {
          suppressionCount += 1
        } else {
          buf += selectionMap
        }
      }
    } catch {
      case e: Exception => LOGGER.error("Failed to get Beaconing Series => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * QUeries and
   * @param startTime
   * @param endTime
   * @return
   */
  def BeaconsDay(startTime: String, endTime: String) = {
    val allResults = collection.mutable.Map[String, collection.mutable.ListBuffer[MutableMap[String, Any]]]()
    val suppressionList = suppressionDao.getSuppressionList
    var suppressionCount = 0

    val tableName = Beacons.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val sqlStr = "select " + Beacons.columns + " from " + tableName + " where " + Beacons.EVENT_TIME + " >= ? " + " AND " +
        Beacons.EVENT_TIME + " < ? "
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        var selectionMap = collection.mutable.Map[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        val source: String = selectionMap("sourceNameOrIp").asInstanceOf[String]
        // For suppression, we don't know if the value of source is an ip, or what.  So use it for all the
        // possible things to suppress.
        if (suppressionDao.shouldSuppress(suppressionList, source, source, source, "Beaconing", false)) {
          suppressionCount += 1
        } else {
          val destination = selectionMap("destinationNameOrIp")
          val beacon_id = source + "->" + destination
          if (!allResults.contains(beacon_id)) {
            allResults += beacon_id -> collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
          }
          allResults(beacon_id) += selectionMap
        }
      }
    } catch {
      case e: Exception => LOGGER.error("Failed to get Beacons between  => " + startTime + " and => " + endTime +
        " => "  + e)
    } finally {
      closeConnections(conn)
    }
    var buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    for (beacon_id <- allResults.keys) {
      buf += AggregateBeacons(allResults(beacon_id))
    }
    buf
  }

  def AggregateBeacons(beaconList: collection.mutable.ListBuffer[MutableMap[String, Any]]): MutableMap[String, Any] = {
    // String constants for accessing beacon fields.
    val HOURS_ACTIVE = "hoursActive" // TODO(mdeshon): Plumb this field through in Hbase
    val EVENT_TIME = "eventTime"
    val CONFIDENCE = "confidence"
    val PERIOD_SECONDS = "periodSeconds"
    val SYSLOG_JSON = "syslogJson"
    // Variable definitions.
    var result = MutableMap[String, Any]()
    var hours_active = collection.mutable.ListBuffer.empty[String]
    for (beacon <- beaconList) {
      if (beacon(PERIOD_SECONDS) == 86400) {
        // Daily run takes precedence.
        result = beacon
        if (beacon.contains(HOURS_ACTIVE)) {
          result += HOURS_ACTIVE -> beacon(HOURS_ACTIVE) // Already a tuple string.
        }
      } else {
        // Hourly result.
        hours_active += beacon(EVENT_TIME).toString
        if (!result.contains(CONFIDENCE)) {
          result = beacon
        } else {
          // Accumulate all the log messages.
          result(SYSLOG_JSON) += beacon(SYSLOG_JSON).toString
        }
      }
    }
    if (!result.contains(HOURS_ACTIVE)) {
      // hours_active was not set by a daily result, so we want to add the accumulated hourly
      // results instead.
      result += HOURS_ACTIVE -> hours_active
    }
    result
  }

  // Need to return a set of hours between two timestamps
  //def




  def getEntityCardsForBeacons(startTime: String, endTime: String, ip: String, userName: String, cache: FeatureServiceCache,
                               buf: ListBuffer[MutableMap[String, Any]],
                               riskScore: Double ) = {
    if(ip != null && ip.nonEmpty) {
      val tableName = Beacons.getName(conf)
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        val sqlStr = "select " + Beacons.EVENT_TIME +","+ Beacons.SOURCE_NAME_OR_IP + ", " + Beacons.DESTINATION_NAME_OR_IP + "," +
          Beacons.PERIOD_SECONDS + ", " +
          Beacons.INTERVAL + " FROM " + tableName + " where " +
          Beacons.EVENT_TIME + " " + ">= ? " + " AND " +
          Beacons.EVENT_TIME + " < ? " + " AND " +
          Beacons.SOURCE_NAME_OR_IP + " = ? " + " ORDER BY  " + Beacons.EVENT_TIME + " ASC "
        conn = getConnection(conf)
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, ip)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val dateTime = rs.getString(Beacons.EVENT_TIME.toString)
          val sourceIp = rs.getString(Beacons.SOURCE_NAME_OR_IP.toString)
          val destinationIp = rs.getString(Beacons.DESTINATION_NAME_OR_IP.toString)
          val interval = rs.getDouble(Beacons.INTERVAL.toString)
          val periodSeconds = rs.getInt(Beacons.PERIOD_SECONDS.toString)
          val modelId = Constants.BeaconModelTuple._1
          val securityEventId = Constants.BeaconBotSecurityEventId
          val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
          val killchainId = secEventData.getKillchainId
          val cardId = secEventData.getCardId
          val featureDesc = secEventData.getEventType
          val secEventType = secEventData.getEventType
          val obj: EntityEventInfo = new EntityEventInfo(sourceIp,destinationIp, null,null, modelId, securityEventId, killchainId)

          //compute whether the signal is from daily/hourly job
          var isDaily = true
          if(periodSeconds == 3600){
            isDaily = false
          }

          val selectionMap = collection.mutable.Map[String, Any]()
          selectionMap += "dateTime" -> dateTime
          selectionMap += "sourceIp" -> sourceIp
          selectionMap += "destinationIp" -> destinationIp
          selectionMap += "modelId" -> modelId
          selectionMap += "eventId" -> securityEventId
          selectionMap += "securityEventType" -> secEventType
          selectionMap += "featureDesc" -> featureDesc
          selectionMap += "killchainId" -> killchainId
          selectionMap += "cardId" -> cardId
          selectionMap += "interval" -> interval
          selectionMap += "sourceUserName" -> null
          selectionMap += "riskScore" -> riskScore
          selectionMap += "isDaily" -> isDaily
          buf += selectionMap

        }
      } catch {
        case e: Exception => LOGGER.error("Failed to get entity cards details for Beacon  => " + e)
      } finally {
        closeConnections(conn)
      }

    }

  }
}

object BeaconsDao {}
