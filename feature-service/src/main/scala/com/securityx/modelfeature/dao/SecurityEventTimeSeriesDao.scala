package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.queryengine.QueryGenerator
import com.securityx.modelfeature.utils.SecurityEventTimeSeries
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 2/27/15.
 */
class SecurityEventTimeSeriesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[SecurityEventTimeSeriesDao])

  /**
   * Queries and returns the securityEventTimeSeries between a given startTime and Endtime
   *
   * @param startTime String specifying starTime
   * @param endTime String specifying endTime
   * @param cache FeatureServiceCache
   * @return
   */
  def getSecurityEventTimeSeries(startTime: String, endTime: String, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val queryJson: QueryJson = new QueryJson()
    queryJson.setStartTime(startTime)
    queryJson.setEndTime(endTime)
    getTimeSeries(queryJson, buf, cache)
    buf
  }

  /**
   *  Forms the sql query using the QueryGenerator and executes to get the SecurityEventTimeSeries data
   *
   * @param queryJson QueryJson to form the sql query
   * @param buf ListBuffer which holds the result securityEventTimeSeries
   * @param cache FeatureServiceCache
   */
  private def getTimeSeries(queryJson: QueryJson, buf: ListBuffer[MutableMap[String, Any]], cache: FeatureServiceCache) = {
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)

      //used to set params for PreparedStatement
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      var sqlStr: String = "Select " + SecurityEventTimeSeries.DATE_TIME + " , " +
        " SUM(" + SecurityEventTimeSeries.EVENT_COUNT + ") as EVENT_COUNT "  + " from " + SecurityEventTimeSeries.getName(conf)

      //get predicate part of the query
      val predicateString = getSqlPredicateString(queryJson, pstmtList, cache)
      sqlStr = sqlStr + " " + predicateString + " GROUP BY " + SecurityEventTimeSeries.DATE_TIME  +  " ORDER BY "  +
        SecurityEventTimeSeries.DATE_TIME
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)
      rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores => " + ex)
    } finally {
      closeConnections(conn)
    }
  }

  def getSqlPredicateString(inputJson: QueryJson, pstmtList: ListBuffer[ColumnMetaData], cache: FeatureServiceCache) = {
    val q: QueryGenerator = new QueryGenerator
    q.getSqlPredicateString(inputJson, pstmtList, null, null, cache, false, conf.getFixNullValue)
  }

}
