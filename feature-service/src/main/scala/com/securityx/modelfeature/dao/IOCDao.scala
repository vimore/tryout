package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.solr.SearchSolrDao
import com.securityx.modelfeature.utils._
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 12/5/14.
 */
class IOCDao(conf: FeatureServiceConfiguration, solrServerClient: CloudSolrServer) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[IOCDao])
  private val searchDao: SearchSolrDao = new SearchSolrDao(solrServerClient,solrServerClient)

  private val facetDao: FacetDao = new FacetDao

  def getIOCList() = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = IocList.getName(conf)
    val sqlStr = "SELECT " + IocList.columns + " FROM " + tableName
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf

  }

  /**
   *
   * @param startTime String specifying start Time
   * @param endTime String specifying end Time
   * @return
   */
  def getIOCResults(startTime: String, endTime: String) = {
    val buff = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val entityFusionHourlyRollUpDao: EntityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf)

      val startDate: String = MiscUtils.getYMDString(startTime)
      val endDate: String = MiscUtils.getYMDString(endTime)

      //Querying IOC_List to get IOC data
      val sqlStr = "select " + IocList.columns + " from " + IocList.getName(conf) + " where " + IocList.DATE_TIME + " >= ? and " +
        IocList.DATE_TIME + " < ? order by LAST_DATE_TIME desc"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startDate)
      pstmt.setString(2, endDate)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buff += selectionMap

        val sourceAddress: String = rs.getString(IocList.SOURCE_ADDRESS.toString)
        val lastDateTime: String = rs.getString(IocList.LAST_DATE_TIME.toString)
        val entityInfo = entityFusionHourlyRollUpDao.getEntityInfoFromFusionForIp(sourceAddress, lastDateTime)
        selectionMap += "userName" -> {
          if (entityInfo == null) "" else entityInfo.getUserName
        }
        selectionMap += "hostName" -> {
          if (entityInfo == null) "" else entityInfo.getHostName
        }

        val threatType: String = rs.getString(IocList.THREAT_TYPE.toString)
        var urls: String = null
        var domains: String = null
        val ips: String = null
        if (threatType.equalsIgnoreCase("url")) {
          urls = rs.getString(IocList.THREAT_VALUE.toString)
        } else if (threatType.equalsIgnoreCase("domain")) {
          domains = rs.getString(IocList.THREAT_VALUE.toString)
        }
        selectionMap += "history" -> getIocCounts(endTime, urls, domains, ips, sourceAddress)

      }

    } catch {
      case ex: Exception => Logger.error("Failed to get IOC Results: ", ex)
    } finally {
      conn.close()
    }
    buff
  }


  /**
   *
   * @param startTime
   * @return
   */
  def getIocSummary(startTime: String, endTime: String) = {
    val resultMap = MutableMap[String, Any]()
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      val startDate: String = MiscUtils.getYMDString(startTime)
      val sqlStr: String = "select MAX(" + IocSummary.DATE_TIME + ") as day, " + "MAX(" + IocSummary.DATE_TIME_RUN + ") as " +
        "time, " + " SUM(" + IocSummary.DOMAINS + ") as domains, " + "SUM(" + IocSummary.NEW_DOMAINS + ") as new_domains, " +
        "" + "SUM(" + IocSummary.DOMAIN_HITS + ") as domain_hits, " + "SUM(" + IocSummary.NEW_DOMAIN_HITS + ") as " +
        "new_domain_hits, " + "SUM(" + IocSummary.URLS + ") as urls, " + "SUM(" + IocSummary.NEW_URLS + ") as new_urls, " +
        "SUM(" + IocSummary.URL_HITS + ") as url_hits, " + "SUM(" + IocSummary.NEW_URL_HITS + ") as new_url_hits From " +
        IocSummary.getName(conf) + " where " + IocSummary.DATE_TIME + " = ? "

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startDate)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        appendToMap(rs, rsMeta, resultMap)
        populateIocErrorStatus(startTime, endTime, resultMap)
      }

    } catch {
      case ex: Exception => Logger.error("Failed to get IOC Summary: ", ex)
    } finally {
      closeConnections(conn)
    }
    resultMap

  }

  /**
   * Returns the counts of iocs in the last 30 days
   * @param endTime
   * @return
   */
  def getIocCounts(endTime: String, urls: String, domains: String, ips: String, sourceAddress: String) = {
    facetDao.getEventCounts(solrServerClient, endTime, urls, domains, ips, sourceAddress, Constants.LAST_N_DAYS)
  }

  /**
   * Lists 30 days events for the specified urls and domains
   * @param ips
   * @param domains
   * @param dateTime
   * @return
   */
  def getHistoryForIOC(ips: String, domains: String, urls: String, dateTime: String, sourceAddress: String, lastNDays: Int) = {
    searchDao.getLogsForIPsDomains(solrServerClient, ips, domains, urls, sourceAddress, dateTime, lastNDays);
  }

  /**
   *
   * @param blob
   * @param time
   * @param lastNDays
   * @return
   */
  def getIOCDataFromBlob(blob: String, time: String, lastNDays: Int, conf: FeatureServiceConfiguration) = {
    searchDao.getLogsFromBlob(solrServerClient, blob, time, lastNDays, conf);
  }

  /**
   * Queries the ioc_error_status table and gets the errors during parsing the ioc uploaded file.
   *
   * @param startTime String specifying startTime
   * @param endTime String specifying endTime
   *
   */
  private def populateIocErrorStatus(startTime: String, endTime: String, selectionMap: MutableMap[String, Any]) = {
    val tableName = IocErrorStatus.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val startDate: String = MiscUtils.getYMDString(startTime)
      val endDate: String = MiscUtils.getYMDString(endTime)
      val sqlStr = "SELECT  * FROM " + tableName +
        " where " + IocErrorStatus.DATE_TIME + " >= ? " +
        " and " + IocErrorStatus.DATE_TIME + " <  ?" + " ORDER BY " + IocErrorStatus.DATE_TIME;
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startDate)
      pstmt.setString(2, endDate)
      rs = executeQuery(pstmt)
      var fileName = ""
      val errorBuf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
      while (rs.next()) {
        val errorMap = MutableMap[String, Any]()
        errorMap += "errorDescription" -> rs.getString(IocErrorStatus.ERROR_DESCRIPTION.toString)
        errorMap += "inputRow" -> rs.getString(IocErrorStatus.INPUT_ROW.toString)
        errorMap += "lineNumber" -> rs.getInt(IocErrorStatus.LINE_NUMBER.toString)
        errorBuf += errorMap

        //In the IOC_ERROR_STATUS table, there will be only records for the last file uploaded, for a given day
        //So we get the last fileName and add it to the result.
        if (fileName.isEmpty) {
          fileName = rs.getString(IocErrorStatus.FILE_NAME.toString)
          selectionMap += "fileName" -> fileName
        }

      }

      //Adding Error status to the result map
      selectionMap += "error" -> errorBuf

    } catch {
      case ex: Exception => Logger
                            .error("Failed to get ioc error status between " + startTime + " and " + endTime + " => " + ex)
    } finally {
      closeConnections(conn)
    }
  }

}
