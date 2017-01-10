package com.securityx.modelfeature.dao

import java.sql.{ResultSet, PreparedStatement, Connection}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{MiscUtils, Constants, Beacons, C2Model}
import org.joda.time.{DateTimeZone, DateTime}
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 3/15/16.
 */
class C2ModelDao (conf: FeatureServiceConfiguration) extends BaseDao(conf){

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[C2ModelDao])
  final val suppressionDao = new SuppressionDao(conf)


  def getC2Anomalies(startTime: String, endTime: String, topN: Integer, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val suppressionList = suppressionDao.getSuppressionList
    var suppressionCount = 0

    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val c2ModelConfigurationFeatureMap = cache.getC2ModelConfigurationFeatureMap
    try {
      conn = getConnection(conf)
      val sqlStr = "select " + C2Model.columns + " from " + C2Model.getName(conf) + " where " +
        C2Model.DATE_TIME + " >= ? " +
        " AND " + C2Model.DATE_TIME + " < ? " +
      " ORDER BY " + C2Model.RISK_SCORE + " DESC limit ?"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, topN)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        val sourceNameOrIp: String = selectionMap.getOrElse("sourceNameOrIp", "").asInstanceOf[String]
        if (suppressionDao.shouldSuppress(suppressionList, sourceNameOrIp, sourceNameOrIp, sourceNameOrIp, "Command and Control", false)) {
          suppressionCount += 1
        } else {
          var c2Factors = rs.getString(C2Model.C2_FACTORS.toString)
          c2Factors = c2Factors.replace("(", "").replace(")", "")
          val c2FactorArr: Array[String] = c2Factors.split(", ")

          var c2FactorValue = rs.getString(C2Model.C2_FACTOR_VALUES.toString)
          c2FactorValue = c2FactorValue.replace("(", "").replace(")", "").replace("'", "")
          val c2FactorValueArr = c2FactorValue.split(", ")

          if (c2FactorArr.size == c2FactorValueArr.size) {
            val featureList = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
            for (i <- 0 to c2FactorArr.size - 1) {
              if (c2FactorArr(i) != "0") {
                val factorValue: String = c2FactorValueArr(i)
                if (c2ModelConfigurationFeatureMap.containsKey(factorValue)) {
                  val featureMap = MutableMap[String, Any]()
                  featureMap += "featureKey" -> factorValue
                  featureMap += "featureDescription" -> c2ModelConfigurationFeatureMap.get(factorValue).getFeatureDescription
                  featureList += featureMap
                }
              }
            }
            selectionMap += "featureList" -> featureList

          } else {
            Logger.error("C2 Factors is not of same length as C2 Factor values")
          }

          buf += selectionMap
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get C2 entities for startTime: " + startTime + " and endTime: " + endTime +
        " => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getEntityCardForC2ById(startTime: String, endTime: String, entityId: String,
                         securityEventId: Int, modelId: Int, riskScore: Double,
                         cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    if (entityId != null && !entityId.isEmpty) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null

      try {
        conn = getConnection(conf)
        val sql = " SELECT * FROM " + C2Model.getName(conf) + " WHERE " +
          C2Model.DATE_TIME + " >= ?  " +
          " AND " + C2Model.DATE_TIME + " < ? " +
          " AND " + C2Model.SOURCE_ENTITY_ID + " = ? "

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, entityId)
        rs = executeQuery(pstmt)
        while(rs.next()){
          val dateTime = rs.getString(C2Model.DATE_TIME.toString)
          val sourceIp = rs.getString(C2Model.SOURCE_NAME_OR_IP.toString)
          val destinationIp = rs.getString(C2Model.DESTINATION_NAME_OR_IP.toString)
          val risk = rs.getFloat(C2Model.RISK_SCORE.toString)
          var hours = rs.getString(C2Model.C2_TRAFFIC_HOURS.toString)
          hours = hours.replace("{", "").replace("}", "").replace("(", "").replace(")", "")
          val hourArr = hours.split(",")
          hourArr.foreach { hour =>
            var date: DateTime = new DateTime(dateTime, DateTimeZone.UTC)
            date = date.plusHours(hour.toInt)
            val modelId = Constants.C2ModelTuple._1
            //TODO: This is hardcoded to 0 because C2 has only one securityEventId. These ids needs to be coming form the backend.
            val securityEventId = 0
            val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
            if (secEventData != null) {
              val killchainId = secEventData.getKillchainId
              val cardId = secEventData.getCardId
              val featureDesc = secEventData.getEventType
              val secEventType = secEventData.getEventType

              //C2 model runs Daily
              val isDaily = true

              val selectionMap = collection.mutable.Map[String, Any]()
              selectionMap += "dateTime" -> date.toString(MiscUtils.iso_format)
              selectionMap += "entityId" -> entityId
              selectionMap += "sourceIp" -> sourceIp
              selectionMap += "destinationIp" -> destinationIp
              selectionMap += "modelId" -> modelId
              selectionMap += "eventId" -> securityEventId
              selectionMap += "securityEventType" -> secEventType
              selectionMap += "featureDesc" -> featureDesc
              selectionMap += "killchainId" -> killchainId
              selectionMap += "cardId" -> cardId
              selectionMap += "sourceUserName" -> null
              selectionMap += "riskScore" -> risk
              selectionMap += "isDaily" -> isDaily
              buf += selectionMap
            }
          }
        }
      } catch {
        case ex: Exception => Logger.error("Failed to get Entity Card Details for C2 => " + ex)
      } finally {
        closeConnections(conn, pstmt, rs)
      }
    }
  }

  def getEntityCardForC2(startTime: String, endTime: String, ip: String, userName: String,
                          securityEventId: Int, modelId: Int, riskScore: Double,
                          cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {

    if( ip != null && ip.nonEmpty ) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null

      try {
        conn = getConnection(conf)
        val sql = " SELECT * FROM " + C2Model.getName(conf) + " WHERE " +
                    C2Model.DATE_TIME + " >= ?  " +
          " AND " + C2Model.DATE_TIME + " < ? " +
        " AND " + C2Model.SOURCE_NAME_OR_IP  + " = ? "

        pstmt = getPreparedStatement(conn, sql)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, ip)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while(rs.next()){
          val dateTime = rs.getString(C2Model.DATE_TIME.toString)
          val sourceIp = rs.getString(C2Model.SOURCE_NAME_OR_IP.toString)
          val destinationIp = rs.getString(C2Model.DESTINATION_NAME_OR_IP.toString)
          val risk = rs.getFloat(C2Model.RISK_SCORE.toString)
          var hours = rs.getString(C2Model.C2_TRAFFIC_HOURS.toString)
          hours = hours.replace("{", "").replace("}", "").replace("(", "").replace(")", "")
          val hourArr = hours.split(",")
          hourArr.foreach { hour =>
            var date: DateTime = new DateTime(dateTime, DateTimeZone.UTC)
            date = date.plusHours(hour.toInt)
            val modelId = Constants.C2ModelTuple._1
            //TODO: This is hardcoded to 0 because C2 has only one securityEventId. These ids needs to be coming form the backend.
            val securityEventId = 0
            val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
            if (secEventData != null) {
              val killchainId = secEventData.getKillchainId
              val cardId = secEventData.getCardId
              val featureDesc = secEventData.getEventType
              val secEventType = secEventData.getEventType
              val obj: EntityEventInfo = new EntityEventInfo(sourceIp, destinationIp, null, null, modelId, securityEventId, killchainId)

              //C2 model runs Daily
              val isDaily = true

              val selectionMap = collection.mutable.Map[String, Any]()
              selectionMap += "dateTime" -> date.toString(MiscUtils.iso_format)
              selectionMap += "sourceIp" -> sourceIp
              selectionMap += "destinationIp" -> destinationIp
              selectionMap += "modelId" -> modelId
              selectionMap += "eventId" -> securityEventId
              selectionMap += "securityEventType" -> secEventType
              selectionMap += "featureDesc" -> featureDesc
              selectionMap += "killchainId" -> killchainId
              selectionMap += "cardId" -> cardId
              selectionMap += "sourceUserName" -> null
              selectionMap += "riskScore" -> risk
              selectionMap += "isDaily" -> isDaily
              buf += selectionMap
            }
          }
        }

      } catch {
        case ex: Exception => Logger.error("Failed to get Entity Card Details for C2 => " + ex.getMessage, ex)
      } finally {
        closeConnections(conn, pstmt, rs)
      }
    }

  }

}
