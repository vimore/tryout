package com.securityx.modelfeature.dao

import java.sql.{ResultSet, Connection, PreparedStatement}
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.EntityModelInfo
import com.securityx.modelfeature.common.cache.{CacheRequestObject, FeatureResponseCache}
import com.securityx.modelfeature.common.inputs.{EndPointAnalytics, TimeSeriesInput}
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, SecurityEventTypeConfiguration}
import com.securityx.modelfeature.utils.{Constants, MiscUtils, TaniumStats}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{Map => MutableMap, ListBuffer}
import scala.util.control.Breaks

/**
 * Created by harish on 7/22/15.
 */
class TaniumStatsDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {


  private final val Logger: Logger = LoggerFactory.getLogger(classOf[TaniumStatsDao])

  // This constant is used to control the number of md5 we return.  Since each md5 results in another query,
  // a large set can result in timeouts in the UI because we're still processing data for thousands of results.
  // This limits that.
  private final val MAX_MD5_FOR_HOST = 50

  def getTimeSeriesByTypeValue(modelId: Int, typeField: String, group: String, value: String, startTime: String, endTime: String) = {
    if(typeField.equalsIgnoreCase("HOST")) {
      getCountForAllHosts(modelId, typeField, group, value, startTime, endTime)
    }  else {
      getHostCountTimeSeriesByTypeValue(modelId, typeField, group, value, startTime, endTime)
    }
  }

    /**
   * Queries Tanium_STATS table for a given type and returns time-series of hosts count
   * @return
   */
  def getHostCountTimeSeriesByTypeValue(modelId: Int, typeField: String, group: String, value: String, startTime: String, endTime: String) = {

      //typefield is used to find if the query is for Newly Observed or Not
    var newlyObserved : String = null
    if(typeField.contains("new")){
      newlyObserved = "Y"
    }

      //group is to find whether it is MD5/AUTO_MD5/PORT or "all".
    val typeString = group

     // key: (day, type), value: (Set(hostnames), count)
    val dateTypeToHostCountMap: MutableMap[String, HostTrackers] = MutableMap[String, HostTrackers]()
      val tableName = TaniumStats.getName(conf)

      var allHosts: mutable.HashSet[String] =  mutable.HashSet[String]()
      var allProcessess: mutable.HashSet[String] =  mutable.HashSet[String]()
      var allDatesSeen: mutable.HashSet[String] =  mutable.HashSet[String]()
      var conn: Connection = null
      try {

        var sqlStr = "select * from " + tableName + " where " +
          TaniumStats.DATE_TIME + " >= ? " +
          " AND " + TaniumStats.DATE_TIME + " < ? "

        if(newlyObserved != null){
          sqlStr = sqlStr +  " AND " + TaniumStats.NEWLY_OBSERVED + "  IS NOT NULL "
        }

        if(typeString != null && typeString.nonEmpty){
          if(typeString.equalsIgnoreCase("all")){
            sqlStr = sqlStr + " AND (" + TaniumStats.TYPE + "  =  'MD5' OR " +  TaniumStats.TYPE + " = 'PORT'  OR " +
            TaniumStats.TYPE + " =  'AUTO_MD5')"
          } else {
            sqlStr = sqlStr + " AND " + TaniumStats.TYPE + "  = '" + typeString + "'"
          }
        }
        if(value != null && value.nonEmpty){
            sqlStr = sqlStr + " AND "  + TaniumStats.TYPE_VALUE + "  = ? "
        }

        conn = getConnection(conf)
        val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)

        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)

        //value is to query for the VALUE of MD5/AUTO_MD5/PORT. Basically column TYPE_VALUE in the Table TANIUM_STATS
        if(value != null && value.nonEmpty){
          pstmt.setString(3, value)
        }

