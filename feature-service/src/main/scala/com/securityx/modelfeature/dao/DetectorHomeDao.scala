package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.cache.AutoCompleteCache
import com.securityx.modelfeature.common.inputs.{QueryJson, SecurityEventBehavior}
import com.securityx.modelfeature.common.{EntityInfo, EntityModelInfo}
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, SecurityEventTypeConfiguration}
import com.securityx.modelfeature.queryengine.QueryGenerator
import com.securityx.modelfeature.utils._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.collection.JavaConversions._

class DetectorHomeDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[DetectorHomeDao])
  val taniumStatsDao = new TaniumStatsDao(conf)
  val suppressionDao = new SuppressionDao(conf)

  def getEntityAllRiskScores(startTime: String, endTime: String, topN: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = DetectorHome.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val sqlStr =
        "SELECT " + DetectorHome.columns + "  FROM " +
          tableName + " WHERE " + DetectorHome.DATE_TIME + " >= ? and " + DetectorHome.DATE_TIME + "  <= ? ORDER BY "
      DetectorHome.CURRENT_SCORE + " DESC " + " limit ?"
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, topN)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get RiskScores from  DetectorHome => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * Uses the input QueryJson to formulate the sql query and
   * returns the entities, associated security events and risk score.
   *
   * @param input QueryJson
   * @param cache FeatureServiceCache
   * @return
   */
  def getThreats(input: QueryJson, entityToEntityBehaviorMap: util.Map[String, util.List[SecurityEventBehavior]],
                 entityToEntityRiskMap: util.Map[EntityInfo, java.lang.Double],
                 cache: FeatureServiceCache) = {
    val buf = MutableMap[EntityInfo, EntityModelInfo]()
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)

      //used to set params for PreparedStatement
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      var sqlStr: String =  "Select " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " +
        EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", " + "MAX(" + EntityThreat.RISK_SCORE + ") AS RISK" +
        " from " + EntityThreat.getName(conf)
      val aliasMap = MutableMap[String, Any]()
      aliasMap += EntityThreat.RISK_SCORE.toString -> "RISK"

      val groupbyString =  " GROUP BY " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.MAC_ADDRESS + ", " + EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID  + ", " + EntityThreat.SECURITY_EVENT_ID

      //get predicate part of the query
      val predicateString = getSqlPredicateString(input, pstmtList, groupbyString, null, null, aliasMap, cache, isSearchQuery = false)
      sqlStr = sqlStr + " " + predicateString
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        //TODO replacing for ' ' with '' is a hack which should be removed
        //=====
        val ipAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        val hostName: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val userName: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val macAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.MAC_ADDRESS.toString), conf)
        //=====
        val dateTime: String = MiscUtils.stringNullCheck(rs.getString(EntityThreat.DATE_TIME.toString))
        val entity: EntityInfo = new EntityInfo(null, ipAddress, macAddress, hostName, userName)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        var riskScore: Double = rs.getDouble(aliasMap(EntityThreat.RISK_SCORE.toString).toString)
        riskScore = MathUtils.formatDecimal(riskScore)

        //true if ipAddress is NOT a part of Alert Filter (whitelisted entities)
        val filterCheck : Boolean = !(ipAddress != null && entityToEntityBehaviorMap.containsKey(ipAddress) &&
          SecurityEventBehavior.showsBehavior(entityToEntityBehaviorMap.get(ipAddress),securityEventId, modelId))
        //true if "riskScore" for "ipAddress" is greater than riskScore in the previous Alert (coming from entityToEntityRiskMap)
        val entityScoreCheck : Boolean = !( entityToEntityRiskMap.containsKey(entity) &&
          ( riskScore - entityToEntityRiskMap.get(entity) <= 0 ) )

        //only if the above two conditions are evaluated to true, we can add the entity in the result.
        if (filterCheck && entityScoreCheck) {

          val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
          val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
          val securityEventData: SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
          if(securityEventData != null) {
            val eventTypePrefix: String = securityEventData.getTypePrefix
            val eventType: String = securityEventData.getEventType
            val eventDescription: String = securityEventData.getEventDescription
            val shortDescription: String = securityEventData.getShortDescription
            val killchainId: Int = securityEventData.getKillchainId
            val featureLabel: String = securityEventData.getFeatureLabel
            val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId

            val entityModelInfo = new EntityModelInfo(killchainId, securityEventId, featureLabel, eventTypePrefix, eventType,
              eventDescription, shortDescription, modelId, cardId, riskScore, dateTime)
            if(!buf.contains(entity)) {
              buf += entity -> entityModelInfo
            }
          }
        }

      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_1 => " + ex, ex)
    } finally {
      closeConnections(conn)
    }

    buf

  }

  /**
   * Uses the input QueryJson to formulate the sql query and
   * returns the entities, associated security events and risk score.
   *
   * @param input QueryJson
   * @param cache FeatureServiceCache
   * @return
   */
  def getEntityScores(input: QueryJson, returnEntityIds: Boolean, cache: FeatureServiceCache) = {
    getEntityRiskScores(input, cache, isSearchQuery = false, returnEntityIds)
  }


  /**
   * Uses the input QueryJson to formulate the sql query and
   * Searches the entities in the input, associated security events and risk score.
   *
   * @param input QueryJson
   * @param cache FeatureServiceCache
   * @return
   */
  def getSearchedEntityScores(input: QueryJson, returnEntityIds: Boolean, cache: FeatureServiceCache) = {
    getEntityRiskScores(input, cache, isSearchQuery = true, returnEntityIds = returnEntityIds)
  }

  // Changed 5/4/2016. Not currently used, but maybe useful in the future.
  def getEntityRiskScoresByRisk(input: QueryJson, cache: FeatureServiceCache, isSearchQuery: Boolean) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val expectedNumEntities = input.getLimit.intValue()
    // Set limit to 0 to fetch all records
    input.setLimit(0)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      //val ipToScoresMap: MutableMap[EntityUniqueness, MutableMap[String, Any]] = MutableMap[EntityUniqueness, MutableMap[String, Any]]()
      val ipToScoresMap: MutableMap[EntitySecurityEventModelDateTimeUniqueness, MutableMap[String, Any]]
        = MutableMap[EntitySecurityEventModelDateTimeUniqueness, MutableMap[String, Any]]()

      conn = getConnection(conf)
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      var sqlStr: String = "Select " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " +
        EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", " + EntityThreat.RISK_SCORE +
        " from " + EntityThreat.getName(conf)

      val predicateString = getSqlPredicateString(input, pstmtList, "", "risk", "DESC", null, cache, isSearchQuery)
      sqlStr = sqlStr + " " + predicateString
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)
      rs = executeQuery(pstmt)

      while (rs.next() && ipToScoresMap.size < expectedNumEntities) {
        //TODO replacing for ' ' with '' is a hack which should be removed
        //===========
        val ipAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        val hostName: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val userName: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val macAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.MAC_ADDRESS.toString), conf)
        //===========
        val riskScore: Double = rs.getDouble(EntityThreat.RISK_SCORE.toString)
        val dateTime: String = rs.getString(EntityThreat.DATE_TIME.toString)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val entity: EntityUniqueness = EntityUniqueness(ipAddress, userName, hostName, macAddress)
        val entitySecModelDate: EntitySecurityEventModelDateTimeUniqueness = EntitySecurityEventModelDateTimeUniqueness(entity, securityEventId, modelId, dateTime)
        val securityEventData : SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId,modelId)
        // If we've already seen another instance of this entity, we skip this.  The entity we already have must have a higher
        // risk score because the query was ordered by risk
        if(securityEventData != null && !ipToScoresMap.contains(entitySecModelDate)) {
          val eventTypePrefix: String = securityEventData.getTypePrefix
          val eventType: String = securityEventData.getEventType
          val eventDescription: String = securityEventData.getEventDescription
          val shortDescription: String = securityEventData.getShortDescription
          val killchainId: Int = securityEventData.getKillchainId
          val featureLabel: String = securityEventData.getFeatureLabel
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId

          var selectionMap: MutableMap[String, Any] = MutableMap[String, Any]()
          ipToScoresMap += entitySecModelDate -> selectionMap
          buf += selectionMap

          val eventInfo: EntityModelInfo = new EntityModelInfo(securityEventId, killchainId, featureLabel,
            eventTypePrefix, eventType, eventDescription, shortDescription, modelId, cardId, riskScore, dateTime)

          //killchain info list
          val list: ListBuffer[EntityModelInfo] = {
            if (selectionMap.contains("modelScores"))
              selectionMap("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
            else
              ListBuffer[EntityModelInfo]()
          }
          list += eventInfo

          //populate result map
          selectionMap += "ipAddress" -> ipAddress
          selectionMap += "hostName" -> hostName
          selectionMap += "userName" -> userName
          selectionMap += "macAddress" -> macAddress
          selectionMap += "currentScore" -> riskScore
          selectionMap += "modelScores" -> list
          selectionMap += "currentSecurityEventId" -> securityEventId
          selectionMap += "currentModelId" -> modelId
          selectionMap += "currentCardId" -> cardId
          selectionMap += "currentDateTime" -> dateTime
        }
      }

    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_2 => " + ex, ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }
  def getNumRows(rs: ResultSet): Int ={
    var rowCount = 0
    try {
      if (rs != null) {
        if (rs.last()) {
          rowCount = rs.getRow
          rs.beforeFirst()
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to fetch num rows => " + ex, ex)
    }
    rowCount
  }
  /**
   * Uses the input QueryJson to formulate the sql query and
   * returns the entities, associated security events and risk score.
   *
   * @param input QueryJson
   * @param cache FeatureServiceCache
   * @param returnEntityIds Return entity ids is a flag to tell us whether we're being called in the new entity fusion
   *                        world or in the old ip-mac-hostname-username world.  This method should support both
   *                        behaviors for the moment, but once all the old style data is gone from any customer site,
   *                        we should remove it and only support the new entity fusion system (i.e. entity_ids).
   *                        This will unfortunately mean that this method is somewhat complex in the short term.
   * @return
   */
  def getEntityRiskScores(input: QueryJson, cache: FeatureServiceCache, isSearchQuery: Boolean, returnEntityIds: Boolean) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val expectedNumEntities = input.getLimit.intValue()
    // Set limit to 0 to fetch all records
    input.setLimit(0)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val ipToScoresMap: MutableMap[EntityUniqueness, MutableMap[String, Any]] = MutableMap[EntityUniqueness, MutableMap[String, Any]]()
      val entityIdToScoresMap: MutableMap[String, MutableMap[String, Any]] = MutableMap[String, MutableMap[String, Any]]()
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      // if isSearchQuery is true, we want to return only threats directly related to the search terms - specifically, if the search
      // term includes user name, we should return threats from user models.  Likewise, if the search includes host name, ip or
      // mac address, we should return threats from host models.
      var returnHostThreats = true
      var returnUserThreats = true
      if (isSearchQuery) {
        returnHostThreats = queryContainsTerm(input, hostTerm = true)
        returnUserThreats = queryContainsTerm(input, hostTerm = false)
        if (!returnHostThreats && !returnUserThreats) {
          // This is a corner case - it can only happen if the query passed in contains no search terms for either user or
          // host, which should never happen. But if by chance it does, we would return nothing, since both booleans would
          // be set to false. Instead, for such a case we will return everything.
          returnHostThreats = true
          returnUserThreats = true
        }
      }

      conn = getConnection(conf)

      //used to set params for PreparedStatement
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      var sqlStr: String = "Select " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " +
        EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", "
      if (returnEntityIds) {
        sqlStr = sqlStr + EntityThreat.HOST_ENTITY_ID + ", " + EntityThreat.USER_ENTITY_ID + ", "
      }
      sqlStr = sqlStr + "MAX(" + EntityThreat.RISK_SCORE + ") AS RISK from " + EntityThreat.getName(conf)
      val aliasMap = MutableMap[String, Any]()
      aliasMap += EntityThreat.RISK_SCORE.toString -> "RISK"

      var groupbyString =  " GROUP BY " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.MAC_ADDRESS + ", " + EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID  + ", " + EntityThreat.SECURITY_EVENT_ID
      if (returnEntityIds) {
        groupbyString = groupbyString + ", " + EntityThreat.HOST_ENTITY_ID + ", " + EntityThreat.USER_ENTITY_ID
      }

      //get predicate part of the query
      val predicateString = getSqlPredicateString(input, pstmtList, groupbyString, null, null, aliasMap, cache, isSearchQuery)
      sqlStr = sqlStr + " " + predicateString
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)
      rs = executeQuery(pstmt)
      Logger.debug("Executing "+ pstmt.toString()+" : "+ pstmtList.toString())
      // If returnEntityIds is true we will only put data into entityIdToScoresMap, while if returnEntityIds is false, we
      // will only put data into ipToScoresMap.  Thus, if either one is larger than expectedNumEntities, we have all
      // the entities we should return.
      var numRecords = 0
      while (rs.next() && ipToScoresMap.size < expectedNumEntities && entityIdToScoresMap.size < expectedNumEntities) {
        numRecords += 1 //increment the number of records
        val ipAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        val hostName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val userName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val macAddress: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.MAC_ADDRESS.toString), conf)
        val hostEntityId: String = if (returnEntityIds) {MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_ENTITY_ID.toString), conf)} else {""}
        val userEntityId: String = if (returnEntityIds) {MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_ENTITY_ID.toString), conf)} else {""}
        val dateTime: String = rs.getString(EntityThreat.DATE_TIME.toString)
        if ((hostEntityId == null || hostEntityId.isEmpty) && (userEntityId == null || userEntityId.isEmpty)) {
          Logger.warn("got result from entity_threat where host_entity_id and user_entity_id are both null. " +
            "date_time [" + dateTime + "] ip_address [" + ipAddress + "] host_name [" + hostName + "] user_name [" + userName + "] mac_address [" + macAddress + "]")
        }
        val riskScore: Double = rs.getDouble(aliasMap(EntityThreat.RISK_SCORE.toString).toString)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val securityEventData : SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId,modelId)
        val isHostThreat = isHostModel(modelId, cache)
        val shouldSuppress = if (securityEventData == null) true
          else suppressionDao.shouldSuppress(suppressionList, ipAddress, hostName, userName, securityEventData.getEventType, false)
        var returnThreat = true
        if (isHostThreat && returnHostThreats || !isHostThreat && returnUserThreats) {
          returnThreat = true
        } else {
          returnThreat = false
        }
        if(shouldSuppress) {
          suppressionCount += 1
        } else if (!returnThreat) {
          Logger.debug("Not including threat [" + ipAddress + ", " + hostName + ", " + userName + ", " + macAddress +
            ", " + modelId + ", " + returnHostThreats + ", " + returnUserThreats + "] because search terms do not match model")
        } else {
          val eventTypePrefix: String = securityEventData.getTypePrefix
          val eventType: String = securityEventData.getEventType
          val eventDescription: String = securityEventData.getEventDescription
          val shortDescription: String = securityEventData.getShortDescription
          val killchainId: Int = securityEventData.getKillchainId
          val featureLabel: String = securityEventData.getFeatureLabel
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId

          var selectionMap: MutableMap[String, Any] = null
          if (returnEntityIds) {
            val entityId = if (isHostModel(modelId, cache)) hostEntityId else userEntityId
            if (entityIdToScoresMap.contains(entityId)) {
              selectionMap = entityIdToScoresMap(entityId)
            } else {
              selectionMap = MutableMap[String, Any]()
              entityIdToScoresMap += entityId -> selectionMap
              buf += selectionMap
            }
          } else {
            val entity: EntityUniqueness = EntityUniqueness(ipAddress, userName, hostName, macAddress)
            //if Ip already exists in the result buf
            if (ipToScoresMap.contains(entity)) {
              selectionMap = ipToScoresMap(entity)
            }
            else {
              selectionMap = MutableMap[String, Any]()
              ipToScoresMap += entity -> selectionMap
              buf += selectionMap
            }
          }

          var currentSecurityEventId: Int = 0
          var currentModelId: Int = 0
          var currentCardId: Int = 0
          var currentDateTime: String = ""
          var currentScore: Double = 0.0

          //find current score, currentSecurityEventId, currentModelId, currentCardId, currentDateTime
          if (selectionMap.contains("currentScore")) {
            val prevScore: Double = selectionMap("currentScore").asInstanceOf[Double]

            // if score is updated then update currentSecurityEventId, currentModelId, currentCardId, currentDateTime
            if (riskScore > prevScore) {
              currentScore = riskScore
              currentSecurityEventId = securityEventId
              currentModelId = modelId
              currentCardId = cardId
              currentDateTime = dateTime
            } else {
              currentScore = selectionMap("currentScore").asInstanceOf[Double]
              currentSecurityEventId = selectionMap("currentSecurityEventId").asInstanceOf[Int]
              currentModelId = selectionMap("currentModelId").asInstanceOf[Int]
              currentCardId = selectionMap("currentCardId").asInstanceOf[Int]
              currentDateTime = selectionMap("currentDateTime").asInstanceOf[String]
            }
          }
          else {
            currentScore = riskScore
            currentSecurityEventId = securityEventId
            currentModelId = modelId
            currentCardId = cardId
            currentDateTime = dateTime
          }

          val eventInfo: EntityModelInfo = new EntityModelInfo(securityEventId, killchainId, featureLabel,
            eventTypePrefix, eventType, eventDescription, shortDescription, modelId, cardId, riskScore, dateTime)

          //killchain info list
          val list: ListBuffer[EntityModelInfo] = {
            if (selectionMap.contains("modelScores"))
              selectionMap("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
            else
              ListBuffer[EntityModelInfo]()
          }
          list += eventInfo

          //populate result map
          selectionMap += "ipAddress" -> ipAddress
          selectionMap += "hostName" -> hostName
          selectionMap += "userName" -> userName
          selectionMap += "macAddress" -> macAddress
          selectionMap += "currentScore" -> currentScore
          selectionMap += "modelScores" -> list
          selectionMap += "currentSecurityEventId" -> currentSecurityEventId
          selectionMap += "currentModelId" -> currentModelId
          selectionMap += "currentCardId" -> currentCardId
          selectionMap += "currentDateTime" -> currentDateTime
          if (returnEntityIds) {
            val (entityId, entityType) =
              getEntityIdAndType(hostEntityId, userEntityId, modelId, ipAddress, hostName, userName, macAddress, securityEventId, cache)
            selectionMap += "entityId" -> entityId
            selectionMap += "entityType" -> entityType
          }
        }
      }
      Logger.debug("Fetched number of records: {}. Number of suppressed records: {}", numRecords, suppressionCount)
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_3 => " + ex, ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }

  def getEntityIdAndType(hostEntityId: String, userEntityId: String, modelId: Int, ipAddress: String, hostName: String,
                         userName: String, macAddress: String, securityEventId: Int, cache: FeatureServiceCache) = {
    var entityId: String = ""
    var entityType: String = ""
    if (isHostModel(modelId, cache)) {
      if (hostEntityId != null && !hostEntityId.isEmpty) {
        entityId = hostEntityId
        entityType = "host"
      } else {
        Logger.warn("getEntityRiskScores: host entity id is null for ipAddress [" + ipAddress + "] hostName [" +
          hostName + "] userName [" + userName + "] macAddress [" + macAddress + "] model [" + modelId + "] securityEventId [" + securityEventId + "]")
      }
    } else {
      if (userEntityId != null && !userEntityId.isEmpty) {
        entityId = userEntityId
        entityType = "user"
      } else {
        Logger.warn("getEntityRiskScores: user entity id is null for ipAddress [" + ipAddress + "] hostName [" +
          hostName + "] userName [" + userName + "] macAddress [" + macAddress + "] model [" + modelId + "] securityEventId [" + securityEventId + "]")
      }
    }
    (entityId, entityType)
  }

  def isHostModel(modelId: Int, cache: FeatureServiceCache) = {
    val modelEntityTypeMap = cache.getModelsEntityTypeMap
    // If there is no entity type in the map for this model, it's probably an error in configuration, but
    // since scala is too stupid to let us say that, just assume host in that case.
    val entityType:String = modelEntityTypeMap.getOrElse(modelId, "host")
    if (entityType.toLowerCase == "host") {
      true
    } else {
      false
    }
  }

  def getModelScoresById(entityId: String, startTime: String, endTime: String, lowerRisk: Double, upperRisk: Double, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      conn = getConnection(conf)

      val selectList: String = "Select " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " +
        EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", " +
        EntityThreat.HOST_ENTITY_ID + ", " + EntityThreat.USER_ENTITY_ID + ", " +
        "MAX(" + EntityThreat.RISK_SCORE + ") AS RISK from " + EntityThreat.getName(conf)
      val aliasMap = MutableMap[String, Any]()
      aliasMap += EntityThreat.RISK_SCORE.toString -> "RISK"

      val groupbyString =  " GROUP BY " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.MAC_ADDRESS + ", " + EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID  + ", " + EntityThreat.SECURITY_EVENT_ID + ", " +
        EntityThreat.HOST_ENTITY_ID + ", " + EntityThreat.USER_ENTITY_ID

      // We don't know if the entity id is a host or a user entity, so we have to query both the host and user entity id columns for the entity
      val predicate = " WHERE (" + EntityThreat.HOST_ENTITY_ID + " = ? OR " + EntityThreat.USER_ENTITY_ID + " = ?) AND " + EntityThreat.RISK_SCORE + " >= ? AND " +
        EntityThreat.RISK_SCORE + " <= ? AND " + EntityThreat.DATE_TIME + " >= ? AND " + EntityThreat.DATE_TIME + " < ?"

      val sqlStr = selectList + predicate + groupbyString
      Logger.debug("getModelScoresById: query is [" + sqlStr + "] for entityId [" + entityId + "] lowerRisk [" + lowerRisk + "] upperRisk [" + upperRisk + "] startTime [" + startTime + "] endTime [" + endTime + "]")
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, entityId)
      pstmt.setString(2, entityId)
      pstmt.setDouble(3, lowerRisk)
      pstmt.setDouble(4, upperRisk)
      pstmt.setString(5, startTime)
      pstmt.setString(6, endTime)
      rs = executeQuery(pstmt)
      var selectionMap: MutableMap[String, Any] = MutableMap[String, Any]()
      val list: ListBuffer[EntityModelInfo] = ListBuffer[EntityModelInfo]()
      while (rs.next()) {
        val ipAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        val hostName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val userName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val hostEntityId: String = rs.getString(EntityThreat.HOST_ENTITY_ID.toString)
        val userEntityId: String = rs.getString(EntityThreat.USER_ENTITY_ID.toString)
        val riskScore: Double = rs.getDouble(aliasMap(EntityThreat.RISK_SCORE.toString).toString)
        val dateTime: String = rs.getString(EntityThreat.DATE_TIME.toString)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val securityEventData : SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId,modelId)

        val shouldSuppress = if (securityEventData == null) true
        else suppressionDao.shouldSuppress(suppressionList, ipAddress, hostName, userName, securityEventData.getEventType, false)
        if(shouldSuppress) {
          suppressionCount += 1
        } else {
          val eventTypePrefix: String = securityEventData.getTypePrefix
          val eventType: String = securityEventData.getEventType
          val eventDescription: String = securityEventData.getEventDescription
          val shortDescription: String = securityEventData.getShortDescription
          val killchainId: Int = securityEventData.getKillchainId
          val featureLabel: String = securityEventData.getFeatureLabel
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId
          val eventInfo: EntityModelInfo = new EntityModelInfo(securityEventId, killchainId, featureLabel,
            eventTypePrefix, eventType, eventDescription, shortDescription, modelId, cardId, riskScore, dateTime)
          list += eventInfo
        }
      }
      selectionMap += "modelScores" -> list
      buf += selectionMap
    } catch {
      case ex: Exception => Logger.error("Failed to get model scores => " + ex, ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }

  /**
   * Returns the list of top n risky entities by given security events.
   * The list is sorted by risk score in descending order.
   *
   * @param input QueryJson
   * @param cache FeatureServiceCache
   * @return
   */
  def getTopNRiskyEntitiesByBehavior(input: QueryJson, cache: FeatureServiceCache) = {
    val resultMap = MutableMap[String, Any]()
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      // List of entities to be excluded in the result set.  For the moment we will support both the old exclusion based
      // on ip/mac/host/user, and also the new approach based on entity id.
      val excludeEntities: util.List[EntityUniqueness] = new util.ArrayList[EntityUniqueness]()
      val excludeEntitiesById: util.List[String] = new util.ArrayList[String]()
      var excludeById = true
      var exclusion = "" // this is used to check that we only exclude by one of the exclusion methods

      // Going over the entities to be excluded from result set
      val entities: util.List[util.Map[String, String]] = input.getIgnoredEntities

      if (entities != null & !entities.isEmpty) {
        for (i <- 0 until entities.size()) {
          val entity: util.Map[String, String] = entities.get(i)
          if (entity.keySet().contains("entityId")) {
            if (exclusion.isEmpty) {
              exclusion = "byEntityId"
            }
            val entityId: String = entity.get("entityId")
            if (exclusion != "byEntityId") {
              Logger.warn("got exclusion entries for both uniqueness and id - ignoring entityId entry [" + entityId + "]")
            }
            excludeEntitiesById.add(entityId)
          } else {
            if (exclusion.isEmpty) {
              exclusion = "byUniqueness"
              excludeById = false
            }
            val sourceIp: String = entity.get("sourceIp")
            val hostName: String = entity.get("hostName")
            val userName: String = entity.get("userName")
            val macAddress: String = entity.get("macAddress")
            if (exclusion != "byUniqueness") {
              Logger.warn("got exclusion entries for both uniqueness and id - ignoring uniqueness entry [" + sourceIp + "/" + hostName + "/" + userName + "/" + macAddress + "]")
            }
            excludeEntities.add(EntityUniqueness(sourceIp, userName, hostName, macAddress))
          }
        }
      }

      // Assume limit a large number if it is not present in request data to get all records
      val limit = {
        if(input.getLimit != null && input.getLimit > 0) {
          val actualLimit = Integer2int(input.getLimit)
          // set limit to include additional size of excluded entities
          input.setLimit(actualLimit + excludeEntities.size())
          actualLimit
        }else{
          10000
        }
      }

      var sqlStr: String = "SELECT " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.MAC_ADDRESS + ", " + EntityThreat.HOST_ENTITY_ID + ", " +
        EntityThreat.USER_ENTITY_ID + ", " + EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", " +
        "MAX(" + EntityThreat.RISK_SCORE + ") AS RISK " +
        "FROM " + EntityThreat.getName(conf)

      val groupbyString: String = " GROUP BY " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.MAC_ADDRESS + ", " + EntityThreat.RISK_SCORE + ", " + EntityThreat.HOST_ENTITY_ID +
        ", " + EntityThreat.USER_ENTITY_ID + ", " + EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID

      val aliasMap = MutableMap[String, Any]()
      aliasMap += EntityThreat.RISK_SCORE.toString -> "RISK"

      // Where Clause
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      val q:QueryGenerator = new QueryGenerator
      // Set sort field and sort order
      input.setSortField(aliasMap(EntityThreat.RISK_SCORE.toString).toString.toLowerCase)
      input.setSortOrder("DESC")
      // Form where clause
      val predicateString = q.getSqlPredicateString(input, pstmtList, groupbyString, aliasMap , cache, isSearchQuery = false, conf.getFixNullValue)
      Logger.debug("sql predicate => " + predicateString)

      // Get connection
      conn = getConnection(conf)

      // SQL Query string
      sqlStr = sqlStr + " " + predicateString
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)

      rs = executeQuery(pstmt)
      var rsCount = 0
      // Because we may be supporting exclusion by either ip/mac/host/user or by entity id, we will have a scores map for both
      // methods. Only one will get values put into it, though.
      val ipToScoresMap: MutableMap[EntityUniqueness, MutableMap[String, Any]] = MutableMap[EntityUniqueness, MutableMap[String, Any]]()
      val entityIdToScoresMap: MutableMap[String, MutableMap[String, Any]] = MutableMap[String, MutableMap[String, Any]]()

      while (rs.next() && rsCount < limit) {
        //TODO replacing for ' ' with '' is a hack which should be removed
        //=====
        val ipAddress: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        val hostName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val userName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val macAddress: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.MAC_ADDRESS.toString), conf)
        val riskScore: Double = rs.getDouble(aliasMap(EntityThreat.RISK_SCORE.toString).toString)
        val hostEntityId: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_ENTITY_ID.toString), conf)
        val userEntityId: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_ENTITY_ID.toString), conf)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        //=====

        val (entityId, entityType) =
          getEntityIdAndType(hostEntityId, userEntityId, modelId, ipAddress, hostName, userName, macAddress, securityEventId, cache)

        var selectionMap: MutableMap[String, Any] = null
        val entity: EntityUniqueness =  EntityUniqueness(ipAddress, userName, hostName, macAddress)

        if (!excludeEntities.contains(entity) && !excludeEntitiesById.contains(entityId)) {
          if (excludeById) {
            if (entityIdToScoresMap.contains(entityId)) {
              selectionMap = entityIdToScoresMap(entityId)
            } else {
              selectionMap = MutableMap[String, Any]()
              entityIdToScoresMap += entityId -> selectionMap
              buf += selectionMap
            }
          } else {
            if (ipToScoresMap.contains(entity)) {
              selectionMap = ipToScoresMap(entity)
            } else {
              selectionMap = MutableMap[String, Any]()
              ipToScoresMap += entity -> selectionMap
              buf += selectionMap
            }
          }

          val currentScore: Double = {
            if (selectionMap.contains("currentScore"))
              Math.max(selectionMap("currentScore").asInstanceOf[Double], riskScore)
            else
              riskScore
          }

          //populate result map entries
          selectionMap += "ipAddress" -> ipAddress
          selectionMap += "hostName" -> hostName
          selectionMap += "userName" -> userName
          selectionMap += "macAddress" -> macAddress
          selectionMap += "currentScore" -> currentScore
          selectionMap += "entityId" -> entityId
          selectionMap += "entityType" -> entityType

          rsCount += 1
        }

      }

      // Populating Result Map
      resultMap.put("entities", buf)
      resultMap.put("total",buf.size)

    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_5 => " + ex, ex)
    } finally {
      closeConnections(conn)
    }

    resultMap
  }

  /**
   * Gets the top N Tanium hosts
   *
   * @param startTime
   * @param endTime
   * @param n
   * @param buf
   * @param cache
   * @return
   */
  // This code is not being updated as part of the endpoint bifurcation work. If we ever decide to call it again,
  // it will likely need work.
  def populateTopNTaniumThreats(startTime: String, endTime: String, n: Int,
                                 buf: collection.mutable.ListBuffer[MutableMap[String, Any]], cache: FeatureServiceCache, hosts: Array[String]) = {
     taniumStatsDao.getTaniumTopNHostsByRisk(startTime,endTime, n, buf, cache, hosts)
   }

  /**
   * Gets the counts for each killchain Id within a given time range
   *
   * @param startTime String specifying startTime
   * @param endTime String specifying endTie
   * @param cache FeatureServiceCache
   *
   * @return
   */
  def getKillchainCounts(startTime: String, endTime: String, cache: FeatureServiceCache) = {
    val queryJson: QueryJson = new QueryJson()
    queryJson.setStartTime(startTime)
    queryJson.setEndTime(endTime)
    val buf: ListBuffer[MutableMap[String, Any]] = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
      //This map killchainId to Map that stores kilchainId and count info keeps a track of all killchains already in our result buffer
      val mapping = MutableMap[Int, MutableMap[String,Any]]()

      //todo: need a better way to define all these constants.
      val killchainIdKey = "killchainId"
      val countKey = "count"

      //iterate over all the killchainIds and add count = 0 for each.
      // This is because, we want to show count = 0 even when any killchain is not found in our data
      cache.getKillchainMap.keySet.foreach{i =>
        val selectionMap = MutableMap[String, Any]()
        selectionMap += killchainIdKey -> i
        selectionMap += countKey -> 0
        buf += selectionMap
        mapping += i.intValue() ->selectionMap
      }

    populateKillchainEventCount(queryJson,buf, mapping, killchainIdKey, countKey, cache)

    buf

  }

  /**
   *
   * @param queryJson  QueryJson object to used to build the "where clause" of sql query
   * @param buf ListBuffer which stores the counts of killchains
   * @param killchainIdToResultMap Map of Killchain Id to Map that stores (killchainId and  count)
   * @param killchainIdKey String "killchainId"
   * @param countKey String "count"
   * @param cache FeatureServiceCache
   */
  private def populateKillchainEventCount(queryJson: QueryJson, buf: ListBuffer[MutableMap[String, Any]],
                             killchainIdToResultMap: MutableMap[Int,MutableMap[String, Any]],
                             killchainIdKey: String, countKey: String, cache: FeatureServiceCache) = {
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)

      //used to set params for PreparedStatement
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      var sqlStr: String = "Select " + EntityThreat.MODEL_ID + " , " + EntityThreat.SECURITY_EVENT_ID + ", " +
        "COUNT(*) as COUNT " + " from " + EntityThreat.getName(conf)

      //get predicate part of the query
      val predicateString = getSqlPredicateString(queryJson, pstmtList,  null, null, null, null, cache, isSearchQuery = false)
      sqlStr = sqlStr + " " + predicateString + " GROUP BY " +
        EntityThreat.MODEL_ID + " , " + EntityThreat.SECURITY_EVENT_ID
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val count : Int = rs.getInt("COUNT")
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val killchainId: Integer = cache.getKillchainIdFromSecurityEventModelIds(securityEventId, modelId)
        var entityCount: Int = 0
        if( killchainId != null && killchainIdToResultMap.contains(killchainId)){
          //"mapping" will always contain the killchain Id as we have added them at the start of the method
          val map: MutableMap[String, Any] = killchainIdToResultMap(killchainId)
          entityCount = count + map(countKey).toString.toInt
          map += countKey -> entityCount
        }else{
          Logger.error("Killchain Id => " + killchainId + " not found for model => " + modelId +
            " and  securiyEventId => " + securityEventId )
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_6 => " + ex, ex)
    } finally {
      closeConnections(conn)
    }
  }

  def queryContainsTerm(inputJson: QueryJson, hostTerm: Boolean): Boolean = {
    val queries: util.List[util.Map[String, AnyRef]] = inputJson.getQuery
    if(queries != null & !queries.isEmpty) {
      for (i <- 0 until queries.size()) {
        val query: util.Map[String, AnyRef] = queries.get(i)
        val filterName: String = query.get("field").asInstanceOf[String]
        // Note: the values that we compare filterName to here must match what the UI is passing in, which is
        // encoded in securityEvents.yml in the filterFields section
        if ((filterName == "sourceIp" || filterName == "hostName" || filterName == "macAddress") && hostTerm) {
          return true
        } else if (filterName == "userName" && !hostTerm) {
          return true
        }
      }
    }
    false
  }

  def getSqlPredicateString(inputJson: QueryJson, pstmtList: ListBuffer[ColumnMetaData],
                            groupbyString: String, sortField: String, sortOrder: String, aliasMap: MutableMap[String, Any], cache: FeatureServiceCache,
                            isSearchQuery: Boolean) = {
    val q: QueryGenerator = new QueryGenerator
    //filtering out web-behavior Security events
    val map: util.Map[String, Object] = new util.HashMap[String, Object]()
    map.put("field","modelId")
    map.put("operator", "not equals")
    val values: util.List[String] = new util.ArrayList[String]()
    //TODO: Remove thehardcoded filtering
    // This filtering (removing beacon and timeseries behavior from investigator page) can go into
    // suppression feature, with config settings for the feature. This should be done once suppression is being moved in the api
    values.add(Constants.BeaconModelTuple._1.toString)
    map.put("values", values)
    inputJson.getQuery.add(map)

    if (sortField != null) {
      inputJson.setSortField(sortField)
      inputJson.setSortOrder(sortOrder)
    }

    //form where clause
    val predicate = q.getSqlPredicateString(inputJson, pstmtList, groupbyString, aliasMap , cache, isSearchQuery, conf.getFixNullValue)
    Logger.debug("sql predicate => " + predicate)
    predicate
  }

  def getEntityCardsByEntityId(startTime: String, endTime: String, entityId: String, lowerRisk: Double, upperRisk: Double, cache: FeatureServiceCache):
      Map[MutableMap[String, Any],ListBuffer[MutableMap[String, Any]]]  = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sqlStr: String = "Select " + EntityThreat.MODEL_ID + " , " + EntityThreat.SECURITY_EVENT_ID + ", " +
        EntityThreat.IP_ADDRESS + ", " + EntityThreat.HOST_NAME + ", " + EntityThreat.USER_NAME + ", " +
        " MAX( " + EntityThreat.RISK_SCORE + " ) AS RISK " + " from " + EntityThreat.getName(conf) +
        " WHERE " + EntityThreat.DATE_TIME + " >= ? " + " AND " + EntityThreat.DATE_TIME + " < ? " +
      // We don't know if the entity is a user or a host entity, so we must check both columns
        " AND (" + EntityThreat.USER_ENTITY_ID + " = ? OR " + EntityThreat.HOST_ENTITY_ID + " = ?)" +
        " AND " + EntityThreat.RISK_SCORE + " >= ? " + " AND " + EntityThreat.RISK_SCORE + " <= ? " +
        " GROUP BY " + EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", " +
        EntityThreat.IP_ADDRESS + ", " + EntityThreat.HOST_NAME + ", " + EntityThreat.USER_NAME

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      // Note that we insert entityId twice - once for the user_entity_id clause and once for the host_entity_id clause
      pstmt.setString(3, entityId)
      pstmt.setString(4, entityId)
      pstmt.setDouble(5, lowerRisk)
      pstmt.setDouble(6, upperRisk)

      val entityEventInfoMap: mutable.Map[EntityEventInfo, RiskScoreHelper] = mutable.Map[EntityEventInfo, RiskScoreHelper]()
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      rs = executeQuery(pstmt)
      while (rs.next()) {
        val modelId = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        // TODO: We only get ip, host name and user name so we can check for suppression and for calling the methods to get model
        // TODO: details.  We should change all that over to using entity id at some point
        val ip = rs.getString(EntityThreat.IP_ADDRESS.toString)
        val hostName = rs.getString(EntityThreat.HOST_NAME.toString)
        val userName = rs.getString(EntityThreat.USER_NAME.toString)
        val riskScore = rs.getDouble("RISK")
        // Check for suppression
        val securityEventData: SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
        val shouldSuppress = if (securityEventData == null) true
        else suppressionDao.shouldSuppress(suppressionList, ip, hostName, userName, securityEventData.getEventType, false)

        if (shouldSuppress) {
          suppressionCount += 1
        } else {
          if (Constants.BeaconModelTuple._1.equals(modelId)) {
            if (conf.includeBeaconingBehaviors()) {
              val beaconsDao: BeaconsDao = new BeaconsDao(conf)
              beaconsDao.getEntityCardsForBeacons(startTime, endTime, ip, userName, cache, buf, riskScore)
            }
          } else if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId) || Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
            val entityAnomalyDetailsDao: EntityAnomalyDetailsDao = new EntityAnomalyDetailsDao(conf)
            entityAnomalyDetailsDao.getPeerEntityCardsById(startTime, endTime, entityId, securityEventId, modelId, riskScore, cache, buf)
          } else if (Constants.TaniumModelTuple._1.equals(modelId) || Constants.EndpointLocalModelTuple._1.equals(modelId) || Constants.EndpointGlobalModelTuple._1.equals(modelId)) {
            val taniumDao: TaniumStatsDao = new TaniumStatsDao(conf)
            taniumDao.getTaniumEntityCards(startTime, endTime, "", entityId, useEntityId = true, riskScore, modelId, cache, buf)
          } else if (Constants.AdNoveltyDetectorTuple._1.equals(modelId)) {
            val noveltyDetectorDao = new NoveltyDetectorDao(conf)
            noveltyDetectorDao.getNoveltyDetectorEntityCardsById(startTime, endTime, entityId, securityEventId, modelId, riskScore, cache, buf)
          } else if (Constants.C2ModelTuple._1.equals(modelId)) {
            if (conf.includeC2Behaviors()) {
              val c2ModelDao = new C2ModelDao(conf)
              c2ModelDao.getEntityCardForC2ById(startTime, endTime, entityId, securityEventId, modelId, riskScore, cache, buf)
            }
          }
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Cards for Entity => " + ex.getMessage, ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }

    val groups: Map[collection.mutable.Map[String, Any], ListBuffer[mutable.Map[String, Any]]] =
      buf.groupBy(x => collection.mutable.Map[String, Any](
        "modelId" -> x.get("modelId"),
        "killchainId" -> x.get("killchainId"),
        "cardId" -> x.get("cardId"),
        "eventId" -> x.get("eventId"),
        "featureDesc" -> x.get("featureDesc"),
        "securityEventType" -> x.get("securityEventType"),
        "riskScore" -> x.get("riskScore")
      )
      )

    groups.foreach { x =>
      var minDate: String = ""
      var maxDate: String = ""
      x._2.foreach { y =>
        val dateString: String = y.getOrElse("dateTime", "").toString
        if (minDate.equals("") || minDate.compareTo(dateString) > 0) {
          minDate = dateString
        }
        if (maxDate.equals("") || maxDate.compareTo(dateString) < 0) {
          maxDate = dateString
        }
      }
      x._1.put("minDate", minDate)
      x._1.put("maxDate", maxDate)
    }

    groups
  }

  def getEntityCards( startTime: String, endTime: String,
                                ip: String, userName: String, hostName: String, macAddress: String,
                               cache: FeatureServiceCache):  Map[MutableMap[String, Any],ListBuffer[MutableMap[String, Any]]]  ={
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      var sqlStr: String = "Select " + EntityThreat.MODEL_ID + " , " + EntityThreat.SECURITY_EVENT_ID + ", " +
        " MAX( " + EntityThreat.RISK_SCORE + " ) AS RISK " + " from " + EntityThreat.getName(conf) +
      " WHERE " + EntityThreat.DATE_TIME + " >= ? " + " AND " + EntityThreat.DATE_TIME + " < ?"

      //TODO replacing for NULL_VALUE with '' is a hack which should be removed
      //=====
      //NOTE: the IP address query had AND NOT(IP_ADDRESS) for working around the query timeout. I am replacing that workaround
      sqlStr = MiscUtils.getQueryFragmmentReplaceNullValue(ip, EntityThreat.IP_ADDRESS, sqlStr, conf)
      sqlStr = MiscUtils.getQueryFragmmentReplaceNullValue(userName, EntityThreat.USER_NAME, sqlStr, conf)
      sqlStr = MiscUtils.getQueryFragmmentReplaceNullValue(hostName, EntityThreat.HOST_NAME, sqlStr, conf)
      sqlStr = MiscUtils.getQueryFragmmentReplaceNullValue(macAddress, EntityThreat.MAC_ADDRESS, sqlStr, conf)
      //===

      sqlStr = sqlStr + " GROUP BY " + EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      var i = 3
      if(ip != null && ip.nonEmpty){
        pstmt.setString(i, ip)
        i = i + 1
      }
      if(userName != null && userName.nonEmpty){
        pstmt.setString(i, userName)
        i = i + 1
      }
      if(hostName != null && hostName.nonEmpty){
        pstmt.setString(i, hostName)
        i = i + 1
      }
      if(macAddress != null && macAddress.nonEmpty){
        pstmt.setString(i, macAddress)
        i = i + 1
      }
      val entityEventInfoMap: mutable.Map[EntityEventInfo, RiskScoreHelper] = mutable.Map[EntityEventInfo, RiskScoreHelper]()
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      rs = executeQuery(pstmt)
      while (rs.next()) {
        val modelId = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val riskScore = rs.getDouble("RISK")
        // Check for suppression
        val securityEventData : SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId,modelId)
        val shouldSuppress = if (securityEventData == null) true
          else suppressionDao.shouldSuppress(suppressionList, ip, hostName, userName, securityEventData.getEventType, false)

        if (shouldSuppress) {
          suppressionCount += 1
        } else {
          if (Constants.BeaconModelTuple._1.equals(modelId)) {
            if (conf.includeBeaconingBehaviors()) {
              val beaconsDao: BeaconsDao = new BeaconsDao(conf)
              beaconsDao.getEntityCardsForBeacons(startTime, endTime, ip, userName, cache, buf, riskScore)
            }
          } else if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId) || Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
            val entityAnomalyDetailsDao: EntityAnomalyDetailsDao = new EntityAnomalyDetailsDao(conf)
            entityAnomalyDetailsDao.getEntityCards(startTime, endTime, ip, userName, securityEventId, modelId, riskScore, cache, buf)
          } else if (Constants.TaniumModelTuple._1.equals(modelId) || Constants.EndpointLocalModelTuple._1.equals(modelId) || Constants.EndpointGlobalModelTuple._1.equals(modelId)) {
            val taniumDao: TaniumStatsDao = new TaniumStatsDao(conf)
            taniumDao.getTaniumEntityCards(startTime, endTime, hostName, "", useEntityId = false, riskScore, modelId, cache, buf)
          } else if (Constants.AdNoveltyDetectorTuple._1.equals(modelId)) {
            val noveltyDetectorDao = new NoveltyDetectorDao(conf)
            noveltyDetectorDao.getNoveltyDetectorEntityCards(startTime, endTime, ip, userName, securityEventId, modelId, riskScore, cache, buf)
          } else if (Constants.C2ModelTuple._1.equals(modelId)) {
            if (conf.includeC2Behaviors()) {
              val c2ModelDao = new C2ModelDao(conf)
              c2ModelDao.getEntityCardForC2(startTime, endTime, ip, userName, securityEventId, modelId, riskScore, cache, buf)
            }
          }
        }
      }

    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Cards for Entity => " + ex.getMessage, ex)
    } finally {
      closeConnections(conn)
    }

    val groups: Map[collection.mutable.Map[String, Any], ListBuffer[mutable.Map[String, Any]]] =
      buf.groupBy(x => collection.mutable.Map[String, Any](
      "modelId" -> x.get("modelId"),
      "killchainId" -> x.get("killchainId"),
      "cardId" -> x.get("cardId"),
      "eventId" -> x.get("eventId"),
      "featureDesc" -> x.get("featureDesc"),
      "securityEventType" -> x.get("securityEventType"),
      "riskScore" -> x.get("riskScore")
    )
    )

    groups.foreach { x =>
      var minDate: String = ""
      var maxDate: String = ""
      x._2.foreach { y =>
        val dateString: String = y.getOrElse("dateTime", "").toString
        if (minDate.equals("") || minDate.compareTo(dateString) > 0) {
          minDate = dateString
        }
        if (maxDate.equals("") || maxDate.compareTo(dateString) < 0) {
          maxDate = dateString
        }
      }
      x._1.put("minDate", minDate)
      x._1.put("maxDate", maxDate)
    }

    groups
  }

  def getValue(some : Any) = {
    val result = some match {
      case Some(value: java.lang.Integer) => value
      case Some(value: Int) => value
      case Some(value: Double) => value
      case Some(value: java.lang.String) => value
      case _ => null
    }

    result
  }

  /**
   * Queries the auot-complete cache to return the results
   *
   * @param incomingString  String
   * @param fieldName String
   * @param autoCompleteCache AutoCompleteCache
   * @return
   */
  def getAutoCompleteResults(incomingString: String, fieldName: String, autoCompleteCache: AutoCompleteCache): util.List[String] = {
    autoCompleteCache.get(incomingString, fieldName)

  }

  /**
   * Queries the auto-complete cache to return the results containing list of ip addresses and/or user names and/or host names
   *
   * @param incomingString  String
   * @param autoCompleteCache AutoCompleteCache
   * @return selectionMap MutableMap
   */
  def getAutoCompleteResultsOnAll(incomingString: String, autoCompleteCache: AutoCompleteCache): java.util.Map[String, util.List[String]] = {
    autoCompleteCache.get(incomingString)
  }


  /**
   * queries detector home and  gets all distinct values for a given column
   *
   * @param fieldName String columnName
   *
   * @return  List of distinct values for the given columnName
   */
  def getDistinct(fieldName: String) = {
    val list: java.util.List[String] = new java.util.LinkedList[String]()

    var column = ""
    if (fieldName.equalsIgnoreCase("hostName")) {
      column = EntityThreat.HOST_NAME.toString
    }else if (fieldName.equalsIgnoreCase("userName")) {
      column = EntityThreat.USER_NAME.toString
    } else if (fieldName.equalsIgnoreCase("sourceIp")) {
      column = EntityThreat.IP_ADDRESS.toString
    }

    if(!column.isEmpty) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)
        val sqlStr = "select DISTINCT " + column + " from  " + EntityThreat.getName(conf)
        pstmt = getPreparedStatement(conn, sqlStr)
        rs = executeQuery(pstmt)
        while (rs.next()) {
          //TODO replacing for ' ' with '' is a hack which should be removed
          list.add(MiscUtils.stringReplaceNullValue(rs.getString(column), conf))
        }
      } catch {
        case ex: Exception => Logger.error("Failed to get Distinct " + fieldName +
          " from table: " + EntityThreat.getName(conf) + " => " + ex.getMessage, ex)
      } finally {
        closeConnections(conn)
      }
    }
    list
  }


  /**
   *
   * @param sourceIp
   * @param modelId
   * @param securityEventId
   * @param killchainId
   * @param startTime
   * @param endTime
   * @return
   */
  def getRiskScoreForEntity(sourceIp: String, destinationIp: String, sourceUserName: String, destinationUserName: String, hostName: String, modelId: Int,
  securityEventId: Int, killchainId: Int, startTime: String, endTime: String): Double = {
    var risk: Double = 0

    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)

      var sql = "select RISK_SCORE from " + EntityThreat.getName(conf) +
        " where " +
        EntityThreat.DATE_TIME + " >= ?"   + " AND " +
        EntityThreat.DATE_TIME + " < ? "  + " AND " +
        EntityThreat.MODEL_ID  + " = ? " + " AND " +
      EntityThreat.SECURITY_EVENT_ID + " = ? "

      //TODO replacing for ' ' with '' is a hack which should be removed
      val (unSql, sourceUserPresent) = MiscUtils.getQueryFragmmentReplaceNullValueExists(destinationUserName, EntityThreat.USER_NAME, sql, conf)
      sql = sql + unSql
      val (ipSql, ipPresent) = MiscUtils.getQueryFragmmentReplaceNullValueExists(destinationIp, EntityThreat.IP_ADDRESS, sql, conf)
      sql = sql + ipSql
      val (hnSql, hostNamePresent) = MiscUtils.getQueryFragmmentReplaceNullValueExists(hostName, EntityThreat.HOST_NAME, sql, conf)
      sql = sql + hnSql

      sql = sql +  " ORDER BY RISK_SCORE DESC " + " LIMIT 1"
      pstmt = getPreparedStatement(conn, sql)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setInt(3, modelId)
      pstmt.setInt(4, securityEventId)
      var i = 5
      if(sourceUserPresent){
        pstmt.setString(i, destinationUserName)
        i = i + 1
      }
      if(ipPresent){
        pstmt.setString(i, sourceIp)
        i = i + 1
      }
      if(hostNamePresent){
        pstmt.setString(i, hostName)
        i = i + 1
      }
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        risk = rs.getDouble(EntityThreat.RISK_SCORE.toString)
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Anomaly Details => " + ex)
    } finally {
      closeConnections(conn)
    }
      risk
  }

  /**
   * Queries ThreatView table, returns the median of risk-scores per day sorted on time.
   * @param startTime String specifying start-time
   * @param endTime String specifying end-time
   * @return
   */
  def getMedianRiskScoreBasedTimeSeries(startTime: String, endTime: String) ={
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)

      val medianScoreString = "MEDIAN_SCORE"
      val dayString = "DAY"

      /** Sql String:
      -------------
      SELECT DAY, PERCENTILE_CONT(0.5)  WITHIN GROUP ( ORDER BY RISK_SCORE DESC ) AS MEDIAN_SCORE FROM
      (
        SELECT SUBSTR(DATE_TIME,0,10) as DAY , DATE_TIME , HOST_NAME , IP_ADDRESS , USER_NAME , MODEL_ID , SECURITY_EVENT_ID , RISK_SCORE
        FROM THREAT_VIEW WHERE DATE_TIME >= '2015-06-16T00:00:00.000Z'  AND DATE_TIME< '2015-07-16T00:00:00.000Z'
      )
      GROUP BY DAY  ORDER BY MEDIAN_SCORE
       -------------
      1. We use SUBSTR(DATE_TIME,0,10) -> an example of our date time is '2015-07-10T00:00:00.000Z'.
      Using the SUBSTR() function, we extract the YYYY-MM-DD part of the date_time so that we can get the RISK_SCORE for DAY
      2. We want to compute the Median risk-score per day. Computing 50 percentile is same as Median.
       Below, PERCENTILE_CONT(0.5) implies 50 percentile.
        **/

      val innerSql  = "  SELECT SUBSTR(DATE_TIME,0,10) AS " + dayString +" , " +  EntityThreat.RISK_SCORE +
        "  FROM " + EntityThreat.getName(conf) +  " WHERE " +
        EntityThreat.DATE_TIME + " >= ? " + " AND " + EntityThreat.DATE_TIME + "< ? "

      val sqlStrBuilder: StringBuilder = new StringBuilder()
      sqlStrBuilder.append(" SELECT ").append( dayString ).append(" AS ").append(dayString).append(", ").
        append(" PERCENTILE_CONT(0.5)  WITHIN GROUP ( ORDER BY ").append( EntityThreat.RISK_SCORE ).append( " DESC ) AS ").append( medianScoreString  )
        .append(" FROM ")
        .append(" ( ").append( innerSql ).append( " ) " )
         .append(" GROUP BY ").append(dayString).append(" ORDER BY ").append(dayString)

      pstmt = getPreparedStatement(conn, sqlStrBuilder.toString())
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val dateTime: String = rs.getString(dayString)
        val medianScore = rs.getDouble(medianScoreString)
        val selectionMap = MutableMap[String, Any]()
        selectionMap += "dateTime" -> dateTime
        selectionMap += "medianRiskScore" -> medianScore
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_7 => " + ex, ex)
    } finally {
      closeConnections(conn)
    }
    buf

  }

  /**
   * Queries ThreatView table for given QueryJson, returns the risk-scores sorted on time.
   * @param input
   * @param cache
   * @return
   */
  def getRiskScoreBasedTimeSeriesForEntity(input: QueryJson, cache: FeatureServiceCache) ={
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    //Tracking Date (YMD) : Map of tuple (Date, IpAddress) to risk Details (risk score, modelscores)
    val dateToScoresMap: MutableMap[(String, String), MutableMap[String, Any]] = MutableMap[(String, String), MutableMap[String, Any]]()

    try {
      conn = getConnection(conf)
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      //used to set params for PreparedStatement
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      var sqlStr: String = "Select " + EntityThreat.DATE_TIME + ", "  + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " + EntityThreat.HOST_NAME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", " + "MAX(" + EntityThreat.RISK_SCORE + ") AS RISK" +
        " from " + EntityThreat.getName(conf)
      val aliasMap = MutableMap[String, Any]()
      aliasMap += EntityThreat.RISK_SCORE.toString -> "RISK"

      val groupbyString =  " GROUP BY " + EntityThreat.DATE_TIME + ", " +  EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " + EntityThreat.HOST_NAME + ", " +
        EntityThreat.MODEL_ID  + ", " + EntityThreat.SECURITY_EVENT_ID

      //get predicate part of the query
      val predicateString = getSqlPredicateString(input, pstmtList, groupbyString, null, null, aliasMap, cache, false)
      sqlStr = sqlStr + " " + predicateString
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val dateTime: String = rs.getString(EntityThreat.DATE_TIME.toString)
        val dateTimeYMD: String = MiscUtils.getYMDSeparatedString(dateTime)
        //TODO replacing for ' ' with '' is a hack which should be removed
        var sourceIp: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        if (sourceIp == null) {
          sourceIp = ""
        }
        //TODO replacing for ' ' with '' is a hack which should be removed
        val userName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val hostName: String =  MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val riskScore: Double = rs.getDouble(aliasMap(EntityThreat.RISK_SCORE.toString).toString)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val securityEventData : SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId,modelId)
        val shouldSuppress = if (securityEventData == null) true
        else suppressionDao.shouldSuppress(suppressionList, sourceIp, hostName, userName, securityEventData.getEventType, false)

        if (shouldSuppress) {
          suppressionCount += 1
        } else {
          val eventTypePrefix: String = securityEventData.getTypePrefix
          val eventType: String = securityEventData.getEventType
          val eventDescription: String = securityEventData.getEventDescription
          val shortDescription: String = securityEventData.getShortDescription
          val killchainId: Int = securityEventData.getKillchainId
          val featureLabel: String = securityEventData.getFeatureLabel
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId

          var selectionMap: MutableMap[String, Any] = null
          //if Ip already exists in the result buf
          if (dateToScoresMap.contains((dateTimeYMD, sourceIp))) {
            selectionMap = dateToScoresMap((dateTimeYMD, sourceIp))
          }
          else {
            selectionMap = MutableMap[String, Any]()
            dateToScoresMap += ((dateTimeYMD, sourceIp) -> selectionMap)
            buf += selectionMap
          }

          //find current score
          val currentScore: Double = {
            if (selectionMap.contains("currentScore"))
              Math.max(selectionMap("currentScore").asInstanceOf[Double], riskScore)
            else
              riskScore
          }

          val eventInfo: EntityModelInfo = new EntityModelInfo(securityEventId, killchainId, featureLabel,
            eventTypePrefix, eventType, eventDescription, shortDescription, modelId, cardId, riskScore, dateTime)

          //killchain info list
          val list: ListBuffer[EntityModelInfo] = {
            if (selectionMap.contains("modelScores"))
              selectionMap("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
            else
              ListBuffer[EntityModelInfo]()
          }
          list += eventInfo

          //populate result map
          selectionMap += "dateTime" -> dateTimeYMD
          selectionMap += "currentScore" -> currentScore
          selectionMap += "modelScores" -> list
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Entity Scores_8 => " + ex, ex)
    } finally {
      closeConnections(conn)
    }
    scala.collection.immutable.TreeMap(dateToScoresMap.toSeq.sortBy(_._1):_*).values.toList
  }


  /**
   * Queries the ENTITY_THREAT Table and computes Top N securityEvents (with respect to the number of events for a given securityEventId)
   * @param startTime
   * @param endTime
   * @param n
   * @param cache
   * @return
   */
  def getTopNSecurityEvents(startTime: String, endTime: String, n: Int, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      val eventTypeMap = MutableMap[String, MutableMap[String, Any]]()
      conn = getConnection(conf)
      val sqlStr = " SELECT " + EntityThreat.MODEL_ID + ", " +
        EntityThreat.SECURITY_EVENT_ID  + ", "  + " COUNT(1) AS COUNT " +
        " FROM " + EntityThreat.getName(conf) +
        " WHERE " + EntityThreat.DATE_TIME + " >= ? "  + " AND " +  EntityThreat.DATE_TIME + " < ? " +
        " GROUP BY  " + EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + " ORDER BY COUNT DESC "

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val count = rs.getInt("COUNT")
        val modelId = rs.getInt(EntityThreat.MODEL_ID.toString)
        val securityEventId = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val securityEventData =  cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)

        // We can only suppress on the behavior (i.e. the event type), so ip, host and user will just be ""
        val shouldSuppress = if (securityEventData == null) true
        else suppressionDao.shouldSuppress(suppressionList, "", "", "", securityEventData.getEventType, false)

        if (shouldSuppress) {
          suppressionCount += 1
        } else {
          var selectionMap = MutableMap[String, Any]()
          val eventType = securityEventData.getEventType
          val eventDescription = securityEventData.getEventDescription
          if(eventTypeMap.contains(eventType)){
            selectionMap = eventTypeMap(eventType)
            selectionMap += "count" -> (selectionMap("count").asInstanceOf[Int] + count)
          }else{
            if(eventTypeMap.size < n) {
              selectionMap += "eventType" -> eventType
              selectionMap += "count" -> count
              eventTypeMap += eventType -> selectionMap
              buf += selectionMap
            }
          }
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get topN security events => " + ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }


}

case class EntityEventInfo(sourceIp: String, destinationIp: String, sourceUserName: String, destinationUserName: String, modelId: Int,
securityEventId: Int, killchainId: Int) {
}

case class RiskScoreHelper(var riskScore: Double){

}

case class EntityUniqueness(sourceIp: String, userName: String, hostName: String, macAddress: String){


  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result =  prime * result + {if(sourceIp == null) 0 else sourceIp.hashCode}
    result =  prime * result + {if(userName == null) 0 else userName.hashCode}
    result =  prime * result + {if(hostName == null) 0 else hostName.hashCode}
    result =  prime * result + {if(macAddress == null) 0 else macAddress.hashCode}

    result

  }

  override def canEqual(obj: Any): Boolean = {
    if(obj == null || obj.getClass != this.getClass){
      return false
    }
    val entityObj: EntityUniqueness = obj.asInstanceOf[EntityUniqueness]
    val result = (sourceIp == entityObj.sourceIp || (sourceIp != null && sourceIp.equals(entityObj.sourceIp))) &&
      (userName == entityObj.userName || (userName != null && userName.equals(entityObj.userName))) &&
        (hostName == entityObj.hostName || (hostName != null && hostName.equals(entityObj.hostName))) &&
      (macAddress == entityObj.macAddress || (macAddress != null && macAddress.equals(entityObj.macAddress)))
    result
  }
}


case class EntitySecurityEventModelDateTimeUniqueness(entityUniqueness: EntityUniqueness,
                                                      modelId: Int, securityEventId: Int,  dateTime: String) {
  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result =  prime * result + {if(entityUniqueness == null) 0 else entityUniqueness.hashCode()}
    result =  prime * result + {if(modelId == null) 0 else modelId.hashCode}
    result =  prime * result + {if(securityEventId == null) 0 else securityEventId.hashCode}
    result =  prime * result + {if(dateTime == null) 0 else dateTime.hashCode}

    result
  }

  override def canEqual(obj: Any): Boolean = {
    if(obj == null || obj.getClass != this.getClass){
      return false
    }
    val entityObj: EntitySecurityEventModelDateTimeUniqueness = obj.asInstanceOf[EntitySecurityEventModelDateTimeUniqueness]
    if (!entityUniqueness.canEqual(entityObj.entityUniqueness)) {
      return false
    }
    val result = modelId == entityObj.modelId &&
      securityEventId == entityObj.securityEventId &&
      (dateTime == entityObj.dateTime || (dateTime != null && dateTime.equals(entityObj.dateTime)))
    result
  }
}


object DetectorHomeDao {}
