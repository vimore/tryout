package com.securityx.modelfeature.dao

import java.sql.{ResultSet, PreparedStatement, Connection}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{NoveltyDetector, PeerEntityCardDetails}
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.collection.mutable.{Map, ListBuffer}

/**
 * Created by harish on 3/1/16.
 */
class NoveltyDetectorDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[NoveltyDetectorDao])

  /**
   * For a given time range and entity id, get details about nox threats.  Details that are found are placed in a map
   * and added to buf.  Note that buf may or may not already have other details in it.
   *
   * @param startTime start of the time range
   * @param endTime end of the time range
   * @param entityId entity id identifying the entity for which we are getting threat details
   * @param securityEventId security event id of the threat
   * @param modelId model id of the threat
   * @param riskScore risk score as found in entity threat table (this is passed in so it can be placed in the map(s) that
   *                  will be added to buf
   * @param cache cache to get security event info from
   * @param buf buffer in which results will be placed
   */
  def getNoveltyDetectorEntityCardsById(startTime: String, endTime: String, entityId: String,
                                    securityEventId: Int, modelId: Int, riskScore: Double,
                                    cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    if (entityId != null && !entityId.isEmpty) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)

        val sql = "SELECT * FROM " + NoveltyDetector.getName(conf) +
          " WHERE " +
          NoveltyDetector.DATE_TIME + " >= ? AND " +
          NoveltyDetector.DATE_TIME + " < ? AND " +
          NoveltyDetector.SECURITY_EVENT_ID + " = ? AND " +
          NoveltyDetector.MODEL_ID + " = ? AND " +
          NoveltyDetector.ENTITY_ID + " = ? " +
          "ORDER BY " + PeerEntityCardDetails.DATE_TIME + " ASC LIMIT 1000 "

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setInt(3, securityEventId)
        pstmt.setInt(4, modelId)
        pstmt.setString(5, entityId)

        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          val anomalyScore = rs.getString(NoveltyDetector.ANOMALY_SCORE.toString)
          val featureKey = rs.getString(NoveltyDetector.FEATURE_KEY.toString)
          val featureKeyCount = rs.getString(NoveltyDetector.FEATURE_KEY_COUNT.toString)
          val targetDescription = rs.getString(NoveltyDetector.TARGET_DESCRIPTION.toString)
          val winEventId = rs.getString(NoveltyDetector.CEF_SIGNATURE_ID.toString)
          val secEventId = rs.getInt(NoveltyDetector.SECURITY_EVENT_ID.toString)
          val modelId = rs.getInt(NoveltyDetector.MODEL_ID.toString)
          val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(secEventId, modelId)

          if (secEventData != null) {
            val killchainId = secEventData.getKillchainId
            val cardId = secEventData.getCardId
            val featureDesc = secEventData.getEventDescription
            appendToMap(rs, rsMeta, selectionMap)
            selectionMap += "entityId" -> entityId
            selectionMap += "targetId" -> featureKey
            selectionMap += "featureKeyCount" -> featureKeyCount
            selectionMap += "targetDescription" -> targetDescription
            selectionMap += "winEventId" -> winEventId
            selectionMap += "eventId" -> secEventId
            selectionMap += "featureDesc" -> featureDesc
            selectionMap += "securityEventType" -> secEventData.getEventType
            selectionMap += "killchainId" -> killchainId
            selectionMap += "cardId" -> cardId
            selectionMap += "riskScore" -> anomalyScore
            selectionMap += "isDaily" -> true //peer group model runs daily

            buf += selectionMap
          }
        }
      } catch {
        case ex: Exception => Logger.error("Failed to get Entity Anomaly Details => " + ex)
      } finally {
        closeConnections(conn, pstmt, rs)
      }
    }
  }

  def getNoveltyDetectorEntityCards(startTime: String, endTime: String, ip: String, userName: String,
                                    securityEventId: Int, modelId: Int, riskScore: Double,
                                    cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    if( userName != null && userName.nonEmpty ) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)

        var sql = "select * from " + NoveltyDetector.getName(conf) +
          " where " +
          NoveltyDetector.DATE_TIME + " >= ?" + " AND " +
          NoveltyDetector.DATE_TIME + " < ? " + " AND " +
          NoveltyDetector.SECURITY_EVENT_ID + " = ? " + " AND " +
          NoveltyDetector.MODEL_ID + " = ? "

        val sqlBuilder = new StringBuilder()

        var userNameEscaped = userName
        if (userNameEscaped != null && userNameEscaped.contains("\\")) {
          userNameEscaped = userNameEscaped.replace("\\", "\\\\")
        }

        var addUserName = false
        // MJL: commented out because we're removing the ENTITY column from the Novelty_Detector table.
        // The entity_id column takes it's place, and this code (the entire method) is now obsolete
//        if ( userNameEscaped != null && userNameEscaped.nonEmpty) {
//          sqlBuilder.append(" AND ( ").append(NoveltyDetector.ENTITY).
//            append(" LIKE ? ) ")
//          userNameEscaped = "%" + userNameEscaped
//          addUserName = true
//        }

        sql = sql + sqlBuilder.toString() + " ORDER BY " + PeerEntityCardDetails.DATE_TIME + " ASC " + " LIMIT 1000 "

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setInt(3, securityEventId)
        pstmt.setInt(4, modelId)
        if (addUserName) {
          pstmt.setString(5, userNameEscaped)
        }

        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          val anomalyScore = rs.getString(NoveltyDetector.ANOMALY_SCORE.toString)
          val featureKey = rs.getString(NoveltyDetector.FEATURE_KEY.toString)
          val featureKeyCount = rs.getString(NoveltyDetector.FEATURE_KEY_COUNT.toString)
          val targetDescription = rs.getString(NoveltyDetector.TARGET_DESCRIPTION.toString)
          val winEventId = rs.getString(NoveltyDetector.CEF_SIGNATURE_ID.toString)
          val secEventId = rs.getInt(NoveltyDetector.SECURITY_EVENT_ID.toString)
          val modelId = rs.getInt(NoveltyDetector.MODEL_ID.toString)
          val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(secEventId, modelId)

          if (secEventData != null) {
            val killchainId = secEventData.getKillchainId
            val cardId = secEventData.getCardId
            val featureDesc = secEventData.getEventDescription
            appendToMap(rs, rsMeta, selectionMap)
            selectionMap += "targetId" -> featureKey
            selectionMap += "featureKeyCount" -> featureKeyCount
            selectionMap += "targetDescription" -> targetDescription
            selectionMap += "winEventId" -> winEventId
            selectionMap += "eventId" -> secEventId
            selectionMap += "featureDesc" -> featureDesc
            selectionMap += "securityEventType" -> secEventData.getEventType
            selectionMap += "killchainId" -> killchainId
            selectionMap += "cardId" -> cardId
            selectionMap += "riskScore" -> anomalyScore
            selectionMap += "isDaily" -> true //peer group model runs daily

            buf += selectionMap
          }

        }
      } catch {
        case ex: Exception => Logger.error("Failed to get Entity Anomaly Details => " + ex)
      } finally {
        closeConnections(conn, pstmt, rs)
      }

    }
  }
}
