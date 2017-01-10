package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.EntityInfo
import com.securityx.modelfeature.utils._
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, SecurityEventTypeConfiguration}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 11/17/14.
 */
class HomePageDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[HomePageDao])

  //Beacon and Bot security Event Id and Killchain Id.
  //toDo: Refactor code. Constants file to keep a track of such info
  private final val BeaconCoordSecurityEventId: Int = 0
  private final val BeaconCoordSecurityKillChainId: Int = 2

  /**
   * Compute the total Security Events per SecurityEventID within the specified start and end time.
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param cache FeatureServiceApiCache
   * @return
   */
  def getEventEntityCounts(startTimeStr: String, endTimeStr: String, minRisk: Double, cache: FeatureServiceCache) = {
    getPeerGroupBeaconCoordCountPerFeatureId(startTimeStr, endTimeStr, minRisk, cache)
  }


  /**
   * Compute the total Security Events per killchain Id within the specified start and end time.
   *
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param cache FeatureServiceApiCache
   * @return
   */
  def getEntitiesPerKillChainType(killChainId: Int, startTimeStr: String, endTimeStr: String, minRisk: Double,
                                  cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val securityEventDataList: ListBuffer[SecurityEventTypeConfiguration] = {
      val killChainToEventDataMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getKillchainToSecurityEventDataMap()
      if (killChainToEventDataMap.contains(killChainId))
        killChainToEventDataMap(killChainId)
      else
        null
    }

    var querySecondHalf = ""
    var beacon: Boolean = false
    var bot: Boolean = false
    if (securityEventDataList != null) {
      var count: Int = 0
      securityEventDataList.foreach { securityEventData =>
        if (securityEventData.getModel.equals(Constants.BeaconModelTuple._1)) {
          beacon = true
        } else if (securityEventData.getModel.equals(Constants.WebCoordinatedBehaviorModelTuple._1)) {
          bot = true
        } else {
          val str = "(" + PeerGroup.PEER_TOP_FEATURES + " = " + "'[" + securityEventData.getSecurityEventTypeId.toString + "]'" +
            " AND " + PeerGroup.PEER_TYPE + " = " + securityEventData.getModel + ")"
          if (querySecondHalf.equals("")) {
            querySecondHalf = str
          } else {
            querySecondHalf = querySecondHalf + " OR " + str
          }
        }
      }

      //Query Peer_group table to find the count of peer group
      if (!querySecondHalf.isEmpty()) {
        count = getPeerCount(startTimeStr, endTimeStr, minRisk, querySecondHalf)
      }

      //query beacon
      if (beacon) {
        count += getBeaconCount(startTimeStr, endTimeStr, minRisk)
      }

      //query bot
      if (bot) {
        count += getBotCount(startTimeStr, endTimeStr, minRisk)
      }

      val selectionMap = MutableMap[String, Any]()
      selectionMap += "killchainId" -> killChainId
      selectionMap += "dateTime" -> startTimeStr
      selectionMap += "count" -> count
      buf += selectionMap
    }
    buf
  }

  /**
   *
   * @param killchainId Int id for Killchain category
   * @param securityEventId Int Id for SecurityEvent
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param cache FeatureServiceApiCache
   * @return collection.mutable.ListBuffer[MutableMap[String, Any]
   */
  def getEventEntityCountsByKillchainIdSecEventId(killchainId: Int, securityEventId: Int, startTimeStr: String,
                                                  endTimeStr: String, minRisk: Double, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var count: Int = 0
    val securityEventDataList: ListBuffer[SecurityEventTypeConfiguration] = {
      val killchainToeventDataMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getKillchainToSecurityEventDataMap()
      if (killchainToeventDataMap.contains(killchainId))
        killchainToeventDataMap(killchainId)
      else
        null
    }

    if (securityEventDataList != null) {
      var bot: Boolean = false
      var beacon: Boolean = false
      var inOperatorString: String = ""
      securityEventDataList.foreach { securityEventData =>
        if (securityEventData.getSecurityEventTypeId.equals(securityEventId)) {
          if (securityEventData.getModel.equals(Constants.BeaconModelTuple._1)) {
            beacon = true
          } else if (securityEventData.getModel.equals(Constants.WebCoordinatedBehaviorModelTuple._1)) {
            bot = true
          } else {
            inOperatorString = if (inOperatorString.isEmpty) {
              "'[" + securityEventData.getSecurityEventTypeId.toString + "]'"
            } else inOperatorString + ", " + "'[" + securityEventData.getSecurityEventTypeId + "]'"

          }
        }
      }

      if (!inOperatorString.isEmpty() || bot || beacon) {
        ///query
        //Query Peer_group table to find the count of peer group
        if (!inOperatorString.isEmpty()) {
          inOperatorString = PeerGroup.PEER_TOP_FEATURES + " In " + "(" + inOperatorString + " )"
          count += getPeerCount(startTimeStr, endTimeStr, minRisk, inOperatorString)
        }
        //query beacon
        if (beacon) {
          count += getBeaconCount(startTimeStr, endTimeStr, minRisk)
        }

        //query bot
        if (bot) {
          count += getBotCount(startTimeStr, endTimeStr, minRisk)
        }

        val selectionMap = MutableMap[String, Any]()
        selectionMap += "killchainId" -> killchainId
        selectionMap += "securityEventTypeId" -> securityEventId
        selectionMap += "dateTime" -> startTimeStr
        selectionMap += "count" -> count
        buf += selectionMap
      }
    }
    buf
  }

  /**
   * This method takes start time and end time as input and finds the count of PeerGroups within that time range.
   * Also, it adds to this count, the total count of Beacon + Bots (coords)
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param cache FeatureServiceApiCache
   * @return collection.mutable.ListBuffer[MutableMap[String, Any]
   */
  private def getPeerGroupBeaconCoordCountPerFeatureId(startTimeStr: String, endTimeStr: String, minRisk: Double,
                                                       cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      //bot
      var botCount: Int = 0
      var maxRisk: Double = 0
      var tableName = CoordActivity.getName(conf)
      var sqlStr = "select " + "MAX( " + CoordActivity.ANOMALY_CLUSTER_SCORE + " ) as max_risk, count(*) as count" + " from " +
        tableName + " where " +
        CoordActivity.DATE_TIME + " >= ? " +
        " AND " + CoordActivity.DATE_TIME + " < ? " +
        " AND " + CoordActivity.ANOMALY_CLUSTER_SCORE + " >=  ?"
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setDouble(3, minRisk)
      rs = executeQuery(pstmt)
      if (rs.next()) {
        botCount = rs.getInt("count")
        maxRisk = rs.getDouble("max_risk")
      }
      var selectionMap = MutableMap[String, Any]()
      var killchainId: Int = BeaconCoordSecurityKillChainId
      var securityEventData: SecurityEventTypeConfiguration =
        cache.getSecurityEventDataFromFeatureIdModelId(BeaconCoordSecurityEventId,Constants.WebCoordinatedBehaviorModelTuple._1)

      selectionMap += "killchainId" -> killchainId
      selectionMap += "securityEventTypeId" -> BeaconCoordSecurityEventId
      selectionMap += "count" -> botCount
      selectionMap += "dateTime" -> startTimeStr
      selectionMap += "maxRisk" -> maxRisk
      selectionMap += "typePrefix" -> securityEventData.getTypePrefix
      selectionMap += "type" -> securityEventData.getEventType
      buf += selectionMap

      // beacon
      var beaconCount: Int = 0
      var risk: Double = 0
      tableName = Beacons.getName(conf)
      sqlStr = "select " + "MAX( " + Beacons.RISK + " ) as max_risk, count(*) as count" + " from " + tableName + " where " +
        Beacons.EVENT_TIME + " >= ? " +
        " AND " + Beacons.EVENT_TIME + " < ? " +
        " AND " + Beacons.RISK + " >= ?"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setDouble(3, minRisk)
      rs = executeQuery(pstmt)
      if (rs.next()) {
        beaconCount = rs.getInt("count")
        risk = rs.getDouble("max_risk")
      }
      selectionMap = MutableMap[String, Any]()
      killchainId = BeaconCoordSecurityKillChainId
      securityEventData = cache
                          .getSecurityEventDataFromFeatureIdModelId(BeaconCoordSecurityEventId, Constants.BeaconModelTuple._1)

      selectionMap += "killchainId" -> killchainId
      selectionMap += "securityEventTypeId" -> BeaconCoordSecurityEventId
      selectionMap += "count" -> beaconCount
      selectionMap += "dateTime" -> startTimeStr
      selectionMap += "maxRisk" -> risk
      selectionMap += "typePrefix" -> securityEventData.getTypePrefix
      selectionMap += "type" -> securityEventData.getEventType
      buf += selectionMap

      //Query Peer_group table to find the count of peer groups
      tableName = PeerGroup.getName(conf)
      sqlStr = "select " + PeerGroup.PEER_TOP_FEATURES + ", " + PeerGroup.PEER_TYPE +
        " , count(*) as count, Max(ANOMALY_SCORE) as max_anomaly" +
        " from " + tableName + " where " +
        PeerGroup.DATE_TIME + " >= ? " +
        " AND " + PeerGroup.DATE_TIME + " < ? " +
        " AND " + PeerGroup.ANOMALY_SCORE + " >= ? " +
        " group by " + PeerGroup.PEER_TOP_FEATURES + "," + PeerGroup.PEER_TYPE
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setDouble(3, minRisk)
      rs = executeQuery(pstmt)

      //compute ( peergroup + beacon + coord ) Count
      while (rs.next()) {
        try {
          val selectionMap = MutableMap[String, Any]()
          val key = getFeatureIdFromPeerTopFeatures(rs.getString(PeerGroup.PEER_TOP_FEATURES.toString))
          val count: Int = rs.getInt("count");
          val maxAnomaly = rs.getDouble("max_anomaly")
          val modelId: Int = rs.getInt(PeerGroup.PEER_TYPE.toString)
          val securityEventData: SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(key, modelId)
          val killchainId: Integer = cache.getKillchainIdFromSecurityEventModelIds(key, modelId)
          if (killchainId == null) {
            Logger.error("No killchainId exits for SecurityEventType " + key)
          }

          selectionMap += "killchainId" -> killchainId
          selectionMap += "securityEventTypeId" -> key
          selectionMap += "count" -> count
          selectionMap += "dateTime" -> startTimeStr
          selectionMap += "maxRisk" -> maxAnomaly
          selectionMap += "typePrefix" -> securityEventData.getTypePrefix
          selectionMap += "type" -> securityEventData.getEventType
          buf += selectionMap
        } catch {
          case ex: Exception => Logger.error("Failed to get entity counts for all security events for peer group: " + ex)
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get entity counts for all security event ids: " + ex)
    } finally {
      closeConnections(conn)
    }
    buf

  }

  /**
   * Queries the PeerGroup Table and
   * returns the events' counts between a given startTime and endTime and above a given minimum risk score
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param minRisk Double minimum risk score
   * @param querySecondHalf where clause for sql query string to be used
   *
   * @return Int counts
   */
  private def getPeerCount(startTimeStr: String, endTimeStr: String, minRisk: Double, querySecondHalf: String): Int = {
    var count: Int = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val tableName = PeerGroup.getName(conf)
      val sqlStr = "select count(*) as count" + " from " + tableName +
        " where " + PeerGroup.DATE_TIME + " >= ? " +
        " AND " + PeerGroup.DATE_TIME + " < ? " +
        " AND (" + querySecondHalf + ")" +
        " AND " + PeerGroup.ANOMALY_SCORE + " >= ?"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setDouble(3, minRisk)
      rs = executeQuery(pstmt)
      if (rs.next()) {
        count = rs.getInt("count")
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get count for Beacons => " + ex)
    } finally {
      closeConnections(conn)
    }
    count
  }

  /**
   * Queries the Beacons Table and
   * returns the events' counts between a given startTime and endTime and above a given minimum risk score
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param minRisk Double minimum risk score
   *
   * @return Int counts
   */
  private def getBeaconCount(startTimeStr: String, endTimeStr: String, minRisk: Double): Int = {
    var count: Int = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sqlStr = "select " + "count(*) as count" + " from " + Beacons.getName(conf) +
        " where " + Beacons.EVENT_TIME + " >= ? " +
        " AND " + Beacons.EVENT_TIME + " < ? " +
        " AND " + Beacons.RISK + " >= ?"

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setDouble(3, minRisk)
      rs = executeQuery(pstmt)
      if (rs.next()) {
        count = rs.getInt("count")
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get count for Beacons => " + ex)
    } finally {
      closeConnections(conn)
    }
    count
  }

  /**
   * Queries the Coord_Activity Table and
   * returns the events' counts between a given startTime and endTime and above a given minimum risk score
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param minRisk Double minimum risk score
   *
   * @return Int counts
   */
  private def getBotCount(startTimeStr: String, endTimeStr: String, minRisk: Double): Int = {
    var count: Int = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sqlStr = "select " + "count(*) as count" + " from " + CoordActivity.getName(conf) +
        " where " + CoordActivity.DATE_TIME + " >= ? " +
        " AND " + CoordActivity.DATE_TIME + " < ? " +
        " AND " + CoordActivity.ANOMALY_CLUSTER_SCORE + " >=  ?"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setDouble(3, minRisk)
      rs = executeQuery(pstmt)
      if (rs.next()) {
        count = rs.getInt("count")
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Bot count => " + ex)
    } finally {
      closeConnections(conn)
    }
    count
  }

  /**
   * List of top 100 entity summary details for all kill chains for all security event types for a given day sorted by risk
   * scores.
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @param cache FeatureServiceApiCache
   *
   * @return topN ListBuffer.empty[MutableMap[String, Any]
   */
  def getEntitySummaryForAllKillchains(startTimeStr: String, endTimeStr: String, topN: Int, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (!(topN > 0)) {
      Logger.debug(" Empty Result will be returned. Because N specified = " + topN)
      buf
    }
    else {
      val beaconTableName = Beacons.getName(conf)
      val coordTableName = CoordActivity.getName(conf)
      val peerGroupTableName = PeerGroup.getName(conf)
      val beaconSqlStr: String = "select distinct SOURCE_NAME_OR_IP, RISK, EVENT_TIME from " + beaconTableName +
        " where  event_time >= ? and event_time < ? order by Risk Desc Limit " + topN
      val coordSql: String = "select distinct SOURCE_NAME_OR_IP, DATE_TIME, ANOMALY_CLUSTER_SCORE from " + coordTableName +
        " where  date_time >= ? and date_time < ? order by ANOMALY_CLUSTER_SCORE DEsc Limit " + topN

      val x: mutable.PriorityQueue[EntityRiskOrder] = new mutable.PriorityQueue[EntityRiskOrder]
      val ipTrackerMap: MutableMap[String, EntityRiskOrder] = MutableMap[String, EntityRiskOrder]()

      try {
        val killchainSecurityEventMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] =
          cache.getKillchainToSecurityEventDataMap()

        for ((killchainId, dataList) <- killchainSecurityEventMap) {
          val modelsMap: MutableMap[Integer, String] = cache.getModelsMap

          dataList.foreach { eventData =>

            val modelId: Int = eventData.getModel
            val modelName = modelsMap(modelId)
            val obj: KillchainHelper = new KillchainHelper(killchainId, eventData.getSecurityEventTypeId, modelId, modelName)
            if (modelName.equals(Constants.BeaconModelTuple._2)) {
              executeTopN(beaconSqlStr, startTimeStr, endTimeStr, x, topN, "beacon", obj, ipTrackerMap)
            } else if (modelName.equals(Constants.WebCoordinatedBehaviorModelTuple._2)) {
              executeTopN(coordSql, startTimeStr, endTimeStr, x, topN, "coord", obj, ipTrackerMap)
            } else {
              // str = if (str.isEmpty) eventData.securityEventId.toString else str + "," + eventData.securityEventId
              val featureIdString = "'[" + eventData.getSecurityEventTypeId + "]'"
              val  peerGroupSql: String = String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s >= ? AND %s < ? AND %s = %s ORDER BY %s DESC",
                PeerGroup.DATE_TIME, PeerGroup.ANOMALY_SCORE, PeerGroup.ENTITY_ID, PeerGroup.PEER_TYPE, PeerGroup.PEER_TOP_FEATURES,
                PeerGroup.getName(conf), PeerGroup.DATE_TIME, PeerGroup.DATE_TIME, PeerGroup.PEER_TOP_FEATURES, featureIdString, PeerGroup.ANOMALY_SCORE)
              /*val peerGroupSql: String = "select DATE_TIME, ANOMALY_SCORE, ENTITY_ID, PEER_TYPE, " +
                PeerGroup.PEER_TOP_FEATURES + " from " + peerGroupTableName + " where date_time >= ? and date_time < ? and " +
                PeerGroup.PEER_TOP_FEATURES + " =  " + featureIdString + " order by ANOMALY_SCORE Desc"*/
              executeTopNForPeerGroup(peerGroupSql, startTimeStr, endTimeStr, x, topN, ipTrackerMap, killchainId, cache)

            }

          }
        }

        populateSummaryResult(x, buf, startTimeStr, endTimeStr)
      } catch {
        case e: Exception => {
          Logger.error("Failed to get entity summary for all killchains: " + e)
        }
      }

      buf
    }

  }

  /**
   * List of top 100 entity summary details for a specified killchainId for all security event types for a given day sorted
   * by risk scores.
   * @param killchainId Int specifying KillchainId
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @param cache FeatureServiceApiCache
   *
   * @return topN ListBuffer.empty[MutableMap[String, Any]
   */
  def getEntitySummaryByKillchainId(killchainId: Int, startTimeStr: String, endTimeStr: String, topN: Int,
                                    cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (!(topN > 0)) {
      Logger.debug(" Empty Result will be returned. Because N specified = " + topN)
      buf
    }
    else {
      val beaconTableName = Beacons.getName(conf)
      val coordTableName = CoordActivity.getName(conf)
      val peerGroupTableName = PeerGroup.getName(conf)
      val beaconSqlStr: String = "select distinct SOURCE_NAME_OR_IP, RISK, EVENT_TIME from " + beaconTableName +
        " where  event_time >= ? and event_time < ? order by Risk Desc Limit " + topN
      val coordSql: String = "select distinct SOURCE_NAME_OR_IP, DATE_TIME, ANOMALY_CLUSTER_SCORE from " + coordTableName +
        " where  date_time >= ? and date_time < ? order by ANOMALY_CLUSTER_SCORE DEsc Limit " + topN

      val x: mutable.PriorityQueue[EntityRiskOrder] = new mutable.PriorityQueue[EntityRiskOrder]()
      val ipTrackerMap: MutableMap[String, EntityRiskOrder] = MutableMap[String, EntityRiskOrder]()

      var beaconBotDone: Boolean = false

      try {
        val eventDataMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getSecurityEventDataMap
        val modelsMap: MutableMap[Integer, String] = cache.getModelsMap

        val killchainSecurityEventMap: MutableMap[Int, ListBuffer[Int]] = cache.getKillchainSecurityEventMapping
        val killchainSecDataMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getKillchainToSecurityEventDataMap()
        val secEventDataList: ListBuffer[SecurityEventTypeConfiguration] = {
          if (killchainSecDataMap.contains(killchainId))
            killchainSecDataMap(killchainId)
          else
            new ListBuffer[SecurityEventTypeConfiguration]
        }

        var querySecondHalf: String = ""
        secEventDataList.foreach { securityEventData =>
          val securityEventId = securityEventData.getSecurityEventTypeId
          val eventlist: ListBuffer[SecurityEventTypeConfiguration] = eventDataMap(securityEventId)
          val modelId: Int = securityEventData.getModel
          val modelName = modelsMap(modelId)
          val obj: KillchainHelper = new KillchainHelper(killchainId, securityEventId, modelId, modelName)
          if (modelName.equals(Constants.BeaconModelTuple._2)) {
            executeTopN(beaconSqlStr, startTimeStr, endTimeStr, x, topN, "beacon", obj, ipTrackerMap)
          } else if (modelName.equals(Constants.WebCoordinatedBehaviorModelTuple._2)) {
            executeTopN(coordSql, startTimeStr, endTimeStr, x, topN, "coord", obj, ipTrackerMap)
          } else {
            val str = "(" + PeerGroup.PEER_TOP_FEATURES + " = " + "'[" + securityEventData.getSecurityEventTypeId.toString + "]'" +
              " AND " + PeerGroup.PEER_TYPE + " = " + modelId + ")"
            if (querySecondHalf.equals("")) {
              querySecondHalf = str
            } else {
              querySecondHalf = querySecondHalf + " OR " + str
            }
          }
        }
        if (!querySecondHalf.isEmpty()) {
          val peerGroupSql: String = String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE  %s >= ? AND %s < ? AND (%s) ORDER BY %s DESC",
            PeerGroup.DATE_TIME, PeerGroup.ANOMALY_SCORE, PeerGroup.ENTITY_ID, PeerGroup.PEER_TYPE, PeerGroup.getName(conf), PeerGroup.DATE_TIME, PeerGroup.DATE_TIME,querySecondHalf, PeerGroup.ANOMALY_SCORE);
          /*
          val peerGroupSql: String = "select DATE_TIME, ANOMALY_SCORE, ENTITY_ID,PEER_TYPE, " + PeerGroup.PEER_TOP_FEATURES +
            " from " + peerGroupTableName + " where date_time >= ? and date_time < ? " + " and (" + querySecondHalf +
            ") order by ANOMALY_SCORE Desc"*/
          executeTopNForPeerGroup(peerGroupSql, startTimeStr, endTimeStr, x, topN, ipTrackerMap, killchainId, cache)
        }

        populateSummaryResult(x, buf, startTimeStr, endTimeStr)
      } catch {
        case e: Exception => {
          Logger.error("Failed to get entity summary for killchainId:" + killchainId + ":" + e)
        }
      }

      buf
    }

  }

  /**
   * List of top 100 entity summary details for a specified (killchainId and securityEventId) for all security event types
   * for a given day sorted by risk scores.
   * @param killchainId Int specifying KillchainId
   * @param securityEventId Int specifying SecurityEventId
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @param cache FeatureServiceApiCache
   *
   * @return topN ListBuffer.empty[MutableMap[String, Any]
   */
  def getEntitySummaryByKillchainIdSecurityEventId(killchainId: Int, securityEventId: Int, startTimeStr: String,
                                                   endTimeStr: String, topN: Int, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (!(topN > 0)) {
      Logger.debug(" Empty Result will be returned. Because N specified = " + topN)
      buf
    }
    else {
      val beaconTableName = Beacons.getName(conf)
      val coordTableName = CoordActivity.getName(conf)
      val peerGroupTableName = PeerGroup.getName(conf)
      val beaconSqlStr: String = "select distinct SOURCE_NAME_OR_IP, RISK, EVENT_TIME from " + beaconTableName +
        " where  event_time >= ? and event_time < ? order by Risk Desc Limit " + topN
      val coordSql: String = "select distinct SOURCE_NAME_OR_IP, DATE_TIME, ANOMALY_CLUSTER_SCORE from " + coordTableName +
        " where  date_time >= ? and date_time < ? order by ANOMALY_CLUSTER_SCORE DEsc Limit " + topN

      val x: mutable.PriorityQueue[EntityRiskOrder] = new mutable.PriorityQueue[EntityRiskOrder]()
      try {
        val modelsMap: MutableMap[Integer, String] = cache.getModelsMap

        val ipTrackerMap: MutableMap[String, EntityRiskOrder] = MutableMap[String, EntityRiskOrder]()
        val killchainSecDataMap: MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = cache.getKillchainToSecurityEventDataMap()
        val secEventDataList: ListBuffer[SecurityEventTypeConfiguration] = {
          if (killchainSecDataMap.contains(killchainId)) killchainSecDataMap(killchainId)
          else new ListBuffer[SecurityEventTypeConfiguration]
        }
        var querySecondHalf: String = ""
        secEventDataList.foreach { eventData =>
          if (eventData.getSecurityEventTypeId.equals(securityEventId)) {
            val modelId: Int = eventData.getModel
            val modelName: String = {
              if (modelsMap.contains(modelId)) {
                modelsMap(modelId)
              } else {
                Logger.error("No model associated with EventId " + securityEventId)
                null
              }
            }

            //forming the object to hold all killchain related data
            val obj: KillchainHelper = new KillchainHelper(killchainId, securityEventId, modelId, modelName)

            //Check if the Security Event belongs to BeaconModel, Bot/Coordinated Activity Model or any other model
            if (modelName.equals(Constants.BeaconModelTuple._2)) {
              executeTopN(beaconSqlStr, startTimeStr, endTimeStr, x, topN, "beacon", obj, ipTrackerMap)
            } else if (modelName.equals(Constants.WebCoordinatedBehaviorModelTuple._2)) {
              executeTopN(coordSql, startTimeStr, endTimeStr, x, topN, "coord", obj, ipTrackerMap)
            } else {
              val str = "(" + PeerGroup.PEER_TOP_FEATURES + " = " + "'[" + eventData.getSecurityEventTypeId.toString + "]'" +
                " AND " + PeerGroup.PEER_TYPE + " = " + modelId + ")"
              if (querySecondHalf.equals("")) {
                querySecondHalf = str
              } else {
                querySecondHalf = querySecondHalf + " OR " + str
              }

            }


          }
        }
        if (!querySecondHalf.isEmpty) {
          val peerGroupSql: String = String.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s >= ? AND %s < ? AND (%s) ORDER BY %s DESC",
            PeerGroup.DATE_TIME, PeerGroup.ANOMALY_SCORE, PeerGroup.ENTITY_ID, PeerGroup.PEER_TYPE,
            PeerGroup.PEER_TOP_FEATURES, peerGroupTableName, PeerGroup.DATE_TIME, PeerGroup.DATE_TIME, querySecondHalf,
            PeerGroup.ANOMALY_SCORE)
          /*val peerGroupSql: String = "select DATE_TIME, ANOMALY_SCORE, ENTITY_ID,PEER_TYPE, " + PeerGroup.PEER_TOP_FEATURES +
            " from " + peerGroupTableName + " where date_time >= ? and date_time < ? " + " and (" + querySecondHalf +
            " ) order by ANOMALY_SCORE DESC"*/
          executeTopNForPeerGroup(peerGroupSql, startTimeStr, endTimeStr, x, topN, ipTrackerMap, killchainId, cache)
        }

        populateSummaryResult(x, buf, startTimeStr, endTimeStr)

      } catch {
        case e: Exception => {
          Logger.error(
            "Failed to get entity summary for killchainId:" + killchainId + " and securityEventId: " +
              securityEventId + ": " +
              e)
        }
      }
      buf

    }
  }

  private def populateSummaryResult(priorityQueue: mutable.PriorityQueue[EntityRiskOrder],
                                    buf: ListBuffer[MutableMap[String, Any]], startTime: String, endTime: String) = {
    val entityFusionHourlySummaryDao: EntityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf)
    val ipToUserNameMap: MutableMap[String, ListBuffer[EntityInfo]] = entityFusionHourlySummaryDao
                                                                      .getIpToEntityInfo(startTime, endTime)
    for (i <- 0 until priorityQueue.size) {
      val entity: EntityRiskOrder = priorityQueue.dequeue()
      entity.riskList.foreach { tuple =>
        val selectionMap = MutableMap[String, Any]()
        selectionMap += "modelId" -> tuple._2.modelId
        selectionMap += "modelName" -> tuple._2.modelName
        val ip: String = entity.ip
        if (Constants.ADPeerAnomaliesModelTuple._1.equals(tuple._2.modelId)) {
          //If AD peer, then it will have only username. No Ip.
          selectionMap += "entityName" -> ip
          selectionMap += "entityIp" -> ""
        } else {
          selectionMap += "entityIp" -> ip
          val entityInfo = entityFusionHourlySummaryDao.getEntityInfoFromIpToUserMap(ipToUserNameMap, ip, entity.dateTime)
          val userName = if (entityInfo == null) "" else entityInfo.getUserName
          selectionMap += "entityName" -> userName
        }
        selectionMap += "risk" -> tuple._1
        selectionMap += "dateTime" -> entity.dateTime
        selectionMap += "killchainId" -> tuple._2.killchainId
        selectionMap += "securityEventId" -> tuple._2.securityEventId
        buf += selectionMap
      }
    }
  }


  /**
   * This method is used to execute the sqlQuery for Beacons and Bots only
   * The query should take in 2 param: StartTime, endTIme
   * @param sqlStr String sql to be executed
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param q PriorityQueue which hold the TopN results
   * @param topN Integer specifying the limit for PriorityQueue
   * @param feature String featureString can be beacon or coord
   * @param helperObj
   */
  private def executeTopN(sqlStr: String, startTimeStr: String, endTimeStr: String,
                          q: mutable.PriorityQueue[EntityRiskOrder], topN: Int, feature: String,
                          helperObj: KillchainHelper, trackerMap: MutableMap[String, EntityRiskOrder]) = {

    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        var entity: EntityRiskOrder = null
        if (feature.equals("beacon")) {
          entity = new EntityRiskOrder(rs.getDouble("RISK"), rs.getString("SOURCE_NAME_OR_IP"), rs.getString("EVENT_TIME"))
          addEntityRiskToQueue(q, entity, topN, trackerMap, helperObj)
        } else if (feature.equals("coord")) {
          entity = new EntityRiskOrder(rs.getDouble("ANOMALY_CLUSTER_SCORE"), rs.getString("SOURCE_NAME_OR_IP"),
            rs.getString("DATE_TIME"))
          addEntityRiskToQueue(q, entity, topN, trackerMap, helperObj)
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get topN for: " + feature + ": " + ex)
    } finally {
      closeConnections(conn)
    }
  }


  /**
   * This method is used to execute the sqlQuery for PeerGroup only
   * The query should take in 3 param: StartTime, endTIme and Feature_Id
   * @param sqlStr String sql to be executed
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param q PriorityQueue which hold the TopN results
   * @param topN Integer specifying the limit for PriorityQueue
   *
   */
  private def executeTopNForPeerGroup(sqlStr: String, startTimeStr: String, endTimeStr: String,
                                      q: mutable.PriorityQueue[EntityRiskOrder], topN: Int,
                                      trackerMap: MutableMap[String, EntityRiskOrder], killchainId: Int,
                                      cache: FeatureServiceCache) = {
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      rs = executeQuery(pstmt)
      var counter: Int = 0;

      while (rs.next() && counter < topN) {
        try {
          var entity: EntityRiskOrder = null
          val s: String = rs.getString(PeerGroup.ENTITY_ID.toString)
          val featureId = getFeatureIdFromPeerTopFeatures(rs.getString(PeerGroup.PEER_TOP_FEATURES.toString))
          val modelId = rs.getInt(PeerGroup.PEER_TYPE.toString)
          val modelName = cache.getModelNameFromId(modelId)
          val helperObj: KillchainHelper = new KillchainHelper(killchainId, featureId, modelId, modelName)
          var arr: Array[String] = null

          //Hack: Because the output format from web peer model is not consistent with ad peer model.
          if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
            arr = s.split(", ")
          } else {
            arr = s.split(",")
          }
          for (i <- 0 until arr.length) {
            val tmp: String = arr(i).replaceAll("\"", "")
            val s = tmp.replaceAll("\\[", "")
            val ip = s.replaceAll("\\]", "")
            val entity: EntityRiskOrder = new EntityRiskOrder(rs.getDouble(PeerGroup.ANOMALY_SCORE.toString), ip, rs.getString(PeerGroup.DATE_TIME.toString))
            addEntityRiskToQueue(q, entity, topN, trackerMap, helperObj)
            counter = counter + 1
          }
        } catch {
          case e: Exception => {
            Logger.error("Failed to get topN for peer groups for killchainId: " + killchainId + ": " + e)
          }
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get peer groups: " + ex)
    } finally {
      closeConnections(conn)
    }
  }


  /**
   * Adds the Entity with Risk info into the queue
   * @param q PriorityQueue of EntityRiskOrder
   * @param entity EntityRiskOrder
   * @param size Int max size of queue
   */
  def addEntityRiskToQueue(q: mutable.PriorityQueue[EntityRiskOrder], entity: EntityRiskOrder, size: Int,
                           trackerMap: MutableMap[String, EntityRiskOrder], killchainHelperObj: KillchainHelper): Unit = {

    //if we already have this ip in the queue, just update the queue node
    if (trackerMap.contains(entity.ip)) {
      var node: EntityRiskOrder = trackerMap(entity.ip)
      val risk: Double = node.maxRisk

      if (entity.maxRisk > node.maxRisk) {
        node.maxRisk = entity.maxRisk
      }
      val list: ListBuffer[(Double, KillchainHelper)] = node.riskList
      if (!list.contains((entity.maxRisk, killchainHelperObj)))
        list += ((entity.maxRisk, killchainHelperObj))
    } else {
      if (q.size < size) {
        entity.riskList += ((entity.maxRisk, killchainHelperObj))
        q.enqueue(entity)
        trackerMap += entity.ip -> entity
      } else {
        if (q.head.maxRisk < entity.maxRisk) {
          q.dequeue()
          q.enqueue(entity)
          entity.riskList += ((entity.maxRisk, killchainHelperObj))
          trackerMap.remove(entity.ip)
          trackerMap += entity.ip -> entity
        }
      }
    }
  }

  /**
   * The value stored in column PeerGroup.PEER_TOP_FEATURES is a string of Array.
   * eg: "[10]"
   * This method parses this value and extracts the FeatureId out of it.
   * @param peerTopFeatures String
   * @return Int featureId
   */
  private def getFeatureIdFromPeerTopFeatures(peerTopFeatures: String): Int = {
    val featureString = peerTopFeatures.replace("[", "").replace("]", "")
    featureString.toInt
  }

}

/**
 * Stores the EntityInformation (riskScore, ip, dateTime, killchainId)
 * Helper class that is used to implement a PriorityQueue as minHeap
 *
 * @param maxRisk Double riskSoce of an Entity
 * @param ip String Ip address of the Entity
 * @param dateTime String indicating DateTime
 */
case class EntityRiskOrder(var maxRisk: Double, ip: String, dateTime: String) extends Ordered[EntityRiskOrder] {
  val riskList: ListBuffer[(Double, KillchainHelper)] = ListBuffer[(Double, KillchainHelper)]()

  def compare(that: EntityRiskOrder) = that.maxRisk.compareTo(this.maxRisk)
}

/**
 * Helper class to store KillchainId, securityEventId, SecurityEvent Type, Type pre-fix, Model Name and Model Id
 * for any entity
 *
 * @param killchainId Int Killchain Id
 * @param securityEventId Int security Event Id
 * @param modelId Int model Id
 * @param modelName Int ModelName
 */
case class KillchainHelper(killchainId: Int, securityEventId: Int, modelId: Int, modelName: String) {
}

object HomePageDao {}
