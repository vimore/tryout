package com.securityx.modelfeature.dao

import java.sql.{ResultSet, PreparedStatement, Connection}
import java.util

import com.e8.sparkle.storage.hbase.query.QueryJson
import com.e8.sparkle.storage.hbase.rowobjects._
import com.e8.sparkle.storage.hbase.tableobjects._
import com.securityx.modelfeature.config.{EntityFusionConfiguration, SecurityEventTypeConfiguration, FeatureServiceConfiguration}
import com.securityx.modelfeature.dao.impala.HostInfoMefDao
import com.securityx.modelfeature.queryengine.QueryGenerator
import com.securityx.modelfeature.{FeatureServiceCache, utils}
import com.securityx.modelfeature.utils.{MiscUtils, EntityThreat, HBaseAccessConfiguration, EntHostProperties}
import org.apache.hadoop.hbase.client.Table
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable
import scala.collection.mutable.{Map => MutableMap, ListBuffer}
import scala.collection.JavaConversions._

/**
 * Created by harish on 7/17/15.
 */
class EntityInvestigatorDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[EntityInvestigatorDao])

  private val hostEntityProperties: HostEntityPropertiesDao = new HostEntityPropertiesDao(conf)
  private val userEntityProperties: UserEntityPropertiesDao = new UserEntityPropertiesDao(conf)
  private val suppressionDao: SuppressionDao = new SuppressionDao(conf)
  private val detectorHomeDao: DetectorHomeDao = new DetectorHomeDao(conf)
  private val hBaseConfig = new HBaseAccessConfiguration(conf)

  def getEntityProperties(entityIp: String, entityHostName: String, entityUserName: String, startTime: String, endTime: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, ListBuffer[MutableMap[String, Any]]]]
    val selectionMap = MutableMap[String,collection.mutable.ListBuffer[MutableMap[String, Any]]]()
    if (!suppressionDao.shouldSuppressNoBehaviorInfo(entityIp, entityHostName, entityUserName)) {
      if (entityIp != null && entityIp.nonEmpty) {
        //obtained from Entity Fusion: Latest known information in the last (endtime - 48 hours) to endtime
        selectionMap += "ipProperties" -> hostEntityProperties.getEntHostPropsByIp(entityIp, endTime)
      }

      if (entityHostName != null && entityHostName.nonEmpty) {
        //obtained from Entity Fusion: Latest known information in the last (endtime - 48 hours) to endtime
        selectionMap += "hostProperties" -> hostEntityProperties.getEntHostPropsByHostName(entityHostName, endTime)
      }

      if (entityUserName != null && entityUserName.nonEmpty) {
        selectionMap += "userProperties" -> userEntityProperties.getUserPropertiesForSourceName(startTime, endTime, entityUserName)
      }
    }

    buf += selectionMap
    buf
  }

  /**
   * For each entity in the entityIds list, get the threat info for all related entities
   *
   * @param startTime start of the date range to query
   * @param endTime end of the date range to query
   * @param entityIds entity for which we should get related entities
   * @param cache cache for getting config info
   */
  def getRelatedEntityList(startTime: String, endTime: String, entityIds: String, cache: FeatureServiceCache) = {
    // This implementation calls getRelatedEntities(), which results in a call to getRelatedEntityThreats(),
    // once for each entity in the list we've been passed.  This has some obvious performance impacts.
    // We may be able to optimize that by getting all the related entities for all the entities listed in
    // entityIds, and calling getRelatedEntityThreats once for all of them.  But that will require separating
    // out the response and dividing up the list returned into separate lists for the entities in entityIds,
    // which will be complex.  For the moment, I'm leaving this slightly less efficient implementation in
    // place until such a time as we decide the performance isn't good enough.
    val buf = collection.mutable.Map[String, collection.mutable.ListBuffer[MutableMap[String, Any]]]()
    val idList = entityIds.split(",")
    for (id <- idList) {
      val idTrimmed = id.trim
      val relatedEntityList = getRelatedEntities(startTime, endTime, idTrimmed, cache)
      buf += idTrimmed -> relatedEntityList
    }
    buf
  }

  /**
   * Get the set of entities that are related to a another entity. A host entity is related to the users that use that host; likewise a
   * user is related to the hosts that the user uses.
   *
   * @param startTime start of the date range to query
   * @param endTime end of the date range to query
   * @param entityId entity for which we should get related entities
   * @param cache cache for getting config info
   * @return a list of maps with information on entities that are related to the entity passed in
   */
  def getRelatedEntities(startTime: String, endTime: String, entityId: String, cache: FeatureServiceCache) = {
    var buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    // First we query the hbase access layer to get the related entities in the time range
    val erQuery = createRelatedEntityQuery(startTime, endTime, entityId)
    val entityRelationshipTable = new EntityRelationshipTable
    val hBaseConn = hBaseConfig.getConnection
    var table: Table = null
    val entityList = collection.mutable.ListBuffer.empty[String]
    try {
      table = entityRelationshipTable.getTable(hBaseConn, hBaseConfig)
      val rows = entityRelationshipTable.getRows(table, erQuery, hBaseConn, false, hBaseConfig)
      for (row <- rows) {
        row match {
          case erRow: EntityRelationship =>
            val relatedEntities = erRow.getRelatedEntities
            if (relatedEntities != null) {
              for (entity <- relatedEntities) {
                // The HBase layer may return the multiple rows with the same entity relationship, if
                // that relationship has be created more than once at different times. We only care
                // about the unique entities in this case, though.
                if (!entityList.contains(entity)) {
                  entityList += entity
                }
              }
            }
          case _ =>
            Logger.warn("getRelatedEntities: expected EntityRelationship object, got [" + row.getClass.getCanonicalName + "]")
        }
      }
    } finally {
      hBaseConfig.close(table)
    }

    // Now, take the list of related entities and get the info that the UI needs.  This is essentially the same as we would
    // get for the entity if we ran detector/entitySearch for that entity - basically, info about the entity and info about
    // the highest risk threat for that entity.  If there were no related entities, we are done.
    if (entityList.isEmpty) {
      Logger.debug("No related entities for entityId [" + entityId + "] startTime [" + startTime + "] endTime [" + endTime + "]")
    } else {
      buf = getRelatedEntityThreats(entityList, startTime, endTime, cache)
      if (buf.isEmpty) {
        val entityListString = entityList.mkString(",")
        Logger.debug("Got related entities [" + entityListString + "] but there are no threats for those entities")
      }
    }
    buf
  }

  /**
   * Get highest threat from entity_threat for each entity in entityList, during the time period given by startTime and endTime
   *
   * @param entityList list of entities to get threats for
   * @param startTime start of the period to query
   * @param endTime end of the period to query
   * @param cache cache for getting config info
   * @return
   */
  def getRelatedEntityThreats(entityList: ListBuffer[String], startTime: String, endTime: String, cache: FeatureServiceCache): ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val entityToScoresMap = collection.mutable.Map.empty[String, MutableMap[String, Any]]
    val expectedNumEntities = 10
    try {
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      conn = getConnection(conf)
      val qGen = new QueryGenerator()

      var sqlStr: String = "Select " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " +
        EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID + ", "
      sqlStr = sqlStr + EntityThreat.HOST_ENTITY_ID + ", " + EntityThreat.USER_ENTITY_ID + ", "
      sqlStr = sqlStr + "MAX(" + EntityThreat.RISK_SCORE + ") AS RISK from " + EntityThreat.getName(conf)

      val inClause = qGen.createInClause(entityList)
      val predicateClause = " WHERE " + EntityThreat.DATE_TIME + " >= ? AND " + EntityThreat.DATE_TIME + " < ? " +
        "AND (" + EntityThreat.HOST_ENTITY_ID + " IN " + inClause + " OR " + EntityThreat.USER_ENTITY_ID + " IN " + inClause + ")"

      var groupbyString = " GROUP BY " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.USER_NAME + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.MAC_ADDRESS + ", " + EntityThreat.DATE_TIME + ", " +
        EntityThreat.MODEL_ID + ", " + EntityThreat.SECURITY_EVENT_ID
      groupbyString = groupbyString + ", " + EntityThreat.HOST_ENTITY_ID + ", " + EntityThreat.USER_ENTITY_ID

      sqlStr = sqlStr + " " + predicateClause + groupbyString
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      // We need to populate twice - once for the host_entity_id and once for the user_entity_id
      qGen.populateInClause(entityList, pstmt, 2)
      qGen.populateInClause(entityList, pstmt, 2 + entityList.size)
      rs = executeQuery(pstmt)
      while (rs.next() && entityToScoresMap.size < expectedNumEntities) {
        val ipAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS.toString), conf)
        val hostName: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME.toString), conf)
        val userName: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME.toString), conf)
        val macAddress: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.MAC_ADDRESS.toString), conf)
        val hostEntityId: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_ENTITY_ID.toString), conf)
        val userEntityId: String = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_ENTITY_ID.toString), conf)
        val riskScore: Double = rs.getDouble("RISK")
        val dateTime: String = rs.getString(EntityThreat.DATE_TIME.toString)
        val modelId: Int = rs.getInt(EntityThreat.MODEL_ID.toString)
        if ((hostEntityId == null || hostEntityId.isEmpty) && (userEntityId == null || userEntityId.isEmpty)) {
          Logger.warn("got result from entity_threat where host_entity_id and user_entity_id are both null. " +
            "date_time [" + dateTime + "] ip_address [" + ipAddress + "] host_name [" + hostName + "] user_name [" + userName + "] mac_address [" + macAddress + "]")
        }
        val securityEventId: Int = rs.getInt(EntityThreat.SECURITY_EVENT_ID.toString)
        val securityEventData: SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
        val (entityId, entityType) = detectorHomeDao.getEntityIdAndType(hostEntityId, userEntityId, modelId, ipAddress, hostName, userName, macAddress, securityEventId, cache)
        val shouldSuppress = if (securityEventData == null) true
        else suppressionDao.shouldSuppress(suppressionList, ipAddress, hostName, userName, securityEventData.getEventType, false)
        if (shouldSuppress) {
          suppressionCount += 1
        } else if (entityList.contains(entityId)) {
          // We only look at threats where the entity id is in the list.  This is because we can get threats for both hosts and users, particularly
          // in cases where both the host and user entity ids have values.  But we only want threats that are specifically for the entities in
          // the passed in entityList.
          val eventType: String = securityEventData.getEventType
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId

          var selectionMap: MutableMap[String, Any] = null
          if (entityToScoresMap.contains(entityId)) {
            selectionMap = entityToScoresMap(entityId)
          } else {
            selectionMap = MutableMap[String, Any]()
            entityToScoresMap += entityId -> selectionMap
            buf += selectionMap
          }

          var currentSecurityEventId: Int = 0
          var currentModelId: Int = 0
          var currentCardId: Int = 0
          var currentDateTime: String = ""
          var currentScore: Double = 0.0
          var currentEventType: String = ""
          if (selectionMap.contains("currentScore")) {

            val prevScore: Double = selectionMap("currentScore").asInstanceOf[Double];

            // if score is updated then update currentSecurityEventId, currentModelId, currentCardId, currentDateTime
            if (riskScore > prevScore) {
              currentScore = riskScore
              currentSecurityEventId = securityEventId
              currentModelId = modelId
              currentCardId = cardId
              currentDateTime = dateTime
              currentEventType = eventType
            } else {
              currentScore = selectionMap("Score").asInstanceOf[Double]
              currentSecurityEventId = selectionMap("SecurityEventId").asInstanceOf[Int]
              currentModelId = selectionMap("ModelId").asInstanceOf[Int]
              currentCardId = selectionMap("CardId").asInstanceOf[Int]
              currentDateTime = selectionMap("DateTime").asInstanceOf[String]
              currentEventType = selectionMap("EventType").asInstanceOf[String]
            }
          } else {
            currentScore = riskScore
            currentSecurityEventId = securityEventId
            currentModelId = modelId
            currentCardId = cardId
            currentDateTime = dateTime
            currentEventType = eventType
          }

          selectionMap += "ipAddress" -> ipAddress
          selectionMap += "hostName" -> hostName
          selectionMap += "userName" -> userName
          selectionMap += "macAddress" -> macAddress
          selectionMap += "entityId" -> entityId
          selectionMap += "entityType" -> entityType
          selectionMap += "EventType" -> currentEventType
          selectionMap += "Score" -> currentScore
          selectionMap += "SecurityEventId" -> currentSecurityEventId
          selectionMap += "ModelId" -> currentModelId
          selectionMap += "CardId" -> currentCardId
          selectionMap += "DateTime" -> currentDateTime
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get Related Entities => " + ex, ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }

    buf
  }

  def createRelatedEntityQuery(startTime: String, endTime: String, entityId: String): QueryJson = {
    val query: QueryJson = new QueryJson
    query.setStartTime(startTime)
    query.setEndTime(endTime)
    query.setMaxVersions(2)
    val filterMap = new util.HashMap[java.lang.String, Object]()
    filterMap.put("field", EntityRelationship.ENTITY_FIELD)
    filterMap.put("operator", BaseTable.EQUALS)
    val values = new util.ArrayList[AnyRef]
    values.add(entityId)
    filterMap.put("values", values)
    val queryList = new util.ArrayList[util.Map[java.lang.String, Object]]()
    queryList.add(filterMap)
    query.setQuery(queryList)

    query
  }

  def createEntityQuery(startTime: String, endTime: String, entityId: String): QueryJson = {
    val query: QueryJson = new QueryJson
    query.setStartTime(startTime)
    query.setEndTime(endTime)
    query.setMaxVersions(2)
    val filterMap = new util.HashMap[java.lang.String, Object]()
    filterMap.put("field", Entity.ENTITY_ID_FIELD)
    filterMap.put("operator", BaseTable.EQUALS)
    val values = new util.ArrayList[AnyRef]
    values.add(entityId)
    filterMap.put("values", values)
    val queryList = new util.ArrayList[util.Map[java.lang.String, Object]]()
    queryList.add(filterMap)
    query.setQuery(queryList)

    query
  }

  /**
   * This method gets a list of maps containing the properties for the entity identified by the passed in IP, hostname and
   * username. The maps containing properties will always have entries for all properties an entity could have, and if
   * there is no value for the given property, it will be returned as the empty string.
   *
   * Note: At the moment this method does not resolve differences between the properties gotten using the different
   * ways to identify an entity.  More work will need to be done when the new entity fusion code is available.
   *
   * @param entityId id of the entity
   * @param startTime start time for getting the properties
   * @param endTime end time for getting the properties
   * @return
   */
  def getEntityPropertiesByEntityId(entityId: String, startTime: String, endTime: String) = {
    getEntityPropertiesByEntId(entityId, startTime, endTime, includePrimaryEntity = true)
  }

  /**
   * This method gets a list of maps containing the properties for the entity identified by the passed in IP, hostname and
   * username. The maps containing properties will always have entries for all properties an entity could have, and if
   * there is no value for the given property, it will be returned as the empty string.
   *
   * This version of getting an entity offers the caller the choice of getting the primary user/host, or not getting
   * it. That allows users to choose better performance if they don't need the primary user/host.
   *
   * @param entityId id of the entity
   * @param startTime start time for getting the properties
   * @param endTime end time for getting the properties
   * @param includePrimaryEntity if true, get the primary host or user
   * @return
   */
  def getEntityPropertiesByEntId(entityId: String, startTime: String, endTime: String, includePrimaryEntity: Boolean) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var selectionMap = MutableMap[String, Any]()

    val query: QueryJson = createEntityQuery(startTime, endTime, entityId)

    val entityTable = new EntityTable
    val hBaseConn = hBaseConfig.getConnection
    var table: Table = null
    try {
      table = entityTable.getTable(hBaseConn, hBaseConfig)
      val rows = entityTable.getRows(table, query, hBaseConn, false, hBaseConfig)

      if (rows.size() == 0) {
        Logger.warn("getEntityPropertiesByEntityId: found no entities for entityId [" + entityId + "] startTime [" + startTime + "] endTime [" + endTime + "]")
      } else {
        if (rows.size() > 1) {
          // We expect only a single row - not sure what to do if there are multiple rows, though since
          // we're returning a list, I suppose we could do that...  But for now, just log a warning and
          // return the first entity.
          Logger.warn("getEntityPropertiesByEntityId: expected one entity but got [" + rows.size() + "]. Only returning the first entity")
        }
        val entity = rows.get(0).asInstanceOf[Entity]
        selectionMap += "entityId" -> entity.getEntityId
        selectionMap += "entityType" -> entity.getEntityType
        selectionMap += "dateTime" -> entity.getFusionTimes
        getListFromPropertyList(selectionMap, "userName", entity.getUserNames)
        getListFromPropertyList(selectionMap, "hostName", entity.getHostNames)
        getListFromPropertyList(selectionMap, "ip", entity.getIpAddresses)
        getListFromPropertyList(selectionMap, "macAddress", entity.getMacAddresses)
        getListFromPropertyList(selectionMap, "browserName", entity.getBrowsers)
        getListFromPropertyList(selectionMap, "browserVersion", entity.getBrowserVersions)
        getListFromPropertyList(selectionMap, "country", entity.getCountries)
        getListFromPropertyList(selectionMap, "os", entity.getOses)
        getListFromPropertyList(selectionMap, "isCritical", entity.getIsCriticals)
        getListFromPropertyList(selectionMap, "accountType", entity.getAccountTypes)
        getListFromPropertyList(selectionMap, "accountAlias", entity.getAccountAliases)
        getListFromPropertyList(selectionMap, "canonicalName", entity.getCanonicalNames)
        getListFromPropertyList(selectionMap, "department", entity.getDepartments)
        getListFromPropertyList(selectionMap, "email", entity.getEmails)
        getListFromPropertyList(selectionMap, "jobTitle", entity.getJobTitles)
        getListFromPropertyList(selectionMap, "location", entity.getLocations)
        getListFromPropertyList(selectionMap, "manager", entity.getManagers)
        getListFromPropertyList(selectionMap, "securityId", entity.getSecurityIds)
        getListFromPropertyList(selectionMap, "creationDate", entity.getUserCreationDates)
        getListWithFirstValueFromPropertyList(selectionMap, "lastLogonDate", entity.getUserLastLogonDates)
        getListFromPropertyList(selectionMap, "lastModificationDate", entity.getUserLastModDates)
        getListFromPropertyList(selectionMap, "passwordLastSetDate", entity.getUserLastPwdSetDates)
        //      selectionMap += "panUserId" -> ""
        if (includePrimaryEntity) {
          if (entity.getEntityType == "host") {
            // get primary user
            val primaryUser = getPrimaryHostOrUser(entityId, isHost = false, startTime, endTime, hBaseConn)
            if (primaryUser != null && primaryUser.nonEmpty) {
              selectionMap += "primaryUser" -> primaryUser
            }
          } else if (entity.getEntityType == "user") {
            // get primary host
            val primaryHost = getPrimaryHostOrUser(entityId, isHost = true, startTime, endTime, hBaseConn)
            if (primaryHost != null && primaryHost.nonEmpty) {
              selectionMap += "primaryHost" -> primaryHost
            }
          } else {
            Logger.warn("Got entity that is neither a host nor a user entity [" + entityId + "]")
          }
        }
      }
    } finally {
      hBaseConfig.close(table)
    }

    buf += selectionMap
    buf
  }

  def getPrimaryHostOrUser(entityId: String, isHost: Boolean, startTime: String, endTime: String, hBaseConn: org.apache.hadoop.hbase.client.Connection): String = {
    var primaryHostOrUser = ""

    // Create a query
    val query = createRelatedEntityQuery(startTime, endTime, entityId)

    val entityRelationshipTable = new EntityRelationshipTable
    var erTable: Table = null
    val entityTable = new EntityTable
    var eTable: Table = null
    // We use entityFrequencyMap to figure out which entity shows up more often - that is our
    // current definition for primary
    val entityFrequencyMap: mutable.Map[String, Int] = mutable.Map[String, Int]()
    var maxTimes = 0
    try {
      var maxEntity = ""
      erTable = entityRelationshipTable.getTable(hBaseConn, hBaseConfig)
      val rows = entityRelationshipTable.getRows(erTable, query, hBaseConn, false, hBaseConfig)
      for (row <- rows) {
        val erRow = row.asInstanceOf[EntityRelationship]
        for (entity <- erRow.getRelatedEntities) {
          if (entityFrequencyMap.contains(entity)) {
            entityFrequencyMap(entity) + 1
          } else {
            entityFrequencyMap += entity -> 1
          }
          if (entityFrequencyMap(entity) > maxTimes) {
            maxTimes = entityFrequencyMap(entity)
            maxEntity = entity
          }
        }
      }

      if (maxEntity.nonEmpty) {
        // If we got an entity id, we need to look it up and get the host or user name out of it.
        val entityQuery = createEntityQuery(startTime, endTime, maxEntity)

        eTable = entityTable.getTable(hBaseConn, hBaseConfig)
        val rows = entityTable.getRows(eTable, entityQuery, hBaseConn, false, hBaseConfig)
        if (rows.nonEmpty) {
          val eRow = rows.head.asInstanceOf[Entity]
          if (isHost) {
            primaryHostOrUser = eRow.getHostName
          } else {
            primaryHostOrUser = eRow.getUserName
          }
        }
      }
    } finally {
      hBaseConfig.close(eTable)
      hBaseConfig.close(erTable)
    }

    primaryHostOrUser
  }

  def getListWithFirstValueFromPropertyList(selectionMap: MutableMap[String,Any], key: String, properties: util.List[ColumnProperties]) = {
    // Note that if the properties list is null or empty, we will not add anything to the selectionMap.  This is intentional,
    // the map should contain no entry for such properties.
    if (properties != null && ! properties.isEmpty) {
      val result = collection.mutable.ListBuffer.empty[MutableMap[String, String]]
      // Only get the first non-dummy result
      var foundResult = false
      for (property <- properties) {
        if (property.getSource != "DUMMY" && !foundResult) {
          val prop = MutableMap[String, String]()
          prop += "value" -> property.getValue
          prop += "source" -> property.getSource
          prop += "timestamp" -> property.getTimestampString
          result += prop
          foundResult = true
        }
      }

      selectionMap += key -> result
    }
  }

  def getListFromPropertyList(selectionMap: MutableMap[String,Any], key: String, properties: util.List[ColumnProperties]) = {
    // Note that if the properties list is null or empty, we will not add anything to the selectionMap.  This is intentional,
    // the map should contain no entry for such properties.
    if (properties != null && ! properties.isEmpty) {
      val result = collection.mutable.ListBuffer.empty[MutableMap[String, String]]
      for (property <- properties) {
        if (property.getSource != "DUMMY") {
          val prop = MutableMap[String, String]()
          prop += "value" -> property.getValue
          prop += "source" -> property.getSource
          prop += "timestamp" -> property.getTimestampString
          result += prop
        }
      }

      selectionMap += key -> result
    }
  }

  /**
   * This method takes a list of maps of column properties, such as those constructed by getListFromPropertyList(), and
   * returns a flat list of the values of the properties.
   *
   * @param list the list to flatten
   * @return a list of strings containing the values from the passed in list
   */
  def getFlattenedList(list: ListBuffer[MutableMap[String, String]]): ListBuffer[String] = {
    val result = collection.mutable.ListBuffer.empty[String]
    for (map <- list) {
      result += map.getOrElse("value", "")
    }
    result
  }

  /**
   * This method gets a list of maps containing the properties for the entity identified by the passed in IP, hostname and
   * username. The maps containing properties will always have entries for all properties an entity could have, and if
   * there is no value for the given property, it will be returned as the empty string.
   *
   * Note: At the moment this method does not resolve differences between the properties gotten using the different
   * ways to identify an entity.  More work will need to be done when the new entity fusion code is available.
   *
   * @param entityIp IP of the entity
   * @param entityHostName Hostname of the entity
   * @param entityUserName Username of the entity
   * @param startTime start time for getting the properties
   * @param endTime end time for getting the properties
   * @return
   */
  def getEntityPropertiesSingleView(entityIp: String, entityHostName: String, entityUserName: String, startTime: String, endTime: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    // First init the selection map so all fields exist.
    val selectionMap = MutableMap[String,Any]()
    selectionMap += "userName" -> ""
    selectionMap += "hostName" -> ""
    selectionMap += "macAddress" -> ""
    selectionMap += "ip" -> ""
    selectionMap += "browserName" -> ""
    selectionMap += "browserVersion" -> ""
    selectionMap += "country" -> ""
    selectionMap += "city" -> ""
    selectionMap += "os" -> ""
    selectionMap += "primaryUserId" -> ""
    selectionMap += "fullName" -> ""
    selectionMap += "isCritical" -> ""
    selectionMap += "accountType" -> ""
    selectionMap += "canonicalName" -> ""
    selectionMap += "fullName" -> ""
    selectionMap += "department" -> ""
    selectionMap += "email" -> ""
    selectionMap += "jobTitle" -> ""
    selectionMap += "location" -> ""
    selectionMap += "manager" -> ""
    selectionMap += "primaryHost" -> ""
    selectionMap += "primaryUserId" -> ""
    selectionMap += "securityId" -> ""
    selectionMap += "dateTime" -> ""
    selectionMap += "creationDate" -> ""
    selectionMap += "lastLogonDate" -> ""
    selectionMap += "lastModificationDate" -> ""
    selectionMap += "passwordLastSetDate" -> ""
    selectionMap += "risk" -> ""
    selectionMap += "panUserId" -> ""

    if (!suppressionDao.shouldSuppressNoBehaviorInfo(entityIp, entityHostName, entityUserName)) {
      if (entityIp != null && entityIp.nonEmpty) {
        //obtained from Entity Fusion: Latest known information in the last (endtime - 48 hours) to endtime
        val entityMapList = hostEntityProperties.getEntHostPropsByIp(entityIp, endTime)
        selectionMap ++= entityMapList.head
      }

      if (entityHostName != null && entityHostName.nonEmpty) {
        //obtained from Entity Fusion: Latest known information in the last (endtime - 48 hours) to endtime
        val hostMapList = hostEntityProperties.getEntHostPropsByHostName(entityHostName, endTime)
        selectionMap ++= hostMapList.head
      }

      if (entityUserName != null && entityUserName.nonEmpty) {
        val userMapList = userEntityProperties.getUserPropertiesForSourceName(startTime, endTime, entityUserName)
        selectionMap ++= userMapList.head
      }
    }
    buf += selectionMap
    buf
  }


  /**
   * Endpoint entity properties
   * @param hostName
   * @param md5
   * @param processName
   * @param startTime
   * @param endTime
   * @return
   */
  def getEndpointEntityProperties(hostName: String, md5: String, processName: String, startTime: String, endTime: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, ListBuffer[MutableMap[String, Any]]]]
    val selectionMap = MutableMap[String,collection.mutable.ListBuffer[MutableMap[String, Any]]]()
    if (hostName != null && hostName.nonEmpty) {
      //obtained from Entity Fusion: Latest known information in the last (endtime - 48 hours) to endtime
      selectionMap += "hostProperties" -> hostEntityProperties.getEntHostPropsByHostName(hostName, endTime)
    }

    val taniumStatsDao: TaniumStatsDao = new TaniumStatsDao(conf)
    selectionMap += "riskScoreProperties" -> taniumStatsDao.getRiskScoreForHost(startTime, endTime, hostName)

    val hostInfoMefDao: HostInfoMefDao = new HostInfoMefDao(conf)
    selectionMap += "processProperties" -> hostInfoMefDao.getProcessPropertiesForHost(hostName, md5, processName, startTime, endTime)

    buf += selectionMap
    buf

  }

  /**
   * Get the endpoint entity properties for a given entity, md5, process name and date range
   *
   * @param entityId entity id for which to get properties
   * @param md5 md5 to use in getting process properties
   * @param processName process name to use in getting process properties
   * @param startTime start time of the date range
   * @param endTime end time of the date range
   * @return a list holding a map with three sub-maps, one each for the host (i.e. entity) properties, the endpoint risk
   *         scores, and the entity process properties
   */
  def getEndpointEntityPropertiesByEntityId(entityId: String, md5: String, processName: String, startTime: String, endTime: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, ListBuffer[MutableMap[String, Any]]]]
    val selectionMap = MutableMap[String,collection.mutable.ListBuffer[MutableMap[String, Any]]]()
    if (entityId != null && entityId.nonEmpty) {
      val hostProps = getEntityPropertiesByEntityId(entityId, startTime, endTime)
      selectionMap += "hostProperties" -> hostProps
      if (hostProps.nonEmpty) {
        // Get hostnames from entity properties
        val hostnames = collection.mutable.ListBuffer.empty[String]
        for (hostProp <- hostProps) {
          val hostNamesProp = hostProp.getOrElse("hostName", collection.mutable.ListBuffer.empty).asInstanceOf[collection.mutable.ListBuffer[MutableMap[String, String]]]
          for (hostPropMap <- hostNamesProp) {
            hostnames += hostPropMap.get("value").get
          }

          //val result = collection.mutable.ListBuffer.empty[MutableMap[String, String]]
        }

        // Now get the process properties for each host
        val hostInfoMefDao: HostInfoMefDao = new HostInfoMefDao(conf)
        val processPropList: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
        for (hName <- hostnames) {
          processPropList ++= hostInfoMefDao.getProcessPropertiesForHost(hName, md5, processName, startTime, endTime)
        }
        selectionMap += "processProperties" -> processPropList
      }
    }

    val taniumStatsDao: TaniumStatsDao = new TaniumStatsDao(conf)
    selectionMap += "riskScoreProperties" -> taniumStatsDao.getRiskScoreForEntity(startTime, endTime, entityId)

    buf += selectionMap
    buf
  }


  /**
    *
    * @param entityIp
    * @param entityHostName
    * @param entityUserName
    * @param startTime
    * @param endTime
    * @param behaviorTypes
    * @return
    */
  def getEntityCustomBehaviors(entityIp: String, entityHostName: String, entityUserName: String, startTime: String, endTime: String, behaviorTypes: String) = {
    val customBehaviorDao: CustomBehaviorDao = new CustomBehaviorDao(conf)
    //Currently, custom behavior properties is only limited to IP Address.
    customBehaviorDao.getCustomBehaviorsForIp(entityIp, startTime, endTime, behaviorTypes)
  }

}
