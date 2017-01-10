package com.securityx.modelfeature.dao

import java.sql.{ResultSet, PreparedStatement, Connection}

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{MiscUtils, DataIngestionStats}
import org.joda.time.DateTimeZone
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by sachinkapse on 29/01/16.
 */
class DataIngestionDao (conf: FeatureServiceConfiguration) extends BaseDao(conf){


  private final val Logger: Logger = LoggerFactory.getLogger(classOf[DataIngestionDao])

  /**
   * Returns the Data Ingestion statistics for web_proxy_mef, iam_mef and endpoint_mef (for given period of time if
   * time period is provided).
   *
   * @param startTime String Optional
   * @param endTime String Optional
   * @param dataIngestionMap Map storing results for "totalDataIngestion" and "currentDataIngestion"
   *
   */
  /**
   * Returns the Data Ingestion statistics for web_proxy_mef, iam_mef and endpoint_mef (for given period of time if
   * time period is provided).
   *
   * @param startTime String Optional
   * @param endTime String Optional
   * @param dataIngestionMap Map storing results for "totalDataIngestion" and "currentDataIngestion"
   *
   */
  def populateDataIngestionMap(startTime: String, endTime: String, dataIngestionMap: MutableMap[String, Any]) = {

    val totalDataIngestionMap = MutableMap[String, Long]()
    val currentDataIngestionMap = MutableMap[String, Long]()
    var totalDataIngestion = 0L
    var totalCurrentDataIngestion = 0L
    var totalWebDataIngestion = 0L
    var totalIamDataIngestion = 0L
    var totalEndpointDataIngestion = 0L
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      val selectStr = "SELECT " + DataIngestionStats.DATE_TIME + ", " + DataIngestionStats.LOG_TYPE+ ", " +
        " SUM("+DataIngestionStats.BYTES_INGESTED+") AS TOTAL_BYTES_INGESTION" +
        " FROM " + DataIngestionStats.getName(conf) +
        " Where " + DataIngestionStats.DATE_TIME + " < ? " +
        " AND "  +
        " ( " +
        DataIngestionStats.LOG_TYPE + " = ? " +
        " OR " + DataIngestionStats.LOG_TYPE + " = ? " +
        " OR " + DataIngestionStats.LOG_TYPE + " = ? " +
        " ) " +
        " GROUP BY " +  DataIngestionStats.DATE_TIME  + ", " + DataIngestionStats.LOG_TYPE

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, selectStr)
      pstmt.setString(1, endTime)
      pstmt.setString(2, "web_proxy_mef_daily")
      pstmt.setString(3, "iam_mef_daily")
      pstmt.setString(4, "endpoint_mef_daily")
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val dateTime = rs.getString(DataIngestionStats.DATE_TIME.toString)
        val logType = rs.getString(DataIngestionStats.LOG_TYPE.toString)
        val dataIngested = rs.getLong("TOTAL_BYTES_INGESTION")
        var isCurrent = false
        if( dateTime >= startTime && dateTime < endTime ) {
          isCurrent = true
        }

        var logName = ""
        if (logType.equals("web_proxy_mef_daily")) {
          logName = "network"
          totalWebDataIngestion += dataIngested
        } else if (logType.equals("iam_mef_daily")) {
          logName = "access"
          totalIamDataIngestion += dataIngested
        } else if (logType.equals("endpoint_mef_daily")) {
          logName = "endpoint"
          totalEndpointDataIngestion += dataIngested
        }

        if(isCurrent) {
          currentDataIngestionMap += logName -> dataIngested
          totalCurrentDataIngestion += dataIngested
          currentDataIngestionMap += "total" -> totalCurrentDataIngestion
        }

        totalDataIngestion += dataIngested
      }

      totalDataIngestionMap += "total" -> totalDataIngestion
      totalDataIngestionMap += "network" -> totalWebDataIngestion
      totalDataIngestionMap += "access" -> totalIamDataIngestion
      totalDataIngestionMap += "endpoint" -> totalEndpointDataIngestion

      dataIngestionMap += "totalDataIngestion" -> totalDataIngestionMap;
      dataIngestionMap += "currentDataIngestion" -> currentDataIngestionMap;

    } catch {
      case e: Exception => Logger.error("Failed to get Data Ingestion Details  => " + e)
    } finally {
      closeConnections(conn)
    }
    totalDataIngestionMap
  }

}