        val rs = executeQuery(pstmt)
        while (rs.next()) {
          val dateTime = rs.getString(TaniumStats.DATE_TIME.toString)
          val dayTime = MiscUtils.getYMDSeparatedString(dateTime)
          val typeStr = rs.getString(TaniumStats.TYPE.toString)
          val hostNew = rs.getString(TaniumStats.HOSTS_CURRENT.toString)
          val hostAll = rs.getString(TaniumStats.HOSTS_HISTORICAL.toString)
          val datesSeen = rs.getString(TaniumStats.DATES_SEEN.toString)
          val processHistorical = rs.getString(TaniumStats.PROCESSES_HISTORICAL.toString)
          val processNew = rs.getString(TaniumStats.PROCESSES_CURRENT.toString)

          val processHistoricalSet = getSetFromPipeSeparatedString(processHistorical)
          val processNewSet = getSetFromPipeSeparatedString(processNew)
          val datesSeenSet = getSetFromPipeSeparatedString(datesSeen)

          val newHostSet = getSetFromPipeSeparatedString(hostNew)
          val allHostSet = getSetFromPipeSeparatedString(hostAll)

          var allMd5HostSet: mutable.HashSet[String] =  mutable.HashSet[String]()
          var newMd5HostSet: mutable.HashSet[String] =  mutable.HashSet[String]()
          var allAutoMd5HostSet: mutable.HashSet[String] =  mutable.HashSet[String]()
          var newAutoMd5HostSet: mutable.HashSet[String] =  mutable.HashSet[String]()
          var allPortHostSet: mutable.HashSet[String] =  mutable.HashSet[String]()
          var newPortHostSet: mutable.HashSet[String] =  mutable.HashSet[String]()

          //tracking all hosts
          allHosts ++= allHostSet
          allHosts ++= newHostSet

          //tracking all processes
          allProcessess ++= processHistoricalSet
          allProcessess ++= processNewSet

          allDatesSeen ++= datesSeenSet
          if(typeStr.equalsIgnoreCase("MD5")){
            allMd5HostSet ++= allHostSet
            newMd5HostSet ++= newHostSet
          }else if(typeStr.equalsIgnoreCase("PORT")){
            allPortHostSet ++= allHostSet
            newPortHostSet ++= newHostSet
          }else if(typeStr.equalsIgnoreCase("AUTO_MD5")){

            allAutoMd5HostSet ++= allHostSet
            newAutoMd5HostSet ++= newHostSet
          }


          val key = dayTime
          var hostTrackers: HostTrackers = null
          if (dateTypeToHostCountMap.contains(key)) {
            hostTrackers = dateTypeToHostCountMap(key)
            hostTrackers.allMd5HostSet ++= allMd5HostSet
            hostTrackers.newMd5HostSet ++= newMd5HostSet
            hostTrackers.allAutoMd5HostSet ++= allAutoMd5HostSet
            hostTrackers.newAutoMd5HostSet ++= newAutoMd5HostSet
            hostTrackers.allPortSet ++= allPortHostSet
            hostTrackers.newPortSet ++= newPortHostSet

            //update counts
            hostTrackers.allMd5HostCount = hostTrackers.allMd5HostSet.size
            hostTrackers.newMd5HostCount = hostTrackers.newMd5HostSet.size
            hostTrackers.allAutoMd5HostCount = hostTrackers.allAutoMd5HostSet.size
            hostTrackers.newAutoMd5HostCount = hostTrackers.newAutoMd5HostSet.size
            hostTrackers.allPortHostCount = hostTrackers.allPortSet.size
            hostTrackers.newPortHostCount = hostTrackers.newPortSet.size
          }
          else {
            hostTrackers = new HostTrackers(dayTime, allMd5HostSet,allMd5HostSet.size,
              newMd5HostSet, newMd5HostSet.size, allAutoMd5HostSet, allAutoMd5HostSet.size,
              newAutoMd5HostSet, newAutoMd5HostSet.size, allPortHostSet, allPortHostSet.size,
              newPortHostSet, newPortHostSet.size)
          }
          dateTypeToHostCountMap += dayTime -> hostTrackers
        }
      } catch {
        case e: Exception => Logger.error("Failed to get host-count time-series for type => " + typeString + "  => " + e)
      } finally {
        closeConnections(conn)
      }

      val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
      val selectionMap = MutableMap[String, Any]()
      selectionMap += "timeSeries" ->   scala.collection.immutable.TreeMap(dateTypeToHostCountMap.toSeq.sortBy(_._1): _*).values.toList
      selectionMap += "allHosts" -> allHosts.size
      selectionMap += "allProcesses" -> allProcessess.size
      val sortedDateSet = allDatesSeen.toSeq.sorted
      var head : String = "-"
      var tail: String = "-"
      if(sortedDateSet != null && sortedDateSet.size > 0) {
        head = sortedDateSet.head
        tail = sortedDateSet.takeRight(1).head
      }
      selectionMap += "firstTimeSeen" -> head
      selectionMap += "lastTimeSeen" -> tail
      buf += selectionMap
      buf
  }


  /**
   * count hosts for given type
   * @param typeField
   * @param group
   * @param value
   * @param startTime
   * @param endTime
   * @return
   */
  def getCountForAllHosts(modelId: Int, typeField: String, group: String, value: String, startTime: String, endTime: String) = {
    val dateTypeToHostCountMap: MutableMap[String, HostPropertiesTracker] = MutableMap[String, HostPropertiesTracker]()
    val tableName = TaniumStats.getName(conf)
    var conn: Connection = null
    try {

      var sqlStr = "select * from " + tableName + " where " +
        "( " + TaniumStats.TYPE + "  = 'HOST'" + " OR " + TaniumStats.TYPE + " = 'AUTO_HOST' " + " ) " +
        " AND " +
        "( " + TaniumStats.DATE_TIME + " >= ? " +
        " AND " + TaniumStats.DATE_TIME + " < ? "   + " ) "
      if(value != null && value.nonEmpty){
        sqlStr = sqlStr + " AND " + TaniumStats.HOST_NAME + " LIKE '" + value + "'"
      }
      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      val rs = executeQuery(pstmt)
      while (rs.next()) {
        val dateTime = rs.getString(TaniumStats.DATE_TIME.toString)
        val dayTime = MiscUtils.getYMDSeparatedString(dateTime)
        val typeStr = rs.getString(TaniumStats.TYPE.toString)
        val md5New = rs.getString(TaniumStats.MD5S_CURRENT.toString)
        val md5all = rs.getString(TaniumStats.MD5S_HISTORICAL.toString)
        val portNew = rs.getString(TaniumStats.PORTS_CURRENT.toString)
        val portall = rs.getString(TaniumStats.PORTS_HISTORICAL.toString)

        val riskScoreString: String = if (Constants.EndpointGlobalModelTuple._1.equals(modelId))
          rs.getString(TaniumStats.RISK_SCORE_GLOBAL.toString) else rs.getString(TaniumStats.RISK_SCORE.toString)
        var risk = 0d
        try {
          risk = riskScoreString.toDouble
          if (risk > 10.0) risk = 10.0
        } catch {
          case e: Exception => Logger.error("Failed to get Host riskscore   => " + e)
        }

        val newMd5Set = getSetFromPipeSeparatedString(md5New)
        val allMd5Set = getSetFromPipeSeparatedString(md5all)
        val newPortSet = getSetFromPipeSeparatedString(portNew)
        val allPortSet = getSetFromPipeSeparatedString(portall)
        val newAutoMd5Set = getSetFromPipeSeparatedString(md5New)
        val allAutoMd5Set = getSetFromPipeSeparatedString(md5all)

        var hostPropertiesTracker: HostPropertiesTracker = null
        if (dateTypeToHostCountMap.contains(dayTime)) {
          hostPropertiesTracker = dateTypeToHostCountMap(dayTime)
          hostPropertiesTracker.newMd5Set ++= newMd5Set
          hostPropertiesTracker.newMd5Count =  hostPropertiesTracker.newMd5Set.size

          hostPropertiesTracker.allMd5Set ++= allMd5Set
          hostPropertiesTracker.allMd5Count= hostPropertiesTracker.allMd5Set.size

          hostPropertiesTracker.newAutoMd55Set ++= newAutoMd5Set
          hostPropertiesTracker.newAutoMd5Count = hostPropertiesTracker.newAutoMd55Set.size

          hostPropertiesTracker.allAutoMd5Set ++= allAutoMd5Set
          hostPropertiesTracker.allAutoMd5Count = hostPropertiesTracker.allAutoMd5Set.size

          hostPropertiesTracker.newPortSet ++= newPortSet
          hostPropertiesTracker.newPortCount = hostPropertiesTracker.newPortSet.size

          hostPropertiesTracker.allPortSet ++= allPortSet
          hostPropertiesTracker.allPortCount = hostPropertiesTracker.allPortSet.size

          if(risk > hostPropertiesTracker.riskScore){
            hostPropertiesTracker.riskScore = risk
          }

        }
        else {
          hostPropertiesTracker = new HostPropertiesTracker(dayTime, typeStr,
            allMd5Set, newMd5Set,
            allMd5Set.size, newMd5Set.size,
          allAutoMd5Set, newAutoMd5Set,
            allAutoMd5Set.size, newAutoMd5Set.size,
          allPortSet, newPortSet,
            allPortSet.size, newPortSet.size, risk )
        }
        dateTypeToHostCountMap += dayTime -> hostPropertiesTracker
      }
    } catch {
      case e: Exception => Logger.error("Failed to get host-properties count for hosts  => " + e)
    } finally {
      closeConnections(conn)
    }
    scala.collection.immutable.TreeMap(dateTypeToHostCountMap.toSeq.sortBy(_._1): _*).values.toList
  }

  def searchData(input:TimeSeriesInput) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null

    try {
      conn = getConnection(conf)
       var sqlStr = " SELECT * FROM " + TaniumStats.getName(conf) +  " WHERE " +
      TaniumStats.DATE_TIME + " >=  ? " +
         " AND " + TaniumStats.DATE_TIME + " < ? "

      //form SQL String
      var temp = ""
      for(i <- 0 to input.getFacets.size() - 1){
        val paramMap: util.Map[String, String] = input.getFacets.get(i)
        val typeField =  paramMap.get("typeField")
        val group =  paramMap.get("group")
        val typeValue =  paramMap.get("groupId")

        var s = ""

        if(group != null && group.nonEmpty){
          if(group.equalsIgnoreCase("all")){
            /*s =  TaniumStats.TYPE + "  =  'MD5' OR " +  TaniumStats.TYPE + " = 'PORT'  OR " +
              TaniumStats.TYPE + " =  'AUTO_MD5'"*/
          } else {
            s =  TaniumStats.TYPE + "  = '" + group + "'"
          }
        } else{ //i.e if group == null
          if(typeField.equalsIgnoreCase("HOST")){
            s =  TaniumStats.TYPE + "  = 'HOST'"
          }
        }

        if(typeValue != null && typeValue.nonEmpty){
          if (typeField.equalsIgnoreCase("HOST")) {
            s = s + " AND " + TaniumStats.HOST_NAME + "  = '" + typeValue + "'"
          } else {
            s = s + " AND " + TaniumStats.TYPE_VALUE + "  = '" + typeValue + "'"
          }
        }

        //newly observed ?
        var newlyObserved : String = ""
        if(typeField.contains("new")){
          newlyObserved = "Y"
        }

        if(newlyObserved != null && newlyObserved.nonEmpty){
          s = if(s.isEmpty) { s } else{ s + " AND "}
          s = s + TaniumStats.NEWLY_OBSERVED + "  = 'Y' "
        }


        if(temp.nonEmpty){
          temp = temp + " OR ( " + s + " ) "
        }else{
          temp = "( " + s + " ) "
        }

      }
      if(temp.nonEmpty){
        sqlStr = sqlStr + " AND (" + temp  + " )"
      }

      sqlStr = sqlStr + " LIMIT  ? "
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, input.getStartTime)
      pstmt.setString(2, input.getEndTime)
      pstmt.setInt(3, input.getNumRows)

      val rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        val typeStr = rs.getString(TaniumStats.TYPE.toString)
        if (typeStr.equalsIgnoreCase("MD5") || typeStr.equalsIgnoreCase("PORT") ||
          typeStr.equalsIgnoreCase("AUTO_MD5") || typeStr.equalsIgnoreCase("HOST")) {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          if (Constants.EndpointGlobalModelTuple._1.equals(input.getModelId)) {
            // Move the global risk score to risk score when we're returning results for the global endpoint model
            val riskScoreGlobal = selectionMap.getOrElse("riskScoreGlobal", "0")
            selectionMap += "riskScore" -> riskScoreGlobal
          }
          // if globalRiskScore is part of the map, remove it - having two risk scores in the results is only
          // confusing for the UI, and by passing in the model id, they've told us which one they want.
          selectionMap -= "riskScoreGlobal"
          buf += selectionMap
        }

      }
    } catch {
      case e: Exception => Logger.error("Failed to get tanium search result  => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  // This code is not being updated as part of the endpoint bifurcation work. If we ever decide to call it again,
  // it will likely need work.
  @Deprecated
  def getTaniumTopNHostsByRisk(startTime: String, endTime: String, n: Int, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    try {
      conn = getConnection(conf)
      val sqlStr = " SELECT * FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " +
        " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND " + TaniumStats.TYPE + " = " + "'HOST'" +
        " ORDER BY " + TaniumStats.RISK_SCORE + " DESC "
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      val ipToScoresMap: MutableMap[EntityUniqueness, MutableMap[String, Any]] = MutableMap[EntityUniqueness, MutableMap[String, Any]]()

      val rs = executeQuery(pstmt)
      while (rs.next() && ipToScoresMap.size < n) {

        val processes = rs.getString(TaniumStats.PROCESSES_CURRENT.toString)
        val processSet : mutable.HashSet[String] = getSetFromPipeSeparatedString(processes)
        val hostName: String = rs.getString(TaniumStats.HOST_NAME.toString)
        val ipAddress: String = ""
        val userName: String = ""
        val macAddress: String = ""
        val riskScoreString: String = rs.getString(TaniumStats.RISK_SCORE.toString)
        val dateTime: String = rs.getString(TaniumStats.DATE_TIME.toString)
        var risk = 0d
        try {
          risk = riskScoreString.toDouble
        } catch {
          case e: Exception => Logger.error("Failed to get tanium riskscore  => " + e)
        }
        val modelId = com.securityx.modelfeature.utils.Constants.TaniumModelTuple._1

        val securityEventId: Int = 0 //TODO: REMOVE SECURITY EVENT ID = 0  (HARD CODED)
        val securityEventData: SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
        if (securityEventData != null) {
          val eventTypePrefix: String = securityEventData.getTypePrefix
          val eventType: String = securityEventData.getEventType
          val eventDescription: String = securityEventData.getEventDescription
          val shortDescription: String = securityEventData.getShortDescription
          val killchainId: Int = securityEventData.getKillchainId
          val featureLabel: String = securityEventData.getFeatureLabel
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId

          var selectionMap: MutableMap[String, Any] = null
          val entity: EntityUniqueness = new EntityUniqueness(ipAddress, userName, hostName, macAddress)
          //if Ip already exists in the result buf
          if (ipToScoresMap.contains(entity)) {
            selectionMap = ipToScoresMap(entity)
          }
          else {
            selectionMap = MutableMap[String, Any]()
            ipToScoresMap += entity -> selectionMap
            buf += selectionMap
          }

          //find current score
          val currentScore: Double = {
            if (selectionMap.contains("currentScore"))
              Math.max(selectionMap("currentScore").asInstanceOf[Double], risk)
            else
              risk
          }

          val eventInfo: EntityModelInfo = new EntityModelInfo(securityEventId, killchainId, featureLabel,
            eventTypePrefix, eventType, eventDescription, shortDescription, modelId, cardId, risk, dateTime)

          //killchain info list
          val list: ListBuffer[EntityModelInfo] = {
            if (selectionMap.contains("modelScores"))
              selectionMap("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
            else
              ListBuffer[EntityModelInfo]()
          }
          list += eventInfo


          //processSet:
          val set  =   {
            if (selectionMap.contains("processes"))
              selectionMap("processes").asInstanceOf[mutable.HashSet[String]]
            else
              mutable.HashSet[String]()
          }
          set ++= processSet

          //populate result map
          selectionMap += "ipAddress" -> ipAddress
          selectionMap += "hostName" -> hostName
          selectionMap += "userName" -> userName
          selectionMap += "currentScore" -> currentScore
          selectionMap += "modelScores" -> list
          selectionMap += "processes" -> set
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get tanium threats  => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  // This code is not being updated as part of the endpoint bifurcation work. If we ever decide to call it again,
  // it will likely need work.
  def getTaniumTopNHostsByRisk(startTime: String, endTime: String, n: Int,
                               buf: collection.mutable.ListBuffer[MutableMap[String, Any]], cache: FeatureServiceCache, hosts: Array[String]) = {
    var conn: Connection = null
    var hostsString : String = ""
    try {
      conn = getConnection(conf)
      var sqlStr = " SELECT * FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " +
        " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND " + TaniumStats.TYPE + " = " + "'HOST'"

      if(hosts != null && !hosts.isEmpty){

        for (host <- hosts) {
          if(hostsString.isEmpty)
            hostsString = "\'" + host + "\'"
          else
            hostsString = hostsString + ", \'" + host + "\'"
        }

        if(hostsString.nonEmpty){
          sqlStr = sqlStr + " AND " + TaniumStats.HOST_NAME + " IN " + " ( " + hostsString + " )"
        }
      }

      sqlStr = sqlStr + " ORDER BY " + TaniumStats.RISK_SCORE + " DESC "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)

      val ipToScoresMap: MutableMap[EntityUniqueness, MutableMap[String, Any]] = MutableMap[EntityUniqueness, MutableMap[String, Any]]()

      val rs = executeQuery(pstmt)
      while (rs.next() && ipToScoresMap.size < n) {

        val newProcesses = rs.getString(TaniumStats.PROCESSES_NEW.toString)
        val newMd5s = rs.getString(TaniumStats.MD5S_NEW.toString)

        val newProcessSet : mutable.HashSet[String] = getSetFromPipeSeparatedString(newProcesses)
        val newMd5sSet : mutable.HashSet[String] = getSetFromPipeSeparatedString(newMd5s)

        val hostName: String = rs.getString(TaniumStats.HOST_NAME.toString)
        val dateTime: String = rs.getString(TaniumStats.DATE_TIME.toString)
        val ipAddress: String = ""
        val userName: String = ""
        val macAddress: String = ""
        val riskScoreString: String = rs.getString(TaniumStats.RISK_SCORE.toString)
        var risk = 0d
        try {
          risk = riskScoreString.toDouble * 0.1
        } catch {
          case e: Exception => Logger.error("Failed to get tanium riskscore  => " + e)
        }
        val modelId = com.securityx.modelfeature.utils.Constants.TaniumModelTuple._1

        val securityEventId: Int = 0 //TODO: REMOVE SECURITY EVENT ID = 0  (HARD CODED)
        val securityEventData: SecurityEventTypeConfiguration = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId)
        if (securityEventData != null) {
          val eventTypePrefix: String = securityEventData.getTypePrefix
          val eventType: String = securityEventData.getEventType
          val eventDescription: String = securityEventData.getEventDescription
          val shortDescription: String = securityEventData.getShortDescription
          val killchainId: Int = securityEventData.getKillchainId
          val featureLabel: String = securityEventData.getFeatureLabel
          val cardId: Int = cache.getSecurityEventDataFromFeatureIdModelId(securityEventId, modelId).getCardId
          var selectionMap: MutableMap[String, Any] = null
          val entity: EntityUniqueness = new EntityUniqueness(ipAddress, userName, hostName, macAddress)
          //if Ip already exists in the result buf
          if (ipToScoresMap.contains(entity)) {
            selectionMap = ipToScoresMap(entity)
          }
          else {
            selectionMap = MutableMap[String, Any]()
            ipToScoresMap += entity -> selectionMap
            buf += selectionMap
          }

          //find current score
          val currentScore: Double = {
            if (selectionMap.contains("currentScore")) {
              if(selectionMap("currentScore").asInstanceOf[Double] < risk){
                selectionMap += "dateTime" -> dateTime
                risk
              }else{
                selectionMap("currentScore").asInstanceOf[Double]
              }
            }
            else {
              selectionMap += "dateTime" -> dateTime
              risk
            }
          }

          val eventInfo: EntityModelInfo = new EntityModelInfo(securityEventId, killchainId, featureLabel,
            eventTypePrefix, eventType, eventDescription, shortDescription, modelId, cardId, risk, dateTime)

          //killchain info list
          val list: ListBuffer[EntityModelInfo] = {
            if (selectionMap.contains("modelScores"))
              selectionMap("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
            else
              ListBuffer[EntityModelInfo]()
          }
          list += eventInfo


          //processSet:
          val processSet  =   {
            if (selectionMap.contains("processes"))
              selectionMap("processes").asInstanceOf[mutable.HashSet[String]]
            else
              mutable.HashSet[String]()
          }
          processSet ++= newProcessSet

          //md5Set:
          val md5Set  =   {
            if (selectionMap.contains("md5s"))
              selectionMap("md5s").asInstanceOf[mutable.HashSet[String]]
            else
              mutable.HashSet[String]()
          }
          md5Set ++= newMd5sSet

          //populate result map
          selectionMap += "ipAddress" -> ipAddress
          selectionMap += "hostName" -> hostName
          selectionMap += "userName" -> userName
          selectionMap += "currentScore" -> currentScore
          selectionMap += "modelScores" -> list
          selectionMap += "processes" -> processSet
          selectionMap += "md5s" -> md5Set
          selectionMap += "dateTime" -> dateTime

        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to populate top tanium threats  => " + e)
    } finally {
      closeConnections(conn)
    }
  }


  def getTaniumEntityCards(startTime: String, endTime: String, processList: util.List[String], md5List: util.List[String]) = {
    val buf = new ListBuffer[MutableMap[String, Any]]()
    var conn: Connection = null
    try {
      val typeMap = MutableMap[String, MutableMap[String, Any]]()
      conn = getConnection(conf)
      // val likeOperatorString = SqlUtils.getEqualsOperatorString(processList, TaniumStats.TYPE_VALUE.toString)
      val sqlStr = " SELECT " + TaniumStats.TYPE + ", " + TaniumStats.TYPE_VALUE + ", " + TaniumStats.NEWLY_OBSERVED +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " +
        " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND (" + TaniumStats.TYPE + " = " + "'PROC'" + " OR " + TaniumStats.TYPE + " = " + "'MD5'" + ") " +
        " ORDER BY " + TaniumStats.RISK_SCORE + " DESC "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      val rs = executeQuery(pstmt)
      while (rs.next()) {
        val newlyObserved = rs.getString(TaniumStats.NEWLY_OBSERVED.toString)
        var typeValue = rs.getString(TaniumStats.TYPE_VALUE.toString)
        val typeString = rs.getString(TaniumStats.TYPE.toString)
        var selectionMap: MutableMap[String, Any] = null
        if (typeMap.contains(typeString)) {
          selectionMap = typeMap(typeString)
        } else {
          selectionMap = MutableMap[String, Any]()
          typeMap += typeString -> selectionMap
          buf += selectionMap
        }

        if (typeValue != null) {
          typeValue = typeValue.trim

          //type = PROC
          if (typeString.equals("PROC")) {
            selectionMap += "type" -> "PROCESS"
            val proc = typeValue
            if (typeString.nonEmpty && processList.contains(proc)) {
              var flag = false
              if(newlyObserved != null){
                flag = true
              }
              val globalTracker = new GlobalTracker(proc, flag)
              var list: ListBuffer[GlobalTracker] = null
              if (selectionMap.contains("values")) {
                list = selectionMap("values").asInstanceOf[ListBuffer[GlobalTracker]]
              } else {
                list = new ListBuffer[GlobalTracker]()
                selectionMap += "values" -> list
              }
              list += globalTracker
            }

          }

          //type = MD5
          if (typeString.equals("MD5")) {
            selectionMap += "type" -> "MD5"
            val md5 = typeValue

            if (typeString.nonEmpty && md5List.contains(md5)) {
              var flag = false
              if(newlyObserved != null){
                flag = true
              }
              val globalTracker = new GlobalTracker(md5, flag)
              var list: ListBuffer[GlobalTracker] = null
              if (selectionMap.contains("values")) {
                list = selectionMap("values").asInstanceOf[ListBuffer[GlobalTracker]]
              } else {
                list = new ListBuffer[GlobalTracker]()
                selectionMap += "values" -> list
              }
              list += globalTracker
            }


          }

        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  // In order to write a better query, getEndpointAnalyticsData() is being split in two.  getEndpointAnalyticsDataByTypeValue() is
  // the original method, and I have left it as-is for the moment in case we need to back out this change because it introduces other
  // bugs.  But at some point getEndpointAnalyticsDataByTypeValue() must be cleaned up.  It wouldn't be a bad thing to go through
  // getEndpointAnalyticsDataNoTypeValue() as well...
  /**
   * Get data for endpoint analytics, such as a set of MD5s or ports used, or details about a given MD5.
   *
   * @param input an object containing the details of what we are searching for
   * @param featureResponseCache cache containing any previously fetched values for such queries, so we can avoid doing them
   *                             again in some cases
   * @return A list containing a single hash map, which in turn contains a total and a list of maps containing further
   *         information about the results
   */
  def getEndpointAnalyticsData(input: EndPointAnalytics, featureResponseCache: FeatureResponseCache) = {
    val typeValue = input.getTypeValue
    if (typeValue == null || typeValue.isEmpty) {
      // This call will always return a set of simple values, like a set of md5s, or a set of ports
      getEndpointAnalyticsDataNoTypeValue(input, featureResponseCache)
    } else {
      // This will (I believe) always return details about a small set of endpoint results
      getEndpointAnalyticsDataByTypeValue(input, featureResponseCache)
    }
  }

  /**
   * Get data for the endpoint analytics. Querying by TYPE or (TYPE AND TYPE_VALUE)
   * @param input
   * @param featureResponseCache
   * @return
   */
  def getEndpointAnalyticsDataByTypeValue(input: EndPointAnalytics, featureResponseCache: FeatureResponseCache) = {
    val buf = new ListBuffer[MutableMap[String, Any]]()
    var conn: Connection = null
    try {
      var typeField = input.getTypeField
      val typeValue = input.getTypeValue
      var isAutoMd5 = false
      if(typeField.equalsIgnoreCase("AUTO_MD5")){
        typeField = "MD5"
        isAutoMd5 = true
      }
      val isNox = input.isNox
      val modelId = input.getModelId

      conn = getConnection(conf)
      // val likeOperatorString = SqlUtils.getEqualsOperatorString(processList, TaniumStats.TYPE_VALUE.toString)
      var sqlStr = " SELECT * " +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? "
      if(typeField != null && typeField.nonEmpty) {
        if(typeField.equalsIgnoreCase("PROC")){ //for PROC, also look for TYPE = 'JOB'
          sqlStr =  sqlStr + " AND ( " + TaniumStats.TYPE + " = ?  OR " + TaniumStats.TYPE + " = 'JOB' ) "
        }else {
          sqlStr = sqlStr + " AND " + TaniumStats.TYPE + " = ? "
        }
      }
      if(typeValue != null && typeValue.nonEmpty) {
        // This may fail if type field is ever HOST, since Alex has moved host names to the HOST_NAME field.
        // It's unclear to me if we need to worry about that.
        sqlStr = sqlStr + " AND " + TaniumStats.TYPE_VALUE + " LIKE '%" + typeValue +"%'"
      }
      // mlarsen: The old version of this only added the clause for newly observed when no typeValue
      // was passed in.  I'm not sure why, and I'm getting rid of it, but I thought it would be good
      // to capture that we used to do that, in case I'm introducing a bug.
      //if(isNox && (typeValue == null || typeValue.isEmpty)) {
      if(isNox) {
        // We need to check different things depending on whether we're looking for global newly observed or
        // local newly observed.
        if (Constants.TaniumModelTuple._1.equals(modelId) || Constants.EndpointGlobalModelTuple._1.equals(modelId)) {
          sqlStr = sqlStr + " AND " + TaniumStats.HOSTS_HISTORICAL + " IS NULL"
        } else {
          // For local newly observed, we'd really prefer to check the list in hosts_historical against hosts_current to
          // make sure that the host appears only in hosts_current.  But Phoenix can't do that (perhaps we can if/when
          // we change to impala).  In the meantime, Alex says that the risk score is an indicator of that condition -
          // if we've see it before, the risk score will be 0.
          // Alex has changed his mind on this, but I'm going to keep this in case he changes his mind again...  At the
          // moment, he says the only important clause for local is the "newly_observed is not null" which is added below
          // for both local and global.  See E8-2358.
          // sqlStr = sqlStr + " AND " + TaniumStats.HOSTS_HISTORICAL + " IS NOT NULL AND " + TaniumStats.RISK_SCORE + " > '0'"
        }
        sqlStr = sqlStr + " AND " + TaniumStats.NEWLY_OBSERVED + " IS NOT NULL "
      }

      if(isAutoMd5){
        sqlStr = sqlStr + " AND " + TaniumStats.KEYS_CURRENT + " IS NOT NULL "
      }
      sqlStr =  sqlStr + " ORDER BY " + TaniumStats.DATES_SEEN + " DESC "

      if (!(typeValue == null || typeValue.isEmpty)) {
        // Since we are going to be getting details in populateListByTypeValue(), this query can be
        // very expensive if there are many results.  So we will limit it to a manageable number
        val endpointLimit = conf.getEndpointLimit
        sqlStr = sqlStr + " LIMIT " + endpointLimit
      }

      Logger.info("getEndpointAnalyticsDataByTypeValue(): Executing query [" + sqlStr + "]")
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, input.getStartTime)
      pstmt.setString(2, input.getEndTime)
      var cacheKeyStr = sqlStr + " # " + input.getStartTime + " # " + input.getEndTime
      var i = 3
      if(typeField != null && typeField.nonEmpty) {
        pstmt.setString(i, typeField)
        i = i + 1
        cacheKeyStr = cacheKeyStr + " # " + typeField
      }
      val resultList: util.List[util.Map[String, AnyRef]] = getFromFeatureResponseCache("getListByType",
        cacheKeyStr,
        conn, pstmt, featureResponseCache)

      val selectionMap = MutableMap[String, Any]()
      if (typeValue == null || typeValue.isEmpty) {
        populateListByType(input, resultList, featureResponseCache, buf)
      }else {
        populateListByTypeValue(input, resultList, featureResponseCache, selectionMap, buf)
      }
      if(selectionMap.nonEmpty){
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get endpoint-analytics data for type: " + input.getTypeField +
        " And typeValue: " + input.getTypeValue + "  => " + e)
    } finally {
      closeConnections(conn)
    }
    buf

  }

  // This is basically a copy of getEndpointAnalyticsDataByTypeValue(), with changes to the query to only get distinct
  // values, in an effort to improve performance.
  def getEndpointAnalyticsDataNoTypeValue(input: EndPointAnalytics, featureResponseCache: FeatureResponseCache) = {
    val buf = new ListBuffer[MutableMap[String, Any]]()
    var conn: Connection = null
    try {
      var typeField = input.getTypeField
      var isAutoMd5 = false
      if(typeField.equalsIgnoreCase("AUTO_MD5")){
        typeField = "MD5"
        isAutoMd5 = true
      }
      val isNox = input.isNox
      val modelId = input.getModelId

      conn = getConnection(conf)
      var sqlStr = " SELECT DISTINCT " + TaniumStats.TYPE_VALUE + ", SUBSTR(" + TaniumStats.DATES_SEEN + ", 1, 20) " +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? "
      if(typeField != null && typeField.nonEmpty) {
        if(typeField.equalsIgnoreCase("PROC")){ //for PROC, also look for TYPE = 'JOB'
          sqlStr =  sqlStr + " AND ( " + TaniumStats.TYPE + " = ?  OR " + TaniumStats.TYPE + " = 'JOB' ) "
        }else {
          sqlStr = sqlStr + " AND " + TaniumStats.TYPE + " = ? "
        }
      }
      // mlarsen: The old version of this only added the clause for newly observed when no typeValue
      // was passed in.  I'm not sure why, and I'm getting rid of it, but I thought it would be good
      // to capture that we used to do that, in case I'm introducing a bug.
      //if(isNox && (typeValue == null || typeValue.isEmpty)) {
      if(isNox) {
        // We need to check different things depending on whether we're looking for global newly observed or
        // local newly observed.
        if (Constants.TaniumModelTuple._1.equals(modelId) || Constants.EndpointGlobalModelTuple._1.equals(modelId)) {
          sqlStr = sqlStr + " AND " + TaniumStats.HOSTS_HISTORICAL + " IS NULL"
        } else {
          // For local newly observed, we'd really prefer to check the list in hosts_historical against hosts_current to
          // make sure that the host appears only in hosts_current.  But Phoenix can't do that (perhaps we can if/when
          // we change to impala).  In the meantime, Alex says that the risk score is an indicator of that condition -
          // if we've see it before, the risk score will be 0.
          // Alex has changed his mind on this, but I'm going to keep this in case he changes his mind again...  At the
          // moment, he says the only important clause for local is the "newly_observed is not null" which is added below
          // for both local and global.  See E8-2358.
          // sqlStr = sqlStr + " AND " + TaniumStats.HOSTS_HISTORICAL + " IS NOT NULL AND " + TaniumStats.RISK_SCORE + " > '0'"
        }
        sqlStr = sqlStr + " AND " + TaniumStats.NEWLY_OBSERVED + " IS NOT NULL "
      }

      if(isAutoMd5){
        sqlStr = sqlStr + " AND " + TaniumStats.KEYS_CURRENT + " IS NOT NULL "
      }
      // Note: we do a substr here (and in the select clause) in order to get the first date out of the | separated list
      // of dates in the dates_seen column. This allows us to get the results we're looking for in the order in which
      // the were first seen.
      sqlStr =  sqlStr + " ORDER BY SUBSTR(" + TaniumStats.DATES_SEEN + ", 1, 20) DESC "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, input.getStartTime)
      pstmt.setString(2, input.getEndTime)
      var cacheKeyStr = sqlStr + " # " + input.getStartTime + " # " + input.getEndTime
      var i = 3
      if(typeField != null && typeField.nonEmpty) {
        pstmt.setString(i, typeField)
        i = i + 1
        cacheKeyStr = cacheKeyStr + " # " + typeField
      }
      val resultList: util.List[util.Map[String, AnyRef]] = getFromFeatureResponseCache("getListByType",
        cacheKeyStr,
        conn, pstmt, featureResponseCache)

      val selectionMap = MutableMap[String, Any]()
      populateListByType(input, resultList, featureResponseCache, buf)
      if(selectionMap.nonEmpty){
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get endpoint-analytics data for type: " + input.getTypeField +
        " And typeValue: " + input.getTypeValue + "  => " + e)
    } finally {
      closeConnections(conn)
    }

    buf
  }

  /**
   * Queries the cache and gets the data
   * @param featureName
   * @param sqlStr
   * @param conn
   * @param pstmt
   * @param featureResponseCache
   * @return
   */
  def getFromFeatureResponseCache(featureName: String, sqlStr: String, conn:Connection, pstmt: PreparedStatement,
                                  featureResponseCache: FeatureResponseCache): util.List[util.Map[String, AnyRef]] = {
    val cacheRequestObj: CacheRequestObject = new CacheRequestObject(featureName, sqlStr, this, conn, pstmt )
    val list: util.List[util.Map[String, AnyRef]] = featureResponseCache.get(cacheRequestObj)
    list
  }

  def populateListByType(input: EndPointAnalytics, resultList: util.List[util.Map[String, AnyRef]],
                         featureResponseCache: FeatureResponseCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    val outputBuf: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
    val typeValueSet: mutable.HashSet[String] = new mutable.HashSet[String]
    var typeField = input.getTypeField
    val typeValue = input.getTypeValue
    var isAutoMd5 = false
    if(typeField.equalsIgnoreCase("AUTO_MD5")){
      typeField = "MD5"
      isAutoMd5 = true
    }
    val lastSeenValue = { if(input.getLastSeenTypeValue == null) "" else input.getLastSeenTypeValue }
    val limit = input.getNumRows
    var found = false
    val loop = new Breaks
    loop.breakable {
      for (i <- 0 until resultList.size()) {
        val rs: util.Map[String, AnyRef] = resultList.get(i)
        val typeValueStr = featureResponseCache.getValueFromMap(rs, TaniumStats.TYPE_VALUE.toString)

        if (!typeValueSet.contains(typeValueStr)) {
            if(outputBuf.size >= limit){
              loop.break()
            }
            if ((lastSeenValue == null || lastSeenValue.isEmpty) ||
              (lastSeenValue != null && typeValueStr.equals(lastSeenValue))) {
              found = true
            }
            if(found && lastSeenValue != null && !typeValueStr.equals(lastSeenValue)){
              val selectionMap = MutableMap[String, Any]()
              selectionMap += typeField -> typeValueStr
              outputBuf += selectionMap
            }
            typeValueSet += typeValueStr
        }
      }
    }
    val selectionMap = MutableMap[String, Any]()
    selectionMap += "results" -> outputBuf
    val totalSet: mutable.HashSet[String] = new mutable.HashSet[String]
    for (i <- 0 until resultList.size()) {
      val rs: util.Map[String, AnyRef] = resultList.get(i)
      val typeValueStr = featureResponseCache.getValueFromMap(rs, TaniumStats.TYPE_VALUE.toString)
      totalSet += typeValueStr
    }

    selectionMap += "total" -> totalSet.size
    buf += selectionMap

  }

  def populateListByTypeValue(input: EndPointAnalytics,
                              resultList: util.List[util.Map[String, AnyRef]],
                              featureResponseCache: FeatureResponseCache, selectionMap: MutableMap[String, Any], buf: ListBuffer[MutableMap[String, Any]]) = {

    for (i <- 0 until resultList.size()) {
      val rs: util.Map[String, AnyRef] = resultList.get(i)
      val currentHosts = featureResponseCache.getValueFromMap(rs, TaniumStats.HOSTS_CURRENT.toString)

      var validRecord = true
      //check for Host
      if (input.getEntities != null && !input.getEntities.isEmpty && currentHosts != null) {
        var entityFound: Boolean = false
        val entities = input.getEntities
        for (i <- 0 until entities.size()) {
          if (entities.get(i) != null && entities.get(i).getHostName != null && currentHosts.contains(entities.get(i).getHostName)) {
            entityFound = true
          }
        }
        if (!entityFound) {
          validRecord = false
        }
      }

      //check if this is a row applicable to be in the result set. This depends on the check for host perfomed above.
      if (validRecord) {
        val typeValueStr = featureResponseCache.getValueFromMap(rs, TaniumStats.TYPE_VALUE.toString)
        val currentProcesses = featureResponseCache.getValueFromMap(rs, TaniumStats.PROCESSES_CURRENT.toString)
        val currentMd5s = featureResponseCache.getValueFromMap(rs, TaniumStats.MD5S_CURRENT.toString)
        val currentPorts = featureResponseCache.getValueFromMap(rs, TaniumStats.PORTS_CURRENT.toString)
        val currentPaths = featureResponseCache.getValueFromMap(rs, TaniumStats.PATHS_CURRENT.toString)
        val datesSeen = featureResponseCache.getValueFromMap(rs, TaniumStats.DATES_SEEN.toString)
        val dateTime = featureResponseCache.getValueFromMap(rs, TaniumStats.DATE_TIME.toString)
        val newlyObserved = featureResponseCache.getValueFromMap(rs, TaniumStats.NEWLY_OBSERVED.toString)
        val newlyObservedObj = new NoxDate(dateTime, {
          if (newlyObserved != null) true else false
        })

        val currentProcessesSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentProcesses)
        val currentMd5ssSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentMd5s)
        val currentPortsSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentPorts)
        val currentHostsSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentHosts)
        val currentPathsSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentPaths)
        val dateSeenSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(datesSeen)

        //processSet:
        val processSet = {
          if (selectionMap.contains("processes"))
            selectionMap("processes").asInstanceOf[mutable.HashSet[String]]
          else
            mutable.HashSet[String]()
        }
        processSet ++= currentProcessesSet

        //md5Set:
        val md5Set = {
          if (selectionMap.contains("md5s"))
            selectionMap("md5s").asInstanceOf[mutable.HashSet[String]]
          else
            mutable.HashSet[String]()
        }
        md5Set ++= currentMd5ssSet

        //port:
        val portSet = {
          if (selectionMap.contains("ports"))
            selectionMap("ports").asInstanceOf[mutable.HashSet[String]]
          else
            mutable.HashSet[String]()
        }
        portSet ++= currentPortsSet

        //host:
        val hostSet = {
          if (selectionMap.contains("hosts"))
            selectionMap("hosts").asInstanceOf[mutable.HashSet[String]]
          else
            mutable.HashSet[String]()
        }
        hostSet ++= currentHostsSet

        //path:
        val pathSet = {
          if (selectionMap.contains("paths"))
            selectionMap("paths").asInstanceOf[mutable.HashSet[String]]
          else
            mutable.HashSet[String]()
        }
        pathSet ++= currentPathsSet

        //dateSeen:
        val dateSet = {
          if (selectionMap.contains("datesSeen"))
            selectionMap("datesSeen").asInstanceOf[mutable.HashSet[String]]
          else
            mutable.HashSet[String]()
        }
        dateSet ++= dateSeenSet

        //nox tuple:
        val newlyObservedSet = {
          if (selectionMap.contains("nox"))
            selectionMap("nox").asInstanceOf[mutable.HashSet[NoxDate]]
          else
            mutable.HashSet[NoxDate]()
        }
        newlyObservedSet += newlyObservedObj

        selectionMap += "processes" -> processSet
        selectionMap += "md5s" -> md5Set
        selectionMap += "ports" -> portSet
        selectionMap += "hosts" -> hostSet
        selectionMap += "paths" -> pathSet
        selectionMap += "datesSeen" -> dateSeenSet
        selectionMap += "firstTimeSeen" -> dateSeenSet.toSeq.sorted.head
        selectionMap += "nox" -> newlyObservedSet
      }
    }

    if(selectionMap.contains("md5s")){
      val md5s = selectionMap("md5s").asInstanceOf[mutable.HashSet[String]]
      val md5whereClause = new StringBuffer()
      val limit = conf.getEndpointLimit
      var count = 0
      md5s.foreach{ md5 =>
        if (count < limit) {
          if (md5whereClause.length() > 0) {
            md5whereClause.append(" OR ").append(TaniumStats.TYPE_VALUE).append(" = ").append("'").append(md5).append("'")
          } else {
            md5whereClause.append(TaniumStats.TYPE_VALUE).append(" = ").append("'").append(md5).append("'")
          }
        }
        count += 1
      }
      var conn: Connection = null
      try {


        conn = getConnection(conf)
        // val likeOperatorString = SqlUtils.getEqualsOperatorString(processList, TaniumStats.TYPE_VALUE.toString)
        var sqlStr = " SELECT  " + TaniumStats.TYPE_VALUE +
          " FROM " + TaniumStats.getName(conf) + " WHERE " +
          TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
          " AND " + TaniumStats.TYPE + " = '" +  "MD5'" +
          " AND " + TaniumStats.KEYS_CURRENT + " IS NOT NULL "

        if(md5whereClause.length() > 0){
          sqlStr = sqlStr + " AND " + md5whereClause.toString
        }
        sqlStr =  sqlStr + " ORDER BY " + TaniumStats.DATES_SEEN + " DESC "

        val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, input.getStartTime)
        pstmt.setString(2, input.getEndTime)
        val rs = executeQuery(pstmt)
        while (rs.next()){
          val autoMd5Set = {
            if (selectionMap.contains("autoMd5s"))
              selectionMap("autoMd5s").asInstanceOf[mutable.HashSet[String]]
            else
              mutable.HashSet[String]()
          }
          autoMd5Set += rs.getString(TaniumStats.TYPE_VALUE.toString)
          selectionMap += "autoMd5s" -> autoMd5Set
        }

      } catch {
        case e: Exception => Logger.error("Failed to get endpoint-analytics data for type: " + input.getTypeField +
          " And typeValue: " + input.getTypeValue + "  => " + e)
      } finally {
        closeConnections(conn)
      }

    }
  }

  /**
   * Does all the work to somehow find he entityCard details for tanium-host
   * @param startTime
   * @param endTime
   * @param hostName
   */
  def getTaniumEntityCards(startTime: String, endTime: String, hostName: String, entityId: String, useEntityId: Boolean, riskScore: Double, modelId: Int, cache: FeatureServiceCache, buf: ListBuffer[MutableMap[String, Any]]) = {
    val md5List = if (useEntityId) {
      getNewMd5sForEntityId(startTime, endTime, entityId)
    } else {
      getNewMd5sForHost(startTime, endTime, hostName)
    }
    var conn: Connection = null
    try {
      conn = getConnection(conf)
      md5List.foreach { md5Map =>
        val md5 = {
          if (md5Map.contains("md5")) {
            md5Map("md5").asInstanceOf[String]
          } else {
            null
          }
        }

        var isAutoMd5 = false
        // Because we now have multiple endpoint models, it's possible to have entries for multiple models in
        // the map as we make repeated calls to getTaniumEntityCards().  We only want to process new results,
        // which will only have the md5 field in the map filled out.  So only if there is no value for
        // modelId do we process the element of buf
        val bufModelId = md5Map.getOrElse("modelId", -1)

        if(md5 != null && md5.nonEmpty && bufModelId == -1) {
          //now get all processes, paths, ports, groups and population info
          val sqlStr = " SELECT * " +
            " FROM " + TaniumStats.getName(conf) + " WHERE " +
            TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
            " AND " + TaniumStats.TYPE + " = 'MD5' " + " AND " + TaniumStats.TYPE_VALUE + " =  ? " +
            " ORDER BY " + TaniumStats.RISK_SCORE + " DESC " + " LIMIT 1 "

          val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
          pstmt.setString(1, startTime)
          pstmt.setString(2, endTime)
          pstmt.setString(3, md5.toString)
          val rs = executeQuery(pstmt)
          val rsMeta = rs.getMetaData
          while (rs.next()) {
            val typeValueStr = rs.getString(TaniumStats.TYPE_VALUE.toString)
            val keysNew = rs.getString(TaniumStats.KEYS_CURRENT.toString)
            val keysHistorical = rs.getString(TaniumStats.KEYS_HISTORICAL.toString)

            if (keysHistorical != null || keysNew != null) {
              isAutoMd5 = true
            }

            val currentHosts = rs.getString(TaniumStats.HOSTS_CURRENT.toString)
            val currentProcesses = rs.getString(TaniumStats.PROCESSES_CURRENT.toString)
            val currentMd5s = rs.getString(TaniumStats.MD5S_CURRENT.toString)
            val currentPorts = rs.getString(TaniumStats.PORTS_CURRENT.toString)
            val currentPaths = rs.getString(TaniumStats.PATHS_CURRENT.toString)
            val datesSeen = rs.getString(TaniumStats.DATES_SEEN.toString)
            val dateTime = rs.getString(TaniumStats.DATE_TIME.toString)
            val newlyObserved = rs.getString(TaniumStats.NEWLY_OBSERVED.toString)
            val newlyObservedObj = new NoxDate(dateTime, {
              if (newlyObserved != null) true else false
            })
            val list = getNewlyObserved(startTime, endTime, md5)

            val currentProcessesSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentProcesses)
            val currentMd5ssSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentMd5s)
            val currentPortsSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentPorts)
            val currentHostsSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentHosts)
            val currentPathsSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(currentPaths)
            val dateSeenSet: mutable.HashSet[String] = getSetFromPipeSeparatedString(datesSeen)
            //processSet:
            val processSet = {
              if (md5Map.contains("processes"))
                md5Map("processes").asInstanceOf[mutable.HashSet[String]]
              else
                mutable.HashSet[String]()
            }
            processSet ++= currentProcessesSet

            //md5Set:
            val md5Set = {
              if (md5Map.contains("md5s"))
                md5Map("md5s").asInstanceOf[mutable.HashSet[String]]
              else
                mutable.HashSet[String]()
            }
            md5Set ++= currentMd5ssSet

            //port:
            val portSet = {
              if (md5Map.contains("ports"))
                md5Map("ports").asInstanceOf[mutable.HashSet[String]]
              else
                mutable.HashSet[String]()
            }
            portSet ++= currentPortsSet

            //host:
            val hostSet = {
              if (md5Map.contains("hosts"))
                md5Map("hosts").asInstanceOf[mutable.HashSet[String]]
              else
                mutable.HashSet[String]()
            }
            hostSet ++= currentHostsSet

            //path:
            val pathSet = {
              if (md5Map.contains("paths"))
                md5Map("paths").asInstanceOf[mutable.HashSet[String]]
              else
                mutable.HashSet[String]()
            }
            pathSet ++= currentPathsSet

            //dateSeen:
            val dateSet = {
              if (md5Map.contains("datesSeen"))
                md5Map("datesSeen").asInstanceOf[mutable.HashSet[String]]
              else
                mutable.HashSet[String]()
            }
            dateSet ++= dateSeenSet

            //nox tuple:
            val newlyObservedSet = {
              if (md5Map.contains("newlyObserved"))
                md5Map("newlyObserved").asInstanceOf[mutable.HashSet[NoxDate]]
              else
                mutable.HashSet[NoxDate]()
            }
            newlyObservedSet += newlyObservedObj

            //nox tuple:
            val groupInfo = {
              if (md5Map.contains("groupInfo"))
                md5Map("groupInfo").asInstanceOf[ListBuffer[TaniumHostGroupInfo]]
              else
                new ListBuffer[TaniumHostGroupInfo]()
            }
            groupInfo ++= list

            md5Map += "processes" -> processSet
            md5Map += "md5s" -> md5Set
            md5Map += "ports" -> portSet
            md5Map += "hosts" -> hostSet
            md5Map += "paths" -> pathSet
            md5Map += "datesSeen" -> dateSeenSet
            var firstSeen = dateSeenSet.toSeq.sorted.head
            if (firstSeen == null) {
              firstSeen = ""
            }
            md5Map += "firstTimeSeen" -> firstSeen
            md5Map += "nox" -> newlyObservedSet
            md5Map += "groupInffo" -> groupInfo
            md5Map += "groupInfo" -> groupInfo
            md5Map += "isAutorun" -> isAutoMd5

            //behavior details:
            val secEventData = cache.getSecurityEventDataFromFeatureIdModelId(0, modelId)

            md5Map += "dateTime" -> dateTime
            md5Map += "modelId" -> secEventData.getModel
            md5Map += "eventId" -> secEventData.getSecurityEventTypeId
            md5Map += "featureDesc" -> secEventData.getEventDescription
            md5Map += "killchainId" -> secEventData.getKillchainId
            md5Map += "securityEventType" -> secEventData.getEventType
            md5Map += "cardId" -> secEventData.getCardId
            md5Map += "hostName" -> hostName
            md5Map += "sourceIp" -> null
            md5Map += "destinationIp" -> null
            md5Map += "interval" -> null
            md5Map += "sourceUserName" -> null
            md5Map += "riskScore" -> riskScore
            md5Map += "isDaily" -> true // endpoint/tanium job runs daily

            if (Constants.EndpointLocalModelTuple._1.equals(modelId)) {
              // If we are looking for local results, all newly observed events count as locally observed (on some machine)
              buf += md5Map
            } else if (Constants.EndpointGlobalModelTuple._1.equals(modelId) && (firstSeen.compareTo(startTime) >= 0 && firstSeen.compareTo(endTime) < 0)) {
              // If we are looking for global results, the first seen date must be in the range passed in
              buf += md5Map
            }
          }
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
    } finally {
      closeConnections(conn)
    }
  }

  /**
   * Get the group info from the tanium stats table. This comes from the newly_observed column.  But
   * the problem is that not all rows have non-null values for this, and also if the date range
   * covers multiple days, we want the most recent day that has a non-null value.  The general
   * row that we get in getTaniumEntityCards() may not meet these criteria, so we need to do
   * another query.
   *
   * This is inefficient, of course, so we may want to look at modifying the tanium stats table
   * or building some other way to get this more efficiently.
   *
   * @param startTime start of the date range we should look within
   * @param endTime end of the date range we should look within
   * @param md5 md5 we should look for
   */
  def getNewlyObserved(startTime: String, endTime: String, md5: String): ListBuffer[TaniumHostGroupInfo] = {
    val list: ListBuffer[TaniumHostGroupInfo] = new ListBuffer[TaniumHostGroupInfo]()
    var conn: Connection = null
    try {
      conn = getConnection(conf)
      val sqlStr = " SELECT " + TaniumStats.NEWLY_OBSERVED +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND " + TaniumStats.TYPE + " = 'MD5' " + " AND " + TaniumStats.TYPE_VALUE + " =  ? " +
        " AND " + TaniumStats.NEWLY_OBSERVED + " IS NOT NULL" +
        " ORDER BY " + TaniumStats.DATE_TIME + " DESC " + " LIMIT 1 "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setString(3, md5)
      val rs = executeQuery(pstmt)
      var newlyObserved = ""
      while (rs.next()) {
        newlyObserved = rs.getString(TaniumStats.NEWLY_OBSERVED.toString)
      }
      getHostGroupInfoList(newlyObserved, list)
    } catch {
      case e: Exception => Logger.error("Failed to get newly observed for tanium entity cards   => " + e)
    } finally {
      closeConnections(conn)
    }

    list
  }

  /**
   * method to check if the md5 is also an AutoRun
   * @param startTime
   * @param endTime
   * @param md5
   * @return
   */
  def checkIfMd5IsAuto(startTime: String, endTime: String, md5: String) : Boolean = {
    var isAutorun = false
    var conn: Connection = null
    try {
      conn = getConnection(conf)
      val sqlStr = " SELECT * " +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND " + TaniumStats.TYPE + " = 'AUTORUN' " + " AND " + TaniumStats.TYPE_VALUE + " =  ? " +
        " ORDER BY " + TaniumStats.RISK_SCORE + " DESC " + " LIMIT 1 "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setString(3, md5)
      val rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        isAutorun = true
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
    } finally {
      closeConnections(conn)
    }
    isAutorun
  }

  /**
   * For a given entity, returns the list of newly observed md5s seen on that entity during
   * the time range given.  It is expected that the entity id will be a host entity.
   *
   * @param startTime start time for which data will be returned
   * @param endTime end time for which data will be returned
   * @param entityId entity for which data will be returned
   * @return A list of maps containing md5s seen on the given entity
   */
  def getNewMd5sForEntityId(startTime: String, endTime: String, entityId: String) = {
    var buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    if (entityId != null && !entityId.isEmpty) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)
        val sqlStr = " SELECT " + TaniumStats.MD5S_NEW +
          " FROM " + TaniumStats.getName(conf) + " WHERE " +
          TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
          " AND " + TaniumStats.TYPE + " = 'HOST' " + " AND " + TaniumStats.TYPE_VALUE + " =  ? " +
          " ORDER BY " + TaniumStats.RISK_SCORE + " DESC "

        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, entityId)
        rs = executeQuery(pstmt)
        var count: Integer = 0
        while (rs.next() && count < MAX_MD5_FOR_HOST) {
          // We check buf size to limit the number of results to prevent us from
          // returning thousands of results which can happen when the date range is wide.
          // May be a candidate for paging at some point.
          val md5 = rs.getString(TaniumStats.MD5S_NEW.toString)
          val md5Set = getSetFromPipeSeparatedString(md5)
          md5Set.foreach { md5 =>
            if (count < MAX_MD5_FOR_HOST) {
              val selectionMap = MutableMap[String, Any]()
              selectionMap += "md5" -> md5
              buf += selectionMap
              count += 1
            }
          }
        }
      } catch {
        case e: Exception => Logger.error("Failed to get MD5s => " + e)
      } finally {
        closeConnections(conn, pstmt, rs)
      }
    }
    buf
  }
  /**
   * For a given host, returns the list of newly observed md5s seen on that host
   * @param startTime
   * @param endTime
   * @param hostName
   * @return
   */
  def getNewMd5sForHost(startTime: String, endTime: String, hostName: String) = {
    var buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    if(hostName != null && hostName.nonEmpty) {
      try {
        conn = getConnection(conf)
        val sqlStr = " SELECT " + TaniumStats.MD5S_NEW +
          " FROM " + TaniumStats.getName(conf) + " WHERE " +
          TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
          " AND " + TaniumStats.TYPE + " = 'HOST' " + " AND " + TaniumStats.HOST_NAME + " =  ? " +
          " ORDER BY " + TaniumStats.RISK_SCORE + " DESC "

        val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, hostName)
        val rs = executeQuery(pstmt)
        var count: Integer = 0
        while (rs.next() && count < MAX_MD5_FOR_HOST) {
          // We check buf size to limit the number of results to prevent us from
          // returning thousands of results which can happen when the date range is wide.
          // May be a candidate for paging at some point.
          val md5 = rs.getString(TaniumStats.MD5S_NEW.toString)
          val md5Set = getSetFromPipeSeparatedString(md5)
          md5Set.foreach { md5 =>
            if (count < MAX_MD5_FOR_HOST) {
              val selectionMap = MutableMap[String, Any]()
              selectionMap += "md5" -> md5
              buf += selectionMap
              count += 1
            }
          }
        }
      } catch {
        case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
      } finally {
        closeConnections(conn)
      }
    }
    buf
  }

  /**
   * For a given host, returns the RISK_SCORE
   * This method is deprecated in the new entity fusion work, but remains here to support
   * legacy code for the moment.
   *
   * @param startTime
   * @param endTime
   * @param hostName
   * @return
   */
  def getRiskScoreForHost(startTime: String, endTime: String, hostName: String) = {
    val buf: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
    val selectionMap = MutableMap[String, Any]()
    var risk = 0d
    var globalRisk = 0d
    var conn: Connection = null
    try {
      conn = getConnection(conf)
      val sqlStr = " SELECT " + " MAX ( " + TaniumStats.RISK_SCORE  + " ) AS RISK, " +
        " MAX ( " + TaniumStats.RISK_SCORE_GLOBAL + " ) as GLOBAL_RISK " +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND " + TaniumStats.TYPE + " = 'HOST' " + " AND " + TaniumStats.HOST_NAME + " =  ? " +
        " ORDER BY RISK "  + " DESC " + " LIMIT 1 "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setString(3, hostName)
      val rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        val riskScore = rs.getString("RISK")
        val globalRiskScore = rs.getString("GLOBAL_RISK")
        try {
          // Risk scores may be greater than 1 - if so, reduce it to 1.
          if(riskScore != null) {
            risk = riskScore.toDouble
            risk = if (risk > 1) 1 else risk
          }
          if(globalRiskScore != null) {
            globalRisk = globalRiskScore.toDouble
            globalRisk = if (globalRisk > 1) 1 else globalRisk
          }
        } catch {
          case e: Exception => Logger.error("Failed to get tanium riskscore  => " + e)
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
    } finally {
      closeConnections(conn)
    }
    selectionMap += "riskScore" -> risk
    selectionMap += "globalRiskScore" -> globalRisk
    buf += selectionMap
  }

  /**
   * For a given entity id, returns the maximum risk score and global risk score from endpoint data
   *
   * @param startTime start of time range to search
   * @param endTime end of time range to search
   * @param entityId the entity to search for
   * @return a list containing a map with the maximum local and global risk scores
   */
  def getRiskScoreForEntity(startTime: String, endTime: String, entityId: String) = {
    val buf: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
    val selectionMap = MutableMap[String, Any]()
    var risk = 0d
    var globalRisk = 0d
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sqlStr = " SELECT " + " MAX ( " + TaniumStats.RISK_SCORE  + " ) AS RISK, " +
        " MAX ( " + TaniumStats.RISK_SCORE_GLOBAL + " ) as GLOBAL_RISK " +
        " FROM " + TaniumStats.getName(conf) + " WHERE " +
        TaniumStats.DATE_TIME + " >=  ? " + " AND " + TaniumStats.DATE_TIME + " < ? " +
        " AND " + TaniumStats.TYPE + " = 'HOST' " + " AND " + TaniumStats.TYPE_VALUE + " =  ? " +
        " ORDER BY RISK "  + " DESC " + " LIMIT 1 "

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setString(3, entityId)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val riskScore = rs.getString("RISK")
        val globalRiskScore = rs.getString("GLOBAL_RISK")
        try {
          // Risk scores may be greater than 1 - if so, reduce it to 1.
          if(riskScore != null) {
            risk = riskScore.toDouble
            risk = if (risk > 1) 1 else risk
          }
          if(globalRiskScore != null) {
            globalRisk = globalRiskScore.toDouble
            globalRisk = if (globalRisk > 1) 1 else globalRisk
          }
        } catch {
          case e: Exception => Logger.error("Failed to get tanium riskscore  => " + e)
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    selectionMap += "riskScore" -> risk
    selectionMap += "globalRiskScore" -> globalRisk
    buf += selectionMap
  }



  /**
   * returns the first time-stamp in the endpoint stats.
   * @return
   */
  def getFirstEndpointDatapoint() = {
    val buf: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
    val selectionMap = MutableMap[String, Any]()
    var conn: Connection = null
    try {
      conn = getConnection(conf)
      val sqlStr = " SELECT " + TaniumStats.DATE_TIME +
        " FROM " + TaniumStats.getName(conf) +
        " ORDER BY " + TaniumStats.DATE_TIME + " ASC " + " LIMIT 1 "

      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      val rs = executeQuery(pstmt)
      while (rs.next()) {
        val dateTime = rs.getString(TaniumStats.DATE_TIME.toString)
        if(dateTime != null && dateTime.nonEmpty) {
          selectionMap += "firstTimeSeen" -> dateTime.replace("'", "").replace("\"", "")
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Tanium Entity Cards   => " + e)
    } finally {
      closeConnections(conn)
    }
    buf += selectionMap
  }

  private def getSetFromPipeSeparatedString(str: String): mutable.HashSet[String] = {
    val set: mutable.HashSet[String] = new mutable.HashSet[String]
    if(str != null && str.nonEmpty) {
      val arr = str.split("\\|")
      arr.foreach { s =>
        set += s
      }
    }
    set
  }


  private def getHostGroupInfoList(newlyObservedString: String, list: ListBuffer[TaniumHostGroupInfo] ) = {
    if(newlyObservedString != null && newlyObservedString.nonEmpty) {
      val arr = newlyObservedString.split("\\|")
      arr.foreach { s =>
        val groupName = s.substring(0, s.indexOf(":"))
        val hostSeen = s.substring(s.indexOf(":")+1, s.indexOf("/"))
        val totalHost = s.substring(s.indexOf("/") + 1)
        list += new TaniumHostGroupInfo(groupName , hostSeen.toInt , totalHost.toInt)
      }
    }
  }
}

case class HostTrackers(dateTime: String,
                                 var allMd5HostSet: mutable.HashSet[String], var allMd5HostCount: Long,
                                 var newMd5HostSet: mutable.HashSet[String], var newMd5HostCount: Long,
                       var allAutoMd5HostSet: mutable.HashSet[String],var allAutoMd5HostCount: Long,
                                 var newAutoMd5HostSet: mutable.HashSet[String], var newAutoMd5HostCount: Long,
                       var allPortSet: mutable.HashSet[String], var allPortHostCount: Long,
                                 var newPortSet: mutable.HashSet[String], var newPortHostCount: Long) {
}

case class HostPropertiesTracker(dateTime: String, typeString: String,
                                 var allMd5Set: mutable.HashSet[String], var newMd5Set: mutable.HashSet[String],
                                 var allMd5Count: Long, var newMd5Count: Long,
                                 var allAutoMd5Set: mutable.HashSet[String], var newAutoMd55Set: mutable.HashSet[String],
                                 var allAutoMd5Count: Long, var newAutoMd5Count: Long,
                                 var allPortSet: mutable.HashSet[String], var newPortSet: mutable.HashSet[String],
                                 var allPortCount: Long, var newPortCount: Long, var riskScore: Double) {
}

case class TaniumEntityCard(val field: String, values: ListBuffer[GlobalTracker]){

}

case class GlobalTracker(val name: String, val isGlobal: Boolean){

}

case class NoxDate(val dateTime: String, val isNewlyObserved: Boolean){

}

case class TaniumHostGroupInfo(val groupName: String, val hostSeen: Int, val totalHosts: Int){

}