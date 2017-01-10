package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.utils.PeerEntityCardDetails
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 3/10/15.
 */
class EntityAnomalyDetailsDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[EntityAnomalyDetailsDao])


  def getEntityCards(startTime: String, endTime: String, ip: String, userName: String,
                     securityEventId: Int, modelId: Int, riskScore: Double,
                     cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    getPeerEntityCards(startTime, endTime, ip, userName, securityEventId, modelId, riskScore, cache, buf)
  }


  def getPeerEntityCardsById(startTime: String, endTime: String, entityId: String, securityEventId: Int, modelId: Int, riskScore: Double,
                             cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    if (entityId != null && !entityId.isEmpty) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)

        val sql = "SELECT * FROM " + PeerEntityCardDetails.getName(conf) +
          " WHERE " +
          PeerEntityCardDetails.DATE_TIME + " >= ? AND " +
          PeerEntityCardDetails.DATE_TIME + " < ? AND " +
          PeerEntityCardDetails.SECURITY_EVENT_ID + " = ? AND " +
          PeerEntityCardDetails.MODEL_ID + " = ? AND (" +
          PeerEntityCardDetails.SOURCE_ENTITY_ID + " = ? OR " +
          PeerEntityCardDetails.DESTINATION_ENTITY_ID + " = ?) " +
          "ORDER BY " + PeerEntityCardDetails.DATE_TIME + " ASC LIMIT 1000 "

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setInt(3, securityEventId)
        pstmt.setInt(4, modelId)
        pstmt.setString(5, entityId)
        pstmt.setString(6, entityId)

        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          val sourceIp = rs.getString(PeerEntityCardDetails.SOURCE_IP.toString)
          val destIp = rs.getString(PeerEntityCardDetails.DESTINATION_IP.toString)
          val sourceUserName = rs.getString(PeerEntityCardDetails.SOURCE_USER_NAME.toString)
          val destinationUserName = rs.getString(PeerEntityCardDetails.DESTINATION_USER_NAME.toString)
          val sourceId = rs.getString(PeerEntityCardDetails.SOURCE_ENTITY_ID.toString)
          val destinationId = rs.getString(PeerEntityCardDetails.DESTINATION_ENTITY_ID.toString)
          val secEventId = rs.getInt(PeerEntityCardDetails.SECURITY_EVENT_ID.toString)
          val modelId = rs.getInt(PeerEntityCardDetails.MODEL_ID.toString)
          val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(secEventId, modelId)

          if (secEventData != null) {
            val killchainId = secEventData.getKillchainId
            val cardId = secEventData.getCardId
            val featureDesc = secEventData.getEventDescription
            val secEventType = secEventData.getEventType
            appendToMap(rs, rsMeta, selectionMap)
            selectionMap += "entityId" -> entityId
            selectionMap += "sourceEntityId" -> sourceId
            selectionMap += "destinationEntityId" -> destinationId
            selectionMap += "eventId" -> secEventId
            selectionMap += "featureDesc" -> featureDesc
            selectionMap += "securityEventType" -> secEventType
            selectionMap += "killchainId" -> killchainId
            selectionMap += "cardId" -> cardId
            selectionMap += "riskScore" -> riskScore
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

  def getPeerEntityCards(startTime: String, endTime: String, ip: String, userName:String,
                         securityEventId: Int, modelId: Int, riskSCore: Double,
                         cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    if( !( (ip == null || ip.isEmpty) && (userName == null || userName.isEmpty) ) ) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)

        var sql = "select * from " + PeerEntityCardDetails.getName(conf) +
          " where " +
          PeerEntityCardDetails.DATE_TIME + " >= ?" + " AND " +
          PeerEntityCardDetails.DATE_TIME + " < ? " + " AND " +
          PeerEntityCardDetails.SECURITY_EVENT_ID + " = ? " + " AND " +
          PeerEntityCardDetails.MODEL_ID + " = ? "

        val sqlBuilder = new StringBuilder()
        val bindVarList = collection.mutable.ListBuffer.empty[String]
        var userNameEscaped = userName
        if (userNameEscaped != null && userNameEscaped.contains("\\")) {
          userNameEscaped = userNameEscaped.replace("\\", "\\\\")
        }

        var userFlag = true
        if (ip != null && ip.nonEmpty) {
          sqlBuilder.append(" AND (  ").append(PeerEntityCardDetails.SOURCE_IP).
            append(" = ? OR ").
            append(PeerEntityCardDetails.DESTINATION_IP).
            append(" = ? ")
          bindVarList += ip
          bindVarList += ip
          if (userNameEscaped != null && userNameEscaped.nonEmpty) {
            userFlag = false
            sqlBuilder.append(" OR ").append(PeerEntityCardDetails.SOURCE_USER_NAME).
              append(" LIKE ? OR ").
              append(PeerEntityCardDetails.DESTINATION_USER_NAME).
              append(" LIKE ? ")
            bindVarList += "%" + userNameEscaped
            bindVarList += "%" + userNameEscaped
          }
          sqlBuilder.append(" ) ")
        }


        if (userFlag && userNameEscaped != null && userNameEscaped.nonEmpty) {
          sqlBuilder.append(" AND ( ").append(PeerEntityCardDetails.SOURCE_USER_NAME).
            append(" LIKE ? OR ").
            append(PeerEntityCardDetails.DESTINATION_USER_NAME).
            append(" LIKE ? ) ")
          bindVarList += "%" + userNameEscaped
          bindVarList += "%" + userNameEscaped
        }

        sql = sql + sqlBuilder.toString() + " ORDER BY " + PeerEntityCardDetails.DATE_TIME + " ASC " + " LIMIT 1000 "

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setInt(3, securityEventId)
        pstmt.setInt(4, modelId)
        var count = 4
        for (bindVar <- bindVarList) {
          count += 1
          pstmt.setString(count, bindVar)
        }

        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          val sourceIp = rs.getString(PeerEntityCardDetails.SOURCE_IP.toString)
          val destIp = rs.getString(PeerEntityCardDetails.DESTINATION_IP.toString)
          val sourceUserName = rs.getString(PeerEntityCardDetails.SOURCE_USER_NAME.toString)
          val destinationUserName = rs.getString(PeerEntityCardDetails.DESTINATION_USER_NAME.toString)
          val secEventId = rs.getInt(PeerEntityCardDetails.SECURITY_EVENT_ID.toString)
          val modelId = rs.getInt(PeerEntityCardDetails.MODEL_ID.toString)
          val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(secEventId, modelId)

          if (secEventData != null) {
            val killchainId = secEventData.getKillchainId
            val cardId = secEventData.getCardId
            val featureDesc = secEventData.getEventDescription
            val secEventType = secEventData.getEventType
            val obj: EntityEventInfo = new EntityEventInfo(sourceIp, destIp, sourceUserName, destinationUserName, modelId, secEventId,
              killchainId)
            appendToMap(rs, rsMeta, selectionMap)
            selectionMap += "eventId" -> secEventId
            selectionMap += "featureDesc" -> featureDesc
            selectionMap += "securityEventType" -> secEventType
            selectionMap += "killchainId" -> killchainId
            selectionMap += "cardId" -> cardId
            selectionMap += "riskScore" -> riskSCore
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
