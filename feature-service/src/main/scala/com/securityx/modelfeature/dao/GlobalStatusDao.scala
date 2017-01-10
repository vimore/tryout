package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.FeatureServiceConfiguration.RiskRangeConfiguration
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, GlobalStatusConfiguration}
import com.securityx.modelfeature.queryengine.QueryGenerator
import com.securityx.modelfeature.utils._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.mutable.{ListBuffer, Map => MutableMap}

class GlobalStatusDao(conf: FeatureServiceConfiguration) extends BaseDao(conf){

  var globalStatusConfig: GlobalStatusConfiguration = conf.getGlobalStatus

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[GlobalStatusDao])
  private val noxDao = new NewlyObservedXDao(conf)
  private val cloudChamber = new CloudChamberDao(conf)
  private val dataIngestionDao = new DataIngestionDao(conf)


  /**
   * Returns the count of total Users and Hosts observed in the system for given time period if specified, otherwise till current date_time .
   *
   * @param startDateTime String optional
   * @param endDateTime String optional
   * @return
   */
  def getTotalTrafficCounts(startDateTime:String = "", endDateTime:String = "") = {
    val selectionMap = MutableMap[String, Any]()
    selectionMap += "users" -> 0
    selectionMap += "systems" -> 0
    var conn: Connection = null

    var userPs: PreparedStatement = null
    var userRs: ResultSet = null
    var userCount = 0
    var hostPs: PreparedStatement = null
    var hostRs: ResultSet = null
    var hostCount = 0

    try {
      conn = getConnection(conf)

      // We do separate queries to get user and host numbers so that we can add different exclusion terms to
      // the different queries.
      var userSql = "select count(distinct upper(" + GlobalFeatures.FIELD_VALUE + ")) from " + GlobalFeatures.getName(conf) +
        " where " + GlobalFeatures.FIELD_NAME + " = 'destinationUserName'"

      var whereStr = ""
      if(startDateTime != null && startDateTime.nonEmpty){
        whereStr = " AND " + GlobalFeatures.DATE_TIME + " >= ?"
      }

      // If End Date Time is specified
      if(endDateTime != null && endDateTime.nonEmpty) {
        whereStr += " AND " + GlobalFeatures.DATE_TIME + " <= ?"
      }
      userSql += whereStr

      // Add terms to exclude users
      var userExclusion: String = ""
      for (term <- globalStatusConfig.getExcludeUserLikeTermList) {
        if (userExclusion != "") {
          userExclusion = userExclusion.concat(" AND ")
        }
        userExclusion = userExclusion.concat(GlobalFeatures.FIELD_VALUE + " not like '" + term + "'")
      }

      if (userExclusion != "") {
        userSql += " AND (" + userExclusion + ")"
      }

      userPs = getPreparedStatement(conn, userSql)
      var userPsIndex = 1
      if(startDateTime != null && startDateTime.nonEmpty){
        userPs.setString(userPsIndex, startDateTime)
        userPsIndex += 1
      }
      if(endDateTime != null && endDateTime.nonEmpty){
        userPs.setString(userPsIndex, endDateTime)
      }
      Logger.info("getTotalTrafficCounts() user sql [" + userSql + "] params [" + startDateTime + ", " + endDateTime + "]")
      val beforeUserQuery = System.currentTimeMillis()
      userRs = executeQuery(userPs)
      val elapsedUserTime = System.currentTimeMillis() - beforeUserQuery
      if (userRs.next()) {
        userCount = userRs.getInt(1)
        Logger.info("getTotalTrafficCounts() results from user sql [" + userCount + "] elapsed time [" + elapsedUserTime + "]")
      } else {
        Logger.info("getTotalTrafficCounts() no results from user sql. elapsed time [" + elapsedUserTime + "]")
      }
      selectionMap += "users" -> userCount

      var hostSql = "select count(distinct upper(" + GlobalFeatures.FIELD_VALUE + ")) from " + GlobalFeatures.getName(conf) +
        " where " + GlobalFeatures.FIELD_NAME + " = 'hostName'"

      // We use the same where clause, since the inputs haven't changed
      hostSql += whereStr

      // Add terms to exclude hosts
      var hostExclusion: String = ""
      for (term <- globalStatusConfig.getExcludeHostLikeTermList) {
        if (hostExclusion != "") {
          hostExclusion += " AND "
        }
        hostExclusion += GlobalFeatures.FIELD_VALUE + " not like '" + term + "'"
      }

      if (hostExclusion != "") {
        hostSql += " AND (" + hostExclusion + ")"
      }

      hostPs = getPreparedStatement(conn, hostSql)
      var hostPsIndex = 1
      if(startDateTime != null && startDateTime.nonEmpty){
        hostPs.setString(hostPsIndex, startDateTime)
        hostPsIndex += 1
      }
      if(endDateTime != null && endDateTime.nonEmpty){
        hostPs.setString(hostPsIndex, endDateTime)
      }
      Logger.info("getTotalTrafficCounts() host sql [" + hostSql + "] params [" + startDateTime + ", " + endDateTime + "]")
      val beforeHostQuery = System.currentTimeMillis()
      hostRs = executeQuery(hostPs)
      val elapsedHostTime = System.currentTimeMillis() - beforeHostQuery
      if (hostRs.next()) {
        hostCount = hostRs.getInt(1)
        Logger.info("getTotalTrafficCounts() results from host sql [" + hostCount + "] elapsed time [" + elapsedHostTime + "]")
      } else {
        Logger.info("getTotalTrafficCounts() no results from host sql. elapsed time [" + elapsedHostTime + "]")
      }
      selectionMap += "systems" -> hostCount
    } catch {
    case e: Exception => Logger.error("Failed to get Global Counts  => "+e.getMessage , e)
  } finally {
    closeConnections(conn)
  }
    selectionMap
  }

  /**
   * Returns the list of distinct entities in the system and count of entities based on their risk types (high,medium,low)
   *
   * @param input QueryJson Filters to be applied
   * @return
   */
  def getEntityCounts(input: QueryJson, cache: FeatureServiceCache, conf: FeatureServiceConfiguration) = {
    val entityMap = MutableMap[String, Any]()
    val riskRangeConf: RiskRangeConfiguration = conf.getRiskRanges

    val allEntities = getEntities(input, cache)

    var numLowRiskEntities: Int = 0
    var numLowMediumEntities: Int = 0
    var numLowHighEntities: Int = 0

    if (allEntities != null & allEntities.nonEmpty) {
      val i = 0
      for (i <- 0 until allEntities.size) {

        val entity: util.Map[String, Any] = allEntities.get(i)
        val risk = entity.get("riskScore").asInstanceOf[Double]

        if (risk >= riskRangeConf.getLowRisk.get("min") && risk < riskRangeConf.getLowRisk.get("max")) {
          numLowRiskEntities += 1
        } else if (risk >= riskRangeConf.getMediumRisk.get("min") && risk < riskRangeConf.getMediumRisk.get("max")) {
          numLowMediumEntities += 1
        } else {
          numLowHighEntities += 1
        }
      }
    }

    entityMap.put("entities", allEntities)
    entityMap.put("totalEntityCount", allEntities.size)
    entityMap.put("lowRiskEntityCount", numLowRiskEntities)
    entityMap.put("mediumRiskEntityCount", numLowMediumEntities)
    entityMap.put("highRiskEntityCount", numLowHighEntities)

    entityMap

  }

  /**
   * Returns the list of distinct entities in the system
   *
   * @param inputJson QueryJson Filters to be applied
   * @param cache FeatureServiceCache
   * @return
   */
  def getEntities(inputJson: QueryJson, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var count = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      var sqlStr = "SELECT DISTINCT " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.USER_NAME + ", " + " MAX(" + EntityThreat.RISK_SCORE + ") AS MAX_RISK " +
        " FROM " + EntityThreat.getName(conf)

      val groupbyString: String = " GROUP BY " + EntityThreat.IP_ADDRESS + ", " + EntityThreat.MAC_ADDRESS + ", " +
        EntityThreat.HOST_NAME + ", " + EntityThreat.USER_NAME

      val q: QueryGenerator = new QueryGenerator
      val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
      val predicateString = q.getSqlPredicateString(inputJson, pstmtList, groupbyString, null, cache, isSearchQuery = false, conf.getFixNullValue)

      sqlStr = sqlStr + predicateString

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      populatePreparedStatementParams(pstmt, pstmtList)

      Logger.info("getEntities() executing query [" + sqlStr + "]")
      val beforeQuery = System.currentTimeMillis()
      rs = executeQuery(pstmt)
      val elapsedTime = System.currentTimeMillis() - beforeQuery
      while (rs.next()) {
        var selectionMap = MutableMap[String, Any]()
        //TODO replacing for ' ' with '' is a hack which should be removed
        selectionMap += "ipAddress" ->  MiscUtils.stringReplaceNullValue(rs.getString("IP_ADDRESS"), conf)
        selectionMap += "macAddress" ->  MiscUtils.stringReplaceNullValue(rs.getString("MAC_ADDRESS"), conf)
        selectionMap += "hostName" ->  MiscUtils.stringReplaceNullValue(rs.getString("HOST_NAME"), conf)
        selectionMap += "userName" ->  MiscUtils.stringReplaceNullValue(rs.getString("USER_NAME"), conf)
        selectionMap += "riskScore" -> rs.getDouble("MAX_RISK")
        buf += selectionMap
      }
      Logger.info("getEntities() query took [" + elapsedTime + "] ms")
    } catch {
      case e: Exception => Logger.error("Failed to get Global Entities  => " + e)
    } finally {
      closeConnections(conn)
    }

    buf
  }

  /**
   * Returns the count of anomalous events observed for given time range.
   * Threat events having risk score between 0.0 and 0.7 are called Anomalies and
   * Threat events having risk score greater than 0.7 are called Threats.
   *
   * @param startDateTime String
   * @param endDateTime String
   * @param lowerRiskBound Double
   * @param upperRiskBound Double
   * @return
   */
  def getAnomaliesCounts(startDateTime:String, endDateTime:String, lowerRiskBound: Double, upperRiskBound: Double) = {
    var count = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      var sqlStr = "Select COUNT(" + EntityThreat.RISK_SCORE + ") AS RISK_COUNT" +
                   " FROM " + EntityThreat.getName(conf) +
                   " WHERE " + EntityThreat.DATE_TIME + "  >=  ?  AND " + EntityThreat.DATE_TIME + "  <  ? " +
                   " AND ( " + EntityThreat.RISK_SCORE + " > ? ) AND ( " + EntityThreat.RISK_SCORE + " <= ? ) "

      val modelsToIgnore = conf.getSuppressStatisticsForModelsList
      var suppressModelClause = ""
      // Note: Since we're constructing a string that will be part of our query, we should be concerned about
      // possible SQL injection attacks.  In this case, we do not need to worry, since the modelsToIgnore
      // list is a list of integers.
      if (modelsToIgnore.size() > 0) {
        if (modelsToIgnore.size() == 1) {
          suppressModelClause = " AND " + EntityThreat.MODEL_ID + " != " + modelsToIgnore.get(0)
        } else {
          var modelList = ""
          var first = true
          for (modelId <- modelsToIgnore) {
            if (first) {
              first = false
            } else {
              modelList = modelList + ", "
            }
            modelList = modelList + modelId
          }
          suppressModelClause = " AND " + EntityThreat.MODEL_ID + " NOT IN (" + modelList + ")"
        }
        sqlStr = sqlStr + suppressModelClause
      }

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startDateTime)
      pstmt.setString(2, endDateTime)
      pstmt.setDouble(3, lowerRiskBound)
      pstmt.setDouble(4, upperRiskBound)

      Logger.info("getAnomaliesCounts() executing query [" + sqlStr + "] with parameters [" + startDateTime + ", " + endDateTime + ", " + lowerRiskBound + ", " + upperRiskBound + "]")
      val beforeQuery = System.currentTimeMillis()
      rs = executeQuery(pstmt)
      val elapsedTime = System.currentTimeMillis() - beforeQuery
      if (rs.next()) {
        count = rs.getInt("RISK_COUNT")
        Logger.info("getAnomaliesCounts() Got result [" + count + "] from query, query took [" + elapsedTime + "] ms")
      } else {
        Logger.info("getAnomaliesCounts() Got no results from query, query took [" + elapsedTime + "] ms")
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Global Counts  => " + e, e)
    } finally {
      closeConnections(conn)
    }
    count
  }
  val MIN = "min"
  val MAX = "max"
  /**
   * Returns the various statistics (like total/new hosts and users, threats, data ingestion, cloud chamber status etc)
   * present in the system for given period of time.
   *
   * @param startTime String
   * @param endTime String
   * @param cache FeatureServiceCache
   * @return
   */
  def getStatistics(startTime:String, endTime:String, cache: FeatureServiceCache) = {
    val statisticsMap = MutableMap[String, Any]()
    val riskRangeConf: RiskRangeConfiguration = conf.getRiskRanges
    // Threats are anomalous events having risk score more than 0.7
    val threatsCount = getAnomaliesCounts(startTime, endTime, riskRangeConf.getHighRisk.get(MIN), riskRangeConf.getHighRisk.get(MAX))
    // Anomalies are anomalous events having risk score between 0 and 0.7
    val anomaliesCount = getAnomaliesCounts(startTime, endTime,riskRangeConf.getMediumRisk.get(MIN), riskRangeConf.getMediumRisk.get(MAX))
    // Total Hosts and Users observed till specified End Date Time
    val totalTrafficCount = getTotalTrafficCounts("",endTime)
    // Newly observed Hosts and Users
    val noxs = noxDao.getNOX(startTime, endTime, cache)

    var newUsersCount:Any = 0
    var newHostsCount:Any = 0
    val webFieldMap = cache.getWebSummaryFieldMap

    for(nox <- noxs) {
      if(nox.get("fieldName").toString.contains(webFieldMap.get("sourceNameOrIp"))){
        newHostsCount = nox.get("count")
      } else if(nox.get("fieldName").toString.contains(webFieldMap.get("sourceUserName"))){
        newUsersCount = nox.get("count")
      }
    }

    // Users Stats
    val usersMap = MutableMap[String, Any]()
    usersMap += "total" -> totalTrafficCount.get("users")
    usersMap += "newlyObserved" -> newUsersCount

    // Hosts Stats
    val hostsMap = MutableMap[String, Any]()
    hostsMap += "total" -> totalTrafficCount.get("systems")
    hostsMap += "newlyObserved" -> newHostsCount

    // Threats Stats
    val threatsMap = MutableMap[String, Any]()
    threatsMap += "total" -> threatsCount

    // Anomalies Stats
    val anomaliesMap = MutableMap[String, Any]()
    anomaliesMap += "total" -> anomaliesCount

    // Cloud Chamber Stats
    val cloudChamberMap = MutableMap[String, Any]()
    cloudChamberMap += "status" -> cloudChamber.getCloudChamberStatus()

    // Data Ingestion Stats till given end date
    val dataIngestionMap = MutableMap[String, Any]()
    dataIngestionDao.populateDataIngestionMap(startTime, endTime, dataIngestionMap )


    statisticsMap += "users" -> usersMap
    statisticsMap += "hosts" -> hostsMap
    statisticsMap += "threats" -> threatsMap
    statisticsMap += "anomalies" -> anomaliesMap
    statisticsMap += "cloudChamber" -> cloudChamberMap
    statisticsMap += "dataIngestion" -> dataIngestionMap

    statisticsMap
  }


  /**
   * Returns the list of N risky items like hosts, users in given period of time.
   *
   * @param startTime String
   * @param endTime String
   * @param topN Integer
   * @param order String
   * @return
   */
  def getRiskyHostsAndUsers(startTime:String, endTime:String, topN: Integer, order: String) = {

    val riskyItemsMap = MutableMap[String, Any]()
    // For fetching riskiest hosts and users among all, set upperRiskScore to 1.0 and set lowerRiskScore to 0.0
    val lowerRiskValue = 0.00001
    val upperRiskValue = 1.0

    val riskyHostsMap = getRiskyItems(startTime, endTime, lowerRiskValue, upperRiskValue, EntityThreat.HOST_NAME.toString, topN, order)
    val riskyUsersMap = getRiskyItems(startTime, endTime, lowerRiskValue, upperRiskValue, EntityThreat.USER_NAME.toString, topN, order)

    riskyItemsMap += "hosts" -> riskyHostsMap
    riskyItemsMap += "users" -> riskyUsersMap

    riskyItemsMap
  }

  /**
   * Returns the risky items (like hosts, users, etc ) within the specified risk score range and for given time period.
   *
   * @param startDateTime String
   * @param endDateTime String
   * @param lowerRiskBound Double
   * @param upperRiskBound Double
   * @param sortOrder String
   * @return
   */
  def getRiskyItems(startDateTime:String, endDateTime:String, lowerRiskBound: Double, upperRiskBound: Double,
                    fieldName: String, numItems: Integer, sortOrder: String) = {

    val riskyItems = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      val sqlStr = "SELECT DISTINCT " + fieldName + ", MAX(" + EntityThreat.RISK_SCORE + ") AS RISK " +
        " FROM " + EntityThreat.getName(conf) +
        " WHERE " + EntityThreat.DATE_TIME + " >=  ? AND " + EntityThreat.DATE_TIME + " <  ? " +
        " AND ( " + EntityThreat.RISK_SCORE + " > ? ) AND ( " + EntityThreat.RISK_SCORE + " <= ? )" +
        " AND ( " + fieldName + " IS NOT NULL ) AND ( " + fieldName + " != '--' ) AND ( " + fieldName + " != '"+conf.getFixNullValue.getNullValue+"' )" +
        " AND " + EntityThreat.MODEL_ID + " != " + Constants.BeaconModelTuple._1 +
        " GROUP BY " + fieldName +
        " ORDER BY RISK " + sortOrder +
        " LIMIT ? "

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startDateTime)
      pstmt.setString(2, endDateTime)
      pstmt.setDouble(3, lowerRiskBound)
      pstmt.setDouble(4, upperRiskBound)
      pstmt.setInt(5, numItems)

      Logger.info("getRiskyItems() executing query [" + sqlStr + "] " +
        "params [" + startDateTime + ", " + endDateTime + ", " + lowerRiskBound + ", " + upperRiskBound + ", " + numItems + "]")
      val beforeQuery = System.currentTimeMillis()
      rs = executeQuery(pstmt)
      val elapsedTime = System.currentTimeMillis() - beforeQuery
      Logger.info("getRiskyItems() query took [" + elapsedTime + "] ms")

      while (rs.next()) {
        val itemMap = MutableMap[String, Any]()
        val riskScore = rs.getDouble("RISK")

        var fieldNameVal = rs.getString(fieldName)
        if (conf.getFixNullValue.isEnabled)
          fieldNameVal = fieldNameVal.replaceAll("^"+conf.getFixNullValue.getNullValue+"$", "")

        itemMap += MiscUtils.underscoreToCamel(fieldName.toLowerCase()) -> fieldNameVal
        itemMap += MiscUtils.underscoreToCamel(EntityThreat.RISK_SCORE.toString.toLowerCase()) -> riskScore

        riskyItems += itemMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Risky " + fieldName + "Items  => " + e)
    } finally {
      closeConnections(conn)
    }

    riskyItems
  }

  /**
   * Returns a map keyed by string naming a feature that needs to report on whether it is enabled on a given
   * installation.  Values are expected to be boolean in general, but could include any information useful
   * to the UI in deciding whether or how to display information for that feature.
   *
   * The result is stored and will only be recalculated once every enabledCheckDuration milliseconds.  Calls
   * that are made within that time will return the precalculated result.
   *
   * @return
   */
  def getEnabled = {
    var enabled = Map ("PAN" -> true)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null

    try {
      // Check to see if we have any Palo Alto Networks info
      val sqlStr = "select count(*) as PAN_COUNT from ent_host_props where category like 'PAN%'"
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      Logger.info("getEnabled() executing query [" + sqlStr + "]")
      val beforeQuery = System.currentTimeMillis()
      rs = executeQuery(pstmt)
      val elapsedTime = System.currentTimeMillis() - beforeQuery
      if (rs.next()) {
        val count = rs.getInt("PAN_COUNT")
        if (count == 0) {
          enabled += ("PAN" -> false)
        }
        Logger.info("getEnabled() count [" + count + "] elapsed time [" + elapsedTime + "]")
      } else {
        Logger.info("getEnabled() got no results, elapsed time [" + elapsedTime + "]")
      }
    } catch {
      case e: Exception => Logger.error("Failed to get enabled map " + e)
    } finally {
      closeConnections(conn)
    }

    enabled
  }

}
