package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{Constants, BehaviorAnomaly}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap, ListBuffer}


class BehaviorAnomalyDao (conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[BehaviorAnomalyDao])
  val suppressionDao = new SuppressionDao(conf)

  def getAnomaliesByModelId(startTime: String, endTime: String, modelId: Int, cache: FeatureServiceCache) ={
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      val featureLabelEntityMap: MutableMap[String, ListBuffer[BehaviorDetails]] = MutableMap[String, ListBuffer[BehaviorDetails]]()

      val sqlStr = "SELECT " + BehaviorAnomaly.columns + "  FROM " + BehaviorAnomaly.getName(conf) + " WHERE " +
          BehaviorAnomaly.DATE_TIME + " >= ?  " +
      " AND " + BehaviorAnomaly.DATE_TIME + "  < ? " +
      " AND " + BehaviorAnomaly.MODEL_ID + " = ? " + "  ORDER BY " +  BehaviorAnomaly.FEATURE_LABEL  + ", " + BehaviorAnomaly.DATE_TIME

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, modelId)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        var featureLabel: String = rs.getString(BehaviorAnomaly.FEATURE_LABEL.toString)
        var entity: String = rs.getString(BehaviorAnomaly.ENTITY.toString)
        // Unfortunately, for the AD anomaly model, the value of the feature_label column does not correspond to a feature label.  This
        // is bad practice on the part of the model, and leave us with no way to identify the event type.  So suppression on event
        // types is broken for this model.
        var eventType = ""
        if(Constants.AdAnomalyProfileTuple._1.equals(modelId)){
          entity = entity.substring(entity.indexOf("-") + 1)
        }else{
          val secEventData = cache.getSecurityEventDataFromFeatureLabelModelId(featureLabel, modelId)
          eventType = secEventData.getEventType
          if(secEventData != null){
            featureLabel = eventType
          }
        }
        // We don't know whether entity is going to be an ip address or a username (it will be different for
        // different models.  So pass it as all three of ip, host and user to shouldSuppress()
        if (suppressionDao.shouldSuppress(suppressionList, entity, entity, entity, eventType, true)) {
          suppressionCount += 1
        } else {
          val dateTime: String = rs.getString(BehaviorAnomaly.DATE_TIME.toString)
          val riskScore: Double = rs.getDouble(BehaviorAnomaly.RISK_SCORE.toString)
          var behaviourDetailList: BehaviorDetails = new BehaviorDetails(featureLabel, dateTime, entity, riskScore)
          var list: ListBuffer[BehaviorDetails] = null
          if (featureLabelEntityMap.contains(featureLabel)) {
            list = featureLabelEntityMap(featureLabel)
          } else {
            list = new ListBuffer[BehaviorDetails]()
            featureLabelEntityMap += featureLabel -> list
            val selectionMap: MutableMap[String, Any] = MutableMap[String, Any]()
            selectionMap += "featureLabel" -> featureLabel
            selectionMap += "behaviorDetails" -> list
            buf += selectionMap
          }
          list += behaviourDetailList
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Behavior Anomalies for modelId  => " + modelId + " : => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }


}


case class BehaviorDetails( featureLabel: String, dateTime: String, entity: String, riskScore: Double){
}
