package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.utils.{Constants, PeerGroup}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap, ListBuffer}

class PeerGroupDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[PeerGroupDao])
  private val suppressionDao: SuppressionDao = new SuppressionDao(conf)

  /**
   * Queries the Peer_Group table for a given start time, end time and Peer Id,and returns all information related to that
   * peer Id.
   * The info includes KillchainId, security event Id, security event Description.
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results]
   * @return topN ListBuffer.empty[MutableMap[String, Any] which contains all information for peer group.
   */
  def getPeerGroupAnomalies(startTimeStr: String, endTimeStr: String, topN: Integer, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = PeerGroup.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList;
      var suppressionCount = 0
      var returnCount = 0

      val sqlStr = "SELECT " + PeerGroup.columns + " FROM " + tableName +
        " WHERE DATE_TIME >= ? and DATE_TIME < ? ORDER BY ANOMALY_SCORE DESC limit ?"
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      // We get twice as many results as asked for, since some may be suppressed
      pstmt.setInt(3, topN * 2)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next() || returnCount < topN) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        //adding killchain ID
        try {
          val modelId: Int = rs.getInt(PeerGroup.PEER_TYPE.toString)
          val modelName: String = cache.getModelNameFromId(modelId)
          val securityEventId: Int = getFeatureIdFromPeerTopFeatures(rs.getString(PeerGroup.PEER_TOP_FEATURES.toString))

          selectionMap += "securityEventId" -> securityEventId
          selectionMap += "featureId" -> securityEventId
          selectionMap += "featureAnomalyScore" -> rs.getDouble(PeerGroup.ANOMALY_SCORE.toString)
          selectionMap += "peerFeatures" -> rs.getString(PeerGroup.PEER_TOP_FEATURES.toString)
          selectionMap += "numberOfEntities" -> rs.getInt(PeerGroup.PEER_TOTAL.toString)

          val securityEventData = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
          val peerUsers = rs.getString(PeerGroup.ENTITY_ID.toString)
          // the peerUsers value can be either a string containing an ip, or a string containing a userName, so we will pass it for
          // all three of ip, host and user when checking for suppression.
          val shouldSuppress = if (securityEventData == null) true
          else suppressionDao.shouldSuppress(suppressionList, peerUsers, peerUsers, peerUsers, securityEventData.getEventType, true)

          if (shouldSuppress) {
            suppressionCount += 1
          } else {
            Logger.debug("model id is " + modelId)
            Logger.debug("modelName " + modelName)
            if (securityEventData != null) {
              Logger.debug("eventDescription is " + securityEventData.getEventDescription)
              selectionMap += "killchainId" -> securityEventData.getKillchainId
              selectionMap += "eventDescription" -> securityEventData.getEventDescription
              selectionMap += "model" -> modelName
            } else {
              Logger.error("Security Event Not found for modelId:" + modelId + " and securityEventId:" + securityEventId)
            }
            buf += selectionMap
            returnCount += 1
          }
        } catch {
          case ex: Exception => Logger.error("Failed to get feature id from top peer group features => {} ", ex)
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get peer group anomalies => {} ", ex)
    } finally {
      if (pstmt != null) {
        pstmt.close()
      }
      if (rs != null) {
        rs.close()
      }
      if (conn != null) {
        conn.close()
      }
    }
    buf
  }

  /**
   * Queries the Peer_Group table for a given start time, end time and Peer Id,and returns all information related to that
   * peer Id.
   * The info includes KillchainId, security event Id, security event Description.
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @param modelId Int representing the Model Id which needs to be queried for
   * @return topN ListBuffer.empty[MutableMap[String, Any] which contains all information for peer group.
   */
  def getPeerGroupAnomaliesByModelId(startTimeStr: String, endTimeStr: String, modelId: Int, topN: Integer,
                                     cache: FeatureServiceCache) = {

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = PeerGroup.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList;
      var suppressionCount = 0

      conn = getConnection(conf)
      val sqlStr = "SELECT " + PeerGroup.columns + " FROM " + tableName +
        " WHERE DATE_TIME >= ? and DATE_TIME < ? and peer_type = ? ORDER BY ANOMALY_SCORE DESC limit ?";
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setInt(3, modelId)
      pstmt.setInt(4, topN)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        try {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          //adding killchain ID
          val modelName: String = cache.getModelNameFromId(modelId)
          val securityEventId = getFeatureIdFromPeerTopFeatures(rs.getString(PeerGroup.PEER_TOP_FEATURES.toString))
          val securityEventData = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
          val entityId = rs.getString(PeerGroup.ENTITY_ID.toString)
          val peerUsers = rs.getString(PeerGroup.PEER_USERS.toString)
          // the peerUsers value can be either a string containing an ip, or a string containing a userName, so we will pass it for
          // all three of ip, host and user when checking for suppression.
          val shouldSuppress = if (securityEventData == null) true
            else suppressionDao.shouldSuppress(suppressionList, peerUsers, peerUsers, peerUsers, securityEventData.getEventType, true)

          if (shouldSuppress) {
            if (securityEventData == null) {
              Logger.error("Security Event Not found for modelId:" + modelId + " and securityEventId:" + securityEventId)
            } else {
              suppressionCount += 1
            }
          } else {
            selectionMap += "peerEntities" -> getCommaSeparatedPeers(peerUsers)

            selectionMap += "securityEventId" -> securityEventId
            selectionMap += "featureId" -> securityEventId
            selectionMap += "featureAnomalyScore" -> rs.getDouble(PeerGroup.ANOMALY_SCORE.toString)
            selectionMap += "peerFeatures" -> rs.getString(PeerGroup.PEER_TOP_FEATURES.toString)
            selectionMap += "numberOfEntities" -> rs.getInt(PeerGroup.PEER_TOTAL.toString)

            selectionMap += "killchainId" -> securityEventData.getKillchainId
            selectionMap += "eventDescription" -> securityEventData.getEventDescription
            selectionMap += "model" -> modelName

            selectionMap += "entityId" -> entityId
            if (entityId != null && entityId.nonEmpty) {
              // Get the entity so we can include the ip and host name
              // TODO: This is pretty horrible for performance - for every row, we have to get the entity.  We should either
              // TODO: implement some caching for entities, or else add and populate extra columns in the table.
              val entityInvestigatorDao = new EntityInvestigatorDao(conf)
              val entityList = entityInvestigatorDao.getEntityPropertiesByEntId(entityId, startTimeStr, endTimeStr, includePrimaryEntity = false)
              if (entityList != null && entityList.nonEmpty) {
                val entity = entityList.head
                val ipList = entity.getOrElse("ip", "").asInstanceOf[ListBuffer[MutableMap[String, String]]]
                selectionMap += "entityIps" -> entityInvestigatorDao.getFlattenedList(ipList)
                val hostList = entity.getOrElse("hostName", "").asInstanceOf[ListBuffer[MutableMap[String, String]]]
                selectionMap += "entityHostNames" -> entityInvestigatorDao.getFlattenedList(hostList)
              }
            }

            buf += selectionMap
          }
        } catch {
          case ex: Exception => Logger.error(
            "Failed to get feature id from top peer group features for modelId => {} with " +
              "exception => {}",
            modelId, ex)
        }
      }
    } catch {
      case ex: Exception => Logger
                            .error("Failed to get peer group anomalies for modelId => {} with exception => {}", modelId, ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * Queries the Peer_Group table for a given start time, end time and Peer Id,and returns all information related to that
   * peer Id.
   * The info includes KillchainId, security event Id, security event Description.
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param topN Int specifying number of results
   * @param peerId  Int specifying peer Id
   * @param modelId Int representing the Model Id which needs to be queried for
   * @return topN ListBuffer.empty[MutableMap[String, Any] which contains all information for peer group.
   */
  def getPeerGroupAnomaliesFromId(startTimeStr: String, endTimeStr: String, modelId: Integer, peerId: Int, topN: Int,
                                  cache: FeatureServiceCache) = {
    val tableName = PeerGroup.getName(conf)
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val suppressionList = suppressionDao.getSuppressionList;
      var suppressionCount = 0
      var resultCount = 0

      val sqlStr = "SELECT  * FROM " + tableName +
        " WHERE DATE_TIME >= ? and DATE_TIME <= ? and PEER_ID = ? and peer_type = ? ORDER BY ANOMALY_SCORE DESC limit ?";
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setInt(3, peerId)
      pstmt.setInt(4, modelId)
      // Get twice as many results as requested, since some may be suppressed.
      pstmt.setInt(5, topN * 2)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next() && resultCount < topN) {
        try {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          //adding killchain ID
          val modelId: Int = rs.getInt(PeerGroup.PEER_TYPE.toString)
          val modelName: String = cache.getModelNameFromId(modelId)
          val securityEventId = getFeatureIdFromPeerTopFeatures(rs.getString(PeerGroup.PEER_TOP_FEATURES.toString))
          val securityEventData = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
          val peerUsers = rs.getString(PeerGroup.ENTITY_ID.toString)
          // the peerUsers value can be either a string containing an ip, or a string containing a userName, so we will pass it for
          // all three of ip, host and user when checking for suppression.
          val shouldSuppress = if (securityEventData == null) true
          else suppressionDao.shouldSuppress(suppressionList, peerUsers, peerUsers, peerUsers, securityEventData.getEventType, true)

          if (shouldSuppress) {
            if (securityEventData != null) {
              Logger.error("Security Event Not found for modelId:" + modelId + " and securityEventId:" + securityEventId)
            } else {
              suppressionCount += 1
            }
          } else {
            val peerUsers = rs.getString(PeerGroup.ENTITY_ID.toString)
            selectionMap += "peerEntities" -> getCommaSeparatedPeers(peerUsers)
            selectionMap += "securityEventId" -> securityEventId
            selectionMap += "featureId" -> securityEventId
            selectionMap += "featureAnomalyScore" -> rs.getDouble(PeerGroup.ANOMALY_SCORE.toString)
            selectionMap += "peerFeatures" -> rs.getString(PeerGroup.PEER_TOP_FEATURES.toString)
            selectionMap += "numberOfEntities" -> rs.getInt(PeerGroup.PEER_TOTAL.toString)
            selectionMap += "killchainId" -> securityEventData.getKillchainId
            selectionMap += "eventDescription" -> securityEventData.getEventDescription
            selectionMap += "model" -> modelName
            buf += selectionMap
          }
        } catch {
          case ex: Exception => Logger.error("Failed to get feature id from top peer group features for modelId => " + modelId +
            " and peerId => " + peerId + " with exception => " + ex)
        }
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get peer group anomalies for modelId => " + modelId + " and peerId => " +
        " with exception => " + ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }


  /**
   *
   * Given a starttime, endtime and peer Id, this method returns the properties of all the  Peers for that Peer Id
   *
   * @param startTimeStr
   * @param endTimeStr
   * @param id
   * @return
   */
  def getPeerEntityProperties(startTimeStr: String, endTimeStr: String, id: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var ip_list: String = ""
    val ents = getPeerEntities(startTimeStr, endTimeStr, id)
    ents.foreach { x =>
      for ((key, value) <- x) {
        val str: String = value.asInstanceOf[String]
        val tmp: String = str.replaceAll("\"", "")
        val s = tmp.replaceAll("\\[", "")
        val ip = s.replaceAll("\\]", "")
        ip_list = if (ip_list.isEmpty) "'" + ip + "'" else ip_list + ", " + "'" + ip + "'"
      }
    }

    if (ip_list == None || ip_list.length == 0) {
      // Return empty result.
      Logger.debug("Empty IP list returned.")
      buf
    }

    //get host Entity Properties
    val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)
    hostEntityPropertiesDao.getEntHostPropsByIpList(ip_list, endTimeStr)

  }

  /**
   * Given a starttime, endtime and peer Id, this method returns the list of Peers for that Peer Id
   *
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param id Int Peer Id
   *
   * @return Peer Users for a Peer Group ID for a specified start time and end time
   */
  def getPeerEntities(startTimeStr: String, endTimeStr: String, id: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = "PEER_GROUP"
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val sqlStr = "SELECT " + PeerGroup.ENTITY_ID + " FROM " + tableName +
        " WHERE DATE_TIME >= ? and DATE_TIME <= ? and PEER_ID = ?";
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setInt(3, id)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get peer group anomalies for peerId => " +
        " with exception => " + ex.getStackTrace.toString)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * Given a starttime, endtime and peer Id, this method returns the list of Peers for that Peer Id
   *
   * @param startTimeStr String specifying start time
   * @param endTimeStr String specifying end Time
   * @param peerId Int Peer Id
   * @param modelId Int Peer Id
   *
   * @return ListBuffer.empty[MutableMap[String, Any] Peer Users for a Peer Group ID for a specified start time and end time
   */
  def getPeerEntities(startTimeStr: String, endTimeStr: String, peerId: Int, modelId: Int) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val sqlStr = "SELECT " + PeerGroup.ENTITY_ID + " FROM " + PeerGroup.getName(conf) +
        " WHERE DATE_TIME >= ? and DATE_TIME < ? and PEER_ID = ? and PEER_TYPE = ?";
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setInt(3, peerId)
      pstmt.setInt(4, modelId)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get peer group anomalies for peerId => " +
        " with exception => " + ex.getStackTrace.toString)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * returns peer properties depending upon the modelId
   * @param startTime String specifying start time
   * @param endTime String specifying end Time
   * @param peerId Int Peer Id
   * @param modelId Int Model Id
   * @return Peer properties
   */
  def getPeerPropertiesForModel(startTime: String, endTime: String, peerId: Int, modelId: Int) = {
    val ents = getPeerEntities(startTime, endTime, peerId, modelId)
    var peers: String = ""
    ents.foreach { x =>
      for ((key, value) <- x) {
        val str: String = value.asInstanceOf[String]
        // In theory, we should never get a null. In practice, we have.  So protect the code
        // from a null pointer exception.
        if (str != null) {
          //str is a string representation of Array of Strings. eg [ "10.1.1.1", "10.1.1.2" ]
          val s = str.replaceAll("\\[", "")
          var ip = s.replaceAll("\\]", "")
          //hacking this further. Because, the format changed again.
          //TODO: update this code to remove all these 'replace' once the format is defined.
          ip = ip.replace("(", "")
          ip = ip.replace(")", "")
          ip = ip.replace("'", "")
          peers = if (peers.isEmpty) ip else peers + ", " + ip
        }
      }
    }
    var buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (!peers.isEmpty) {
      // TODO: Formats for Web peer entities is different than AD. So separately parsing them. Parsing the peer entities will
      // be changed once the formats are unified in the backend
      if (modelId.equals(Constants.ADPeerAnomaliesModelTuple._1)) {
        //eg for Ad peers here: "US1-svc-logger, US1-svc-wks-difs"
        val peerArray: Array[String] = peers.split(", ")
        var adPeerString = ""
        peerArray.foreach { p =>
          //p = "US1-svc-logger"
          var str = p.substring(p.indexOf("-") + 1) //get string after index of first ","
          str = str.replace("\"", "")
          if (adPeerString.isEmpty) {
            adPeerString = "\"" + str + "\""
          } else {
            adPeerString = adPeerString + "," + "\"" + str +"\""
          }
        }
        buf = getPeerProptertiesForAd(startTime, endTime, adPeerString)
      } else if (modelId.equals(Constants.WebPeerAnomaliesModelTuple._1)) {
        buf = getPeerProptertiesForWeb(startTime, endTime, peers)
      }
    }
    buf
  }

  /**
   * Querying for user properties for AD peer group
   * @param startTime String specifying start time
   * @param endTime String specifying end Time
   * @return
   */
  private def getPeerProptertiesForAd(startTime: String, endTime: String, peers: String) = {
    val userEntProp: UserEntityPropertiesDao = new UserEntityPropertiesDao(conf)
    userEntProp.getUserPropertiesBySourceNames(startTime, endTime, peers)

  }

  /**
   * Querying for Entity properties for Web Peer Group
   * @param startTime String specifying start time
   * @param endTime String specifying end Time
   * @return
   */
  private def getPeerProptertiesForWeb(startTime: String, endTime: String, peers: String) = {
    //get host Entity Properties
    val hostEntityPropertiesDao = new HostEntityPropertiesDao(conf)
    hostEntityPropertiesDao.getEntHostPropsByIpList(peers, endTime)
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


  /**
   * Removes the domain info from the user name (so that it can used to query solr for facetting and log-searching)
   * @param peerUsers Format: for web: [ "10.1.1.1","10.1.1.2" ] ,
   *                  For ad:  ["US1-user1","ABH-user2"]
   * @return formatted string representing Array of peerEntities
   */
  private def getCommaSeparatedPeers( peerUsers: String) = {
    var peers = ""
    if(peerUsers != null) {
      val peerUsersArr = peerUsers.split(",")
      peerUsersArr.foreach { str =>
        //str is a string representation of Array of Strings. eg [ "10.1.1.1","10.1.1.2" ] OR ["US1-user1","ABH-user2"]
        var user = str.replaceAll("\\[", "")
        user = user.replaceAll("\\]", "")
        //hacking this further. Because, the format changed again.
        //TODO: update this code to remove all these 'replace' once the format is defined.
        user = user.replace("(", "")
        user = user.replace(")", "")
        user = user.replace("'", "")
        user = user.replace("\"", "")
        //form correct string
        user = "\"" + user + "\""
        peers = if (peers.isEmpty) {
          user
        } else {
          peers + "," + user
        }
      }
      //Form the array-string ["user1","user2"]
      peers = "[" + peers + "]"
    }else{
      Logger.error("Null Peer users ")
    }
    peers
  }
}

object PeerGroupDao {}


