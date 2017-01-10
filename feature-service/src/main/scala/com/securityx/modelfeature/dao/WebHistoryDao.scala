package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.utils.{Constants, MiscUtils, WebBehaviour}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.solr.WebAnomalyProfileSolrDao
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.joda.time.{DateTime, DateTimeZone, Days}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable.{Map => MutableMap}

/**
 * DAO to access web history table.
 *
 */
class WebHistoryDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[WebHistoryDao])
  private val webAnomalyProfileSolrDao: WebAnomalyProfileSolrDao = new WebAnomalyProfileSolrDao()

  def getAnomalyProfiles(startTime: String, endTime: String, periodInt: Integer, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null

    val nameMappings = this.getAnomalyProfileNameMappings(cache)
    val tableName = WebBehaviour.getName(conf)
    val sqlStr = "SELECT " + WebBehaviour.columns + " FROM " + tableName + " WHERE " +
      WebBehaviour.DATE_TIME + "  >= ? and " + WebBehaviour.DATE_TIME + " <= ? AND " + WebBehaviour.PERIOD_SECONDS + " = ? " +
      " ORDER BY " + WebBehaviour.RISK_SCORE + " DESC";
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, periodInt)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        val raw_profile: String = rs.getString(WebBehaviour.ANOMALY_PROFILE.toString)
        var mapping = MutableMap[String, String]()
        appendToMap(rs, rsMeta, selectionMap)
        if (nameMappings.contains(raw_profile)) {
          // There is an exactly matching profile, so we use that type and category.
          // Event categories in nameMappings are already canonicalized.
          mapping = nameMappings.get(raw_profile).get.asScala
        } else {
          // If there's no exactly matching profile, we map the individual profile elements
          // to human-readable versions.
          val elements: Array[String] = raw_profile.split(" ")
          val converted: Array[String] = elements.map(x => getSingleReadable(x, nameMappings))
          mapping += "eventType" -> converted.mkString(" ")
          // We don't do a single-label mapping to an event category, instead we rely on the default
          // as set below.
        }
        if (nameMappings.contains("DEFAULT")) {
          val default_mapping: MutableMap[String, String] = nameMappings.get("DEFAULT").get.asScala
          if (!mapping.contains("eventType")) {
            mapping += "eventType" -> default_mapping.get("eventType").get
          }
          if (!mapping.contains("eventCategory")) {
            val category: String = getCanonicalEventCategory(default_mapping.get("eventCategory").get)
            if (category != null) {
              mapping += "eventCategory" -> category // Use the canonical name from the Enumeration.
            }
          }
        }
        if (mapping.nonEmpty) {
          selectionMap += "eventType" -> mapping.get("eventType").get
          selectionMap += "eventCategory" -> mapping.get("eventCategory").get
        }
        buf += selectionMap
      }
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }

  private def getSingleReadable(input_string: String, nameMappings: MutableMap[String, util.Map[String, String]]): String = {
    val label: String = input_string take (input_string.length() - 1)
    val direction: String = input_string takeRight 1
    var new_label: String = ""
    if (nameMappings.contains(label) && nameMappings.get(label).get.asScala.contains("eventType")) {
      new_label = nameMappings.get(label).get.get("eventType")
    } else {
      // There is no mapping for this label, so use the existing label.
      new_label = label
    }
    new_label + "(" + direction + ")"
  }

  def getAnomalyProfileNameMappings(cache: FeatureServiceCache): MutableMap[String, util.Map[String, String]] = {
    val webAnomalyProfileConfigCache = cache.getWebAnomalyProfileConfigCache
    webAnomalyProfileConfigCache.getAnomalyProfileMapping.asScala
  }

  def getRiskScoresPerAnomalyProfile(dateTime: String, anomalyProfile: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val tableName = WebBehaviour.getName(conf)
      val sqlStr = "SELECT " + WebBehaviour.RISK_SCORE + "," + WebBehaviour.IP_ADDRESS +
        "  FROM " + tableName + " WHERE " + WebBehaviour.DATE_TIME + " = ?  AND " + WebBehaviour.ANOMALY_PROFILE + "  = ? " +
        " ORDER BY " + WebBehaviour.DATE_TIME;
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, dateTime)
      pstmt.setString(2, anomalyProfile)
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

  def getEntityAnomalyProfile(dateTime: String, anomalyProfile: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = this.getEnvTableName("WEB_ANOMALY_PROFILE_BIN_COUNTS")
    val sqlStr = "SELECT IP_ADDRESS, ANOMALY_PROFILE, TYPE, SUB_TYPE, COUNT FROM " + tableName +
      " WHERE DATE_TIME = ?  AND ANOMALY_PROFILE = ? ORDER BY IP_ADDRESS, COUNT DESC";
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, dateTime)
      pstmt.setString(2, anomalyProfile)
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


  def getTopNDestinations(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                          sourceAddress: String, facetKeyValues: util.Map[String, util.List[String]],
                          startTime: String, endTime: String, facetLimit: Int, pageNo: Int, summarize: Boolean,
                          cache: FeatureServiceCache) = {
    val startDateTime: DateTime = new DateTime(startTime, DateTimeZone.UTC)
    webAnomalyProfileSolrDao.getTopNDestinations(webProxySolrClient, iamSolrClient, Constants.WebPeerAnomaliesModelTuple._1,
      facetKeyValues,
      sourceAddress, startTime, endTime.toString(), facetLimit, 0, 0, summarize, cache)
  }

  def getSearchResults(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer, modelId: Int,
                       sourceAddress: String, facetKeyValues: util.Map[String, util.List[String]],
                       startTime: String, endTime: String, endRows: Int, pageNo: Int, summaryFacetLimit: Int, sortField: String,
                       sortOrder: String, summarize: Boolean, cache: FeatureServiceCache) = {
    val startDate: String = MiscUtils.getYMDSeparatedString(startTime)

    val daysElapsed: Int = Days.daysBetween(new DateTime(startDate), DateTime.now).getDays

    if (daysElapsed > 15) new SearchImpalaDao(conf).getWebAnomalyFacetedSearchResults(modelId,
      facetKeyValues, sourceAddress, startTime, endTime, endRows, pageNo, summaryFacetLimit, sortField,
      sortOrder, summarize, cache)
    else
    webAnomalyProfileSolrDao.getFacetedSearchResults(webProxySolrClient, iamSolrClient, modelId,
      facetKeyValues,
      sourceAddress, startTime, endTime.toString(), endRows, pageNo, summaryFacetLimit, sortField,
      sortOrder, summarize, cache)
  }
}

object WebHistoryDao {}
