/**
 * DAO for Coordinate Activity (aka Botnet)
 */

package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.utils.CoordActivity
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}


class CoordActivityDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[CoordActivityDao])
  val suppressionDao = new SuppressionDao(conf)
  val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)

  def getEventsInTime(startTime: String, endTime: String, topN: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = CoordActivity.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      val sqlStr = "SELECT " + CoordActivity.columns + " FROM " + tableName +
        " WHERE " + CoordActivity.DATE_TIME + "  >= ? and " + CoordActivity.DATE_TIME + "  < ? ORDER BY " +
        CoordActivity.ANOMALY_EDGE_SCORE +
        " DESC " + "limit ?"
      LOGGER.debug(sqlStr)
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, topN)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val sourceNameOrIp = rs.getString("source_name_or_ip")
        val destNameOrIp = rs.getString("destination_name_or_ip")
        // We don't know if we have names or ips, so pass the string as both the hostname and the ip.  We pass
        // the empty string for username, since we know nothing about it. Behavior will always be Coordinated Activity.
        if (suppressionDao.shouldSuppress(suppressionList, sourceNameOrIp, sourceNameOrIp, "", "Coordinated Activity", false) ||
          suppressionDao.shouldSuppress(suppressionList, destNameOrIp, destNameOrIp, "", "Coordinated Activity", false)) {
          suppressionCount += 1
        } else {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          buf += selectionMap
        }
      }
    } catch {
      case e: Exception => LOGGER.error(
        "Failed to get Coordinated Activity Events between => " + startTime + " and => " + endTime + " => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getCoordActivityEntities(startTime: String, endTime: String, id: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = CoordActivity.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val sqlStr = "SELECT " + CoordActivity.SOURCE_NAME_OR_IP + " FROM " + tableName +
        " WHERE " + CoordActivity.DATE_TIME + " >= ? and " + CoordActivity.DATE_TIME + "  <= ? and " + CoordActivity.CLUSTER_ID + " = ?";
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, id)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => LOGGER.error(
        "Failed to get Coordinated Activity Events for cluster_id => " + id + " => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getCoordActivityEntitiesParallel(startTime: String, endTime: String, id: Integer) = {
    var users: String = ""
    val ents = getCoordActivityEntities(startTime, endTime, id)
    ents.foreach { x =>
      for ((key, value) <- x) {
        val str: String = value.asInstanceOf[String]
        users = if (users.isEmpty) "\"" + str + "\"" else users + ", " + "\"" + str + "\""
      }
    }

    val ip_list: String = users.asInstanceOf[String]
    if (ip_list == null || ip_list.length == 0) {
      // Return empty result.
      LOGGER.debug("Empty IP list returned.")
      val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
      buf
    } else {
      val buf = hostEntityPropertiesDao.getEntHostPropsByIpList(ip_list, endTime)
      var entries = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
      entries = buf.map(x => scala.collection.mutable.Map[String, Any](
        "dateTime" -> {
          if (x.contains("dateTime") && !x.get("dateTime").equals(Some(null))) x.get("dateTime") else null
        },
        "ipAddress" -> {
          if (x.contains("ipAddress") && !x.get("ipAddress").equals(Some(null))) x.get("ipAddress") else null
        },
        "macAddress" -> {
          if (x.contains("macAddress") && !x.get("macAddress").equals(Some(null))) x.get("macAddress") else null
        },
        "country" -> {
          if (x.contains("country") && !x.get("country").equals(Some(null))) x.get("country") else null
        },
        "city" -> {
          if (x.contains("city") && !x.get("city").equals(Some(null))) x.get("city") else null
        },
        "os" -> {
          if (x.contains("os") && !x.get("os").equals(Some(null))) x.get("os") else null
        },
        "browserName" -> {
          if (x.contains("browserName") && !x.get("browserName").equals(Some(null))) x.get("browserName") else null
        },
        "browserVersion" -> {
          if (x.contains("browserVersion") && !x.get("browserVersion").equals(Some(null))) x.get("browserVersion") else null
        },
        "hostName" -> {
          if (x.contains("hostName") && !x.get("hostName").equals(Some(null))) x.get("hostName") else null
        },
        "primaryUserId" -> {
          if (x.contains("primaryUserId") && !x.get("primaryUserId").equals(Some(null))) x.get("primaryUserId") else null
        },
        "risk" -> {
          if (x.contains("risk") && !x.get("risk").equals(Some(null))) x.get("risk") else null
        }
      ))
      buf
    }
  }
}


object CoordActivityDao {}