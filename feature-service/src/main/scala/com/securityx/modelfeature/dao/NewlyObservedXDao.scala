package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util

import com.securityx.modelfeature.utils.{MiscUtils, NewlyObserved}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.FeatureServiceCache
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 3/23/15.
 */
class NewlyObservedXDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[NewlyObservedXDao])

  /**
   * Finds the newly observed Xs within a given time range
   * @param startTime String specifying startTime
   * @param endTime String specifying endTime
   * @param cache FeatureServiceCache
   * @return
   */
  def getNOX(startTime: String, endTime: String, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val webFieldMap: util.Map[String, String] = cache.getWebSummaryFieldMap
      val adFieldMap: util.Map[String, String] = cache.getAdSummaryFieldMap
      conn = getConnection(conf)

      val sql = "select  field_name, COUNT(1) as COUNT from " + NewlyObserved.getName(conf) +
        " where " +
        NewlyObserved.DATE_TIME + " >= ?" + " AND " +
        NewlyObserved.DATE_TIME + " < ? " + " group by field_name "

      pstmt = getPreparedStatement(conn, sql)
      val startTimeSeparated = MiscUtils.getYMDSeparatedString(startTime)
      val endTimeSeparated =  MiscUtils.getYMDSeparatedString(endTime)
      pstmt.setString(1, startTimeSeparated)
      pstmt.setString(2, endTimeSeparated)
      Logger.info("getNOX() executing query [" + sql + "] params [" + startTimeSeparated + ", " + endTimeSeparated + "]")
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val selectionMap = collection.mutable.Map[String, Any]()
        val fieldName = rs.getString(NewlyObserved.FIELD_NAME.toString)
        var fieldLocal = ""
        if (webFieldMap.containsKey(fieldName)) {
          fieldLocal = webFieldMap.get(fieldName)
        } else if (adFieldMap.containsKey(fieldName)){
          fieldLocal = adFieldMap.get(fieldName)
        } else {
          fieldLocal = fieldName
        }

        val count = rs.getInt(NewlyObserved.COUNT.toString)
        selectionMap += "fieldName" -> fieldLocal
        selectionMap += "count" -> count
        buf += selectionMap

      }
    } catch {
      case ex: Exception => Logger.error("Failed to get NOX data => " + ex)
    } finally {
      closeConnections(conn)
    }

    buf

  }
}