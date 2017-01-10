package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{Constants, EntityFeatures}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 7/31/15.
 */
class EntityFeaturesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[EntityFeaturesDao])

  def getEntityFeaturesByFeatureId(startTime: String, endTime: String,
                        sourceNameOrIp: String, destinationNameOrIp: String,
                        modelId: Int, securityEventId: Int, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    var conn: Connection = null
    try {
      var sqlStr: mutable.StringBuilder = new mutable.StringBuilder().append("select  SUBSTR(DATE_TIME,0,10) as DAY, SUM(FEATURE_VALUE) as COUNT from ").append(EntityFeatures.getName(conf)).
        append(" where ").append(EntityFeatures.DATE_TIME + " >= ? ").append(" AND ").
        append(EntityFeatures.DATE_TIME + " < ? ").append(" AND ").
        append(EntityFeatures.MODEL_ID + " =  ? ").append(" AND ").
        append(EntityFeatures.SECURITY_EVENT_ID + " = ? ")

      if (sourceNameOrIp != null && sourceNameOrIp.nonEmpty) {
        sqlStr = sqlStr.append(" AND ").append(EntityFeatures.SOURCE_NAME_OR_IP + " LIKE " + "'%" + sourceNameOrIp + "%'")
      }

      if (destinationNameOrIp != null && destinationNameOrIp.nonEmpty) {
        sqlStr = sqlStr.append(" AND ").append(EntityFeatures.DESTINATION_NAME_OR_IP + " LIKE " + "'%" + destinationNameOrIp + "%'")
      }

      sqlStr = sqlStr.append(" GROUP BY DAY ")

      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr.toString())
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, modelId)
      pstmt.setInt(4, securityEventId)
      val rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Entity Features => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }


  def getEntityFeaturesByFeatureLabel(startTime: String, endTime: String,
                        sourceNameOrIp: String, destinationNameOrIp: String,
                        modelId: Int, featureLabel: String, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    var conn: Connection = null
    try {
      var featureLabelStr = featureLabel
      if(modelId.equals(Constants.WebAnomalyProfileTuple._1)){
        // The issue here is that what is being passed in as a feature label for web anomalies (model 4) is not -
        // rather it is an event type.  So we need to get the security event data and get the feature label from
        // that.  This has some issues in that there are some cases in model 4 where there are multiple feature
        // labels for the same event type, but that will need to be handled in a more comprehensive fix.
        val securityEventData = cache.getSecurityEventDataFromEventTypeModelId(featureLabel, modelId)
        featureLabelStr = securityEventData.getFeatureLabel
      }
      var sqlStr: mutable.StringBuilder = new mutable.StringBuilder().append("select  SUBSTR(DATE_TIME,0,10) as DAY, SUM(FEATURE_VALUE) as COUNT from ").append(EntityFeatures.getName(conf)).
        append(" where ").append(EntityFeatures.DATE_TIME + " >= ? ").append(" AND ").
        append(EntityFeatures.DATE_TIME + " < ? ").append(" AND ").
        append(EntityFeatures.MODEL_ID + " =  ? ").append(" AND ").
        append(EntityFeatures.FEATURE_LABEL + " = ? ")

      sqlStr = sqlStr.append(getSqlPredicateByModelId(sourceNameOrIp, destinationNameOrIp, modelId))
      sqlStr = sqlStr.append(" GROUP BY DAY ")

      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr.toString())
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, modelId)
      pstmt.setString(4, featureLabelStr)
      val rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Entity Features => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getSqlPredicateByModelId(source: String, destination: String, modelId: Int): String = {
    var q: mutable.StringBuilder = new mutable.StringBuilder()
    if(Constants.WebAnomalyProfileTuple._1.equals(modelId)){
      if (source != null && source.nonEmpty) {
        q = q.append(" AND ").append(EntityFeatures.SOURCE_NAME_OR_IP + " LIKE " + "'%" + source + "%'")
      }

      if (destination != null && destination.nonEmpty) {
        q = q.append(" AND ").append(EntityFeatures.DESTINATION_NAME_OR_IP + " LIKE " + "'%" + destination + "%'")
      }

    } else if(Constants.AdAnomalyProfileTuple._1.equals(modelId)){
      if (source != null && source.nonEmpty) {
        q = q.append(" AND (").append(EntityFeatures.SOURCE_NAME_OR_IP + " LIKE " + "'%" + source + "%'").append(" OR ").
          append(EntityFeatures.DESTINATION_NAME_OR_IP + " LIKE " + "'%" + source + "%'").append(" )")
      }

      if (destination != null && destination.nonEmpty) {
        q = q.append(" AND (").append(EntityFeatures.SOURCE_NAME_OR_IP + " LIKE " + "'%" + destination + "%'").append(" OR ").
          append(EntityFeatures.DESTINATION_NAME_OR_IP + " LIKE " + "'%" + destination + "%'").append(" )")
      }
    }
    q.toString()
  }
}